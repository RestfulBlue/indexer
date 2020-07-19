package org.indexer.read.filter

import org.indexer.createFilterFromList
import org.indexer.index.RAMIndex
import org.indexer.index.inverted.IntersectedInvertedIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class ANDFilterTest {

    private fun test(input: List<List<Int>>, output: List<Int>) {
        val indexes = input.map {
            createFilterFromList(it)
        }

        val result = ANDFilter(indexes).filter(RAMIndex()).iterator().asSequence().toList()
        assertEquals(output, result)
    }

    @Test
    fun testAndFilter() {
        test(
            listOf(
                listOf(1,2,3,4,5,6,7,8),
                listOf(4,6,7),
                listOf(2,4,6,8)
            ),
            listOf(4, 6)
        )
    }

    @Test
    fun testEmptyAndFilter() {
        test(
            listOf(
                listOf(1,2,3,4,5,6,7,8),
                listOf(1,3,5),
                listOf(2,4,6,8)
            ),
            listOf()
        )
    }


    @Test
    fun testEmptyInput() {
        test(
            listOf(
                listOf(1,2,3,4,5,6,7,8),
                listOf(),
                listOf(2,4,6,8)
            ),
            listOf()
        )


        test(
            listOf(
                listOf(),
                listOf(),
                listOf()
            ),
            listOf()
        )

        test(
            listOf(
                listOf(),
                listOf()
            ),
            listOf()
        )
    }

}