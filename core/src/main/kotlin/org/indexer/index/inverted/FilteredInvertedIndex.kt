package org.indexer.index.inverted

class FilteredInvertedIndex<T>(private val index: ReadableInvertedIndex<T>, private val filter: (T) -> Boolean) :
    ReadableInvertedIndex<T> {

    override fun iterator(): Iterator<T> {
        val iterator = index.iterator()

        return object : Iterator<T> {
            private var processed = false
            private var current: T? = null

            private fun findNext() {
                if (current != null) {
                    return
                }

                while (iterator.hasNext()) {
                    val nextValue = iterator.next()
                    if (filter(nextValue)) {
                        current = nextValue
                        break
                    }
                }
                processed = true
            }

            override fun hasNext(): Boolean {
                findNext()
                return !(current == null && processed)
            }

            override fun next(): T {
                findNext()
                if (current == null && processed) {
                    throw IllegalStateException()
                } else {
                    val tmp = current!!
                    current = null
                    return tmp
                }
            }

        }

    }

}