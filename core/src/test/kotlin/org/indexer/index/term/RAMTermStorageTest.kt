package org.indexer.index.term

import kotlinx.coroutines.runBlocking
import org.indexer.index.inverted.EmptyInvertedIndex
import org.indexer.index.inverted.ReadWriteInvertedIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class RAMTermStorageTest {

    @Test
    fun testStorage() {
        runBlocking {
            val storage = RAMTermStorage()

            assert(storage.getTermIndex("test") is EmptyInvertedIndex)

            storage.addTerm("test", 0)
            storage.addTerm("test", 1)

            val invertedIndex = storage.getTermIndex("test")
            assert(invertedIndex is ReadWriteInvertedIndex)

            val indexData = invertedIndex.iterator().asSequence().toSet()
            assertEquals(2, indexData.size)
            assert(indexData.contains(0))
            assert(indexData.contains(1))

            storage.removeTerm("test", 1)
            assert(storage.getTermIndex("test") is ReadWriteInvertedIndex)

            storage.removeTerm("test", 0)
            assert(storage.getTermIndex("test") is EmptyInvertedIndex)
        }
    }

}