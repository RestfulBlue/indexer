package org.indexer.index

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.indexer.analyze.WhiteSpaceAnalyzer
import org.indexer.document.Source
import org.indexer.read.AtomicIndexReader
import org.indexer.read.IndexReader
import org.indexer.read.extract.DefaultDocumentExtractor
import org.indexer.read.filter.ANDFilter
import org.indexer.read.filter.TermFilter
import org.indexer.source.CompositeTermSource
import org.indexer.source.FileInput
import org.indexer.tokenization.NOOPTokenizer
import org.indexer.tokenization.SubstringTokenizer
import org.indexer.write.AtomicIndexWriter
import java.util.concurrent.CountDownLatch
import kotlin.test.Test
import kotlin.test.assertEquals


class RAMIndexIT {


    @Test
    fun testFull() {

        runBlocking {
            // create ram index
            val index = RAMIndex()
            // create writer
            val writer = AtomicIndexWriter(index)

            // index files with info about cattles
            val cattleIndexing = GlobalScope.launch {
                writer.inTransaction {
                    index(
                        CompositeTermSource(
                            Source("file", "cattle.txt"),
                            // file source
                            FileInput(getPathForResource("cattle.txt"), 1024),
                            // analyzer which split input by space
                            WhiteSpaceAnalyzer(),
                            // tokenizer which do not modify source input
                            NOOPTokenizer()
                        )
                    )
                }
            }

            // index files with information about dogs
            val dogIndexing = GlobalScope.launch {
                writer.inTransaction {
                    index(
                        CompositeTermSource(
                            Source("file", "dog.txt"),
                            // file source
                            FileInput(getPathForResource("dog.txt"), 1024),
                            // analyzer which split input by space
                            WhiteSpaceAnalyzer(),
                            // tokenizer which do not modify source input
                            NOOPTokenizer()
                        )
                    )
                }
            }

            // wait indexing to finish
            cattleIndexing.join()
            dogIndexing.join()

            val reader = AtomicIndexReader(index)

            reader
                .search(
                    // search for all the documents which contains control term
                    TermFilter("control"),
                    // extract positions of control term from document
                    DefaultDocumentExtractor(listOf("control"))
                )
                .collect {
                    // input such information
                    print("Term 'control' was found in a document ${it.document.source.name} at positions :")
                    for (pos in it.terms["control"]?.iterator()!!) {
                        print("${pos} ")
                    }
                    println()
                }
        }
    }

    @Test
    fun testFulltext() {
        runBlocking {
            val index = RAMIndex()
            val writer = AtomicIndexWriter(index)

            GlobalScope.launch {
                writer.inTransaction {
                    for (dataset in Dataset.values()) {
                        index(dataset.getTermSource(SubstringTokenizer()))
                    }
                }
            }.join()

            searchForTermListAndValidate(AtomicIndexReader(index), listOf("earch"), getDatasetsWithTerm("earch"))
        }
    }

    @Test
    fun testSimple() {
        runBlocking {
            val index = RAMIndex()
            val writer = AtomicIndexWriter(index)

            GlobalScope.launch {
                writer.inTransaction {
                    for (dataset in Dataset.values()) {
                        index(dataset.getTermSource())
                    }
                }
            }.join()

            searchForTermListAndValidate(AtomicIndexReader(index), listOf("milk"), getDatasetsWithTerm("milk"))
        }
    }

    @Test
    fun testAtomic() {
        runBlocking {
            val index = RAMIndex()
            val writer = AtomicIndexWriter(index)

            val firstIndexingLock = Mutex(true)
            val secondIndexingLock = Mutex(true)

            val latch = CountDownLatch(2)

            // start indexing, but do not commit it
            val firstJob = GlobalScope.launch {
                writer.inTransaction {
                    index(Dataset.GOAT.getTermSource())
                    index(Dataset.CAT.getTermSource())
                    // index data, but don't commit it
                    latch.countDown()
                    firstIndexingLock.withLock { }
                }
            }

            val secondJob = GlobalScope.launch {
                writer.inTransaction {
                    // index documents in transaction. All the documents added or none
                    index(Dataset.CATTLE.getTermSource())
                    index(Dataset.SHEEP.getTermSource())
                    // index data, but don't commit it
                    latch.countDown()
                    secondIndexingLock.withLock { }
                }
            }

            latch.await()
            // nothing is committed yet, we won't see anything
            searchForTermListAndValidate(AtomicIndexReader(index), listOf("milk"), emptySet())

            // commit first indexing, we should see milk keyword inside GOAT dataset
            firstIndexingLock.unlock()
            firstJob.join()
            searchForTermListAndValidate(AtomicIndexReader(index), listOf("milk"), setOf(Dataset.GOAT))

            // commit second indexing, now we should see milk keyword inside goat, cattle and sheep documents
            secondIndexingLock.unlock()
            secondJob.join()
            searchForTermListAndValidate(
                AtomicIndexReader(index),
                listOf("milk"),
                setOf(Dataset.GOAT, Dataset.CATTLE, Dataset.SHEEP)
            )
        }

    }

    @Test
    fun testCancellation() {
        runBlocking {
            val index = RAMIndex()
            val writer = AtomicIndexWriter(index)

            val firstIndexingLock = Mutex(true)
            val secondIndexingLock = Mutex(true)

            val latch = CountDownLatch(2)

            val firstJob = GlobalScope.launch {
                writer.inTransaction {
                    index(Dataset.GOAT.getTermSource())
                    index(Dataset.CAT.getTermSource())
                    // index data, but don't commit it
                    latch.countDown()
                    firstIndexingLock.withLock { }
                }
            }

            val secondJob = GlobalScope.launch {
                writer.inTransaction {
                    index(Dataset.CATTLE.getTermSource())
                    index(Dataset.SHEEP.getTermSource())
                    // index data, but don't commit it
                    latch.countDown()
                    secondIndexingLock.withLock { }
                }
            }

            latch.await()
            // nothing is committed yet, we won't see any data
            searchForTermListAndValidate(AtomicIndexReader(index), listOf("milk"), emptySet())

            // commit first transaction, now we'll see GOAT dataset
            firstIndexingLock.unlock()
            firstJob.join()
            searchForTermListAndValidate(AtomicIndexReader(index), listOf("milk"), setOf(Dataset.GOAT))

            // cancel second transaction. We won't see any differences and all updates during second transaction
            // indexing will be reverted
            secondJob.cancel()
            secondJob.join()
            secondIndexingLock.unlock()
            searchForTermListAndValidate(AtomicIndexReader(index), listOf("milk"), setOf(Dataset.GOAT))
        }

    }

    private suspend fun searchForTermListAndValidate(
        indexReader: IndexReader,
        termList: List<String>,
        datasets: Set<Dataset>
    ) {
        val filter = if (termList.size == 1) TermFilter(termList[0]) else ANDFilter(termList.map { TermFilter(it) })

        val datasetsInResult = HashSet<Dataset>()

        indexReader.search(filter, DefaultDocumentExtractor(termList))
            .collect {
                val dataset = Dataset.valueOf(it.document.source.type)
                datasetsInResult.add(dataset)
                for ((term, index) in it.terms) {
                    dataset.validateTermPositions(term, index)
                }
            }

        assertEquals(datasets, datasetsInResult)

    }
}