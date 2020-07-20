package org.indexer.index.inverted

import java.util.concurrent.ConcurrentSkipListSet

class RAMInvertedIndex<T> : ReadWriteInvertedIndex<T> {

    // our iterator should return ordered data in order
    // to merge it with disk based index ( which could be serial only )
    private val data: MutableSet<T> = ConcurrentSkipListSet<T>()

    override fun add(value: T) {
        data.add(value)
    }

    override fun remove(value: T) {
        data.remove(value)
    }

    override fun iterator(): Iterator<T> {
        return data.iterator()
    }

    override fun size(): Int {
        return data.size
    }
}