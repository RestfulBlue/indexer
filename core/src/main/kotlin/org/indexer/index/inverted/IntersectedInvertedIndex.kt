package org.indexer.index.inverted

import kotlin.math.max

class IntersectedInvertedIndex(private val sets: List<ReadableInvertedIndex<Int>>) : ReadableInvertedIndex<Int> {

    override fun iterator(): Iterator<Int> {

        val setList = ArrayList<DocumentSetCursor>()
        var maxValue = 0
        for (set in sets) {
            val iterator = set.iterator()
            if (!iterator.hasNext()) {
                return EmptyCursor
            }
            val value = iterator.next()
            maxValue = max(maxValue, value)

            setList.add(DocumentSetCursor(value, iterator))
        }

        return object : Iterator<Int> {

            private var max = maxValue
            private var processed = false
            private var nextValue: Int? = null
            private val list = setList

            private fun advance() {
                var good = true
                var nextMax = Int.MIN_VALUE
                for (cursor in list) {

                    with(cursor) {
                        while (currentValue < max && iterator.hasNext()) {
                            currentValue = iterator.next()
                        }
                    }


                    nextMax = max(nextMax, cursor.currentValue)

                    if (cursor.currentValue > max) {
                        good = false
                        break
                    }

                    if (cursor.currentValue < max) {
                        good = false
                        processed = true
                        break
                    }


                    if (!cursor.iterator.hasNext()) {
                        processed = true
                        continue
                    }

                    cursor.currentValue = cursor.iterator.next()
                }

                if (good) {
                    nextValue = max
                }
                max = nextMax

            }

            private fun findNext() {
                while (nextValue == null && !processed) {
                    advance()
                }
            }

            override fun hasNext(): Boolean {
                findNext()
                return !(nextValue == null && processed)
            }

            override fun next(): Int {
                findNext()
                if (nextValue == null && processed) {
                    throw IllegalStateException()
                }

                val tmp = nextValue!!
                nextValue = null
                return tmp
            }

        }
    }
}

private data class DocumentSetCursor(var currentValue: Int, val iterator: Iterator<Int>)

object EmptyCursor : Iterator<Int> {
    override fun hasNext(): Boolean {
        return false
    }

    override fun next(): Int {
        throw IllegalStateException()
    }
}