package org.indexer.read.filter

import kotlinx.coroutines.runBlocking
import org.indexer.index.RAMIndex
import kotlin.test.Test

class TermFilterTest {

    @Test
    fun testTermFilter() {
        runBlocking {
            val index = RAMIndex()

            val termStorage = index.termStorage()
            termStorage.addTerm("test", 0)

            val filter = TermFilter("test")
            val data = filter.filter(index).iterator().asSequence().toList()
            assert(data.contains(0))
        }
    }
}