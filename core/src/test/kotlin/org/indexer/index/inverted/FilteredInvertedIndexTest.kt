package org.indexer.index.inverted

import org.indexer.createInvertedFromList
import kotlin.test.Test
import kotlin.test.assertEquals

class FilteredInvertedIndexTest {

    private fun test(input: List<Int>, output: List<Int>, filter: (value: Int) -> Boolean) {
        val result = FilteredInvertedIndex(createInvertedFromList(input), filter).iterator().asSequence().toList()
        assertEquals(output, result)
    }

    @Test
    fun testFilter() {
        test(
            listOf(1, 2, 3, 4, 5, 6, 7, 8),
            listOf(2, 4, 6, 8)
        ) { it % 2 == 0 }
    }

    @Test
    fun testEmptyInput() {
        test(
            listOf(),
            listOf()
        ) { it % 2 == 0 }
    }

    @Test
    fun testEmptyOutput(){
        test(
            listOf(1, 2, 3, 4, 5, 6, 7, 8),
            listOf()
        ) { false }
    }

}