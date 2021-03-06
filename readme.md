# Simple fulltext indexer

This repo contains simple fulltext indexer, which supports following operations :
1. Indexing source input, analyzing and tokenizing it
1. Parallel writes and reads
1. Atomic writes and reads, read committed isolation
1. Transactional commits and rollbacks across multiple documents
1. Different filters, for example by term, AND filter, etc
1. Document projections


# Architecture 

![architecture](./docs/arch.png "Architecture")


# Usage

All the usage examples are located in an integration tests, for example RAMIndexIt

## Simple indexing 

```kotlin
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
        // search for all the documents which contain control term
        TermFilter("control"),
        // extract positions of control term from document
        DefaultDocumentExtractor(listOf("control"))
    )
    .collect {
        print("Term 'control' was found in a document ${it.document.source.name} at positions :")
        for (pos in it.terms["control"]?.iterator()!!) {
            print("${pos} ")
        }
        println()
    }
```

## Atomic indexing example 

```kotlin
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
```


# Indexing cancellation

```kotlin

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

```


# Future work

This version is simple and has a lot to be added :
1. Metrics and logging
1. Persistence. Index persisting should be implemented. It will accept ReadableIndex and 
persist it to disk. It has a lot to be implemented - delta encoded reverted indexes, 
roaring bitmap, memory mappings, offset management. After index persisted to disk - 
it becomes immutable
1. Index merger - Index becomes immutable after it persisted to disk. In order to update 
it we will just merge it with other indexes. To do that we need to implement index
merger, which will accept two ReadableIndexes and produce a new one
1. Resource management - in memory bytes limiters,
maximum parallel indexation, etc.. 
1. Extend query language - currently only simple selection by term and AND filter 
supported. We probably also want to search for phrases which contains space, for example
we want to find ```lo wo``` in a ```hello world ``` document. In order to do that - we 
have to split query by space - `lo` and `wo`. After that - we'll find documents that contain
both of them and merge inverted indexes of their positions with shifting. We shift ```lo``` inverted index
by 3 to the right and then intersect it with ```wo``` inverted index.
1. Add a lot of inputs, analyzers and tokenizers. JDBCInput, HttpInput, JavaAnalyzer, HTMLAnalyzer,
 KSkipNGramTokenizer, etc
1. Reduce memory and performance footprint. Current implementation has a LOT of a low performant places. For example
streaming data from input using flow probably has a lot of performance issues, especially wrapping source byte buffer to 
string and should be translated to save more 'row' processing. Also a lot of place can be optimized adding 
append-only data structures, but it probably won't fit with transactions

