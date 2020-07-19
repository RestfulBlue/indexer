package org.indexer.read.filter

import org.indexer.index.ReadableIndex
import org.indexer.index.inverted.ReadableInvertedIndex

/**
 * Creates inverted index for specified terms
 */
class TermFilter(private val term: String) : Filter {

    override fun filter(index: ReadableIndex): ReadableInvertedIndex<Int> {
        return index.termStorage().getTermIndex(term)
    }

}