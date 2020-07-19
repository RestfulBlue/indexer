package org.indexer.read

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.indexer.createFilterFromList
import org.indexer.index.RAMIndex
import org.indexer.index.ReadableIndex
import org.indexer.index.inverted.ReadableInvertedIndex
import org.indexer.read.extract.DocumentExtractor
import org.indexer.read.extract.DocumentProjection
import kotlin.test.Test
import kotlin.test.assertEquals

class AtomicIndexReaderTest {

    @Test
    fun testConstruction() {

        runBlocking {
            val index = RAMIndex()
            with(index) {
                with(transactionStorage()) {
                    val id = createTransaction()
                    addDocumentToTransaction(id, 1)
                    addDocumentToTransaction(id, 2)
                }
            }

            val reader = AtomicIndexReader(index)

            reader.search(
                createFilterFromList(listOf(1, 2, 3, 4)),
                object : DocumentExtractor {
                    override fun extractDocument(
                        index: ReadableIndex,
                        documentsInvertedIndex: ReadableInvertedIndex<Int>
                    ): Flow<DocumentProjection> {
                        assertEquals(documentsInvertedIndex.iterator().asSequence().toList(), listOf(3, 4))
                        return flow {}
                    }

                })
        }
    }

}