package org.indexer.index.inverted

import org.indexer.createInvertedFromList
import kotlin.test.Test
import kotlin.test.assertEquals

class IntersectedInvertedIndexTest {

    private fun test(input: List<List<Int>>, output: List<Int>) {
        val indexes = input.map {
            createInvertedFromList(it)
        }

        val result = IntersectedInvertedIndex(indexes).iterator().asSequence().toList()
        assertEquals(output, result)
    }

    @Test
    fun testIntersect() {
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
    fun testEmptyIntersect() {
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