package org.indexer.index.inverted

class EmptyInvertedIndex<T> : ReadableInvertedIndex<T> {

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            override fun hasNext(): Boolean {
                return false
            }

            override fun next(): T {
                throw IllegalStateException()
            }

        }
    }
}