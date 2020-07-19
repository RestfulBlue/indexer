package org.indexer.index.inverted

import kotlinx.coroutines.runBlocking
import org.indexer.assertThrow
import kotlin.test.Test

class EmptyInvertedIndexTest {

    @Test
    fun testEmpty(){
        runBlocking {
            val index = EmptyInvertedIndex<Int>()

            val iterator = index.iterator()
            assert(!iterator.hasNext())
            assertThrow(IllegalStateException::class) {
                iterator.next()
            }
        }
    }
}