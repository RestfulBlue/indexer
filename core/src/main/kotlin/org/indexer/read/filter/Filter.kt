package org.indexer.read.filter

import org.indexer.index.ReadableIndex
import org.indexer.index.inverted.ReadableInvertedIndex

interface Filter {

    /**
     * Creates inverted index using specified index
     */
    fun filter(index: ReadableIndex): ReadableInvertedIndex<Int>

}