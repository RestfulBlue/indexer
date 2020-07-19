package org.indexer.index.inverted

import kotlin.test.Test
import kotlin.test.assertEquals

class RAMInvertedIndexTest {

    @Test
    fun testOrdering(){
        val index = RAMInvertedIndex<Int>()

        index.add(0)
        index.add(5)
        index.add(2)

        assertEquals(3, index.size())

        val list = index.iterator().asSequence().toList()
        assertEquals(3, list.size)
        assert(list.contains(0))
        assert(list.contains(5))
        assert(list.contains(2))

        // iterator should always return sorted series
        assertEquals(listOf(0,2,5), list)
    }


    @Test
    fun testRemove(){
        val index = RAMInvertedIndex<Int>()

        index.add(0)
        index.add(5)
        index.add(2)

        index.remove(5)

        val list = index.iterator().asSequence().toList()
        assertEquals(2, list.size)
        assert(list.contains(0))
        assert(list.contains(2))

        // iterator should always return sorted series
        assertEquals(listOf(0,2), list)
    }


}
