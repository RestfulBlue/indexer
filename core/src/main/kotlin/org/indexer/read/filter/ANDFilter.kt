package org.indexer.read.filter

import org.indexer.index.ReadableIndex
import org.indexer.index.inverted.IntersectedInvertedIndex
import org.indexer.index.inverted.ReadableInvertedIndex

/**
 * Creates AND filter for specified filters
 */
class ANDFilter(private val filters: List<Filter>) : Filter {

    override fun filter(index: ReadableIndex): ReadableInvertedIndex<Int> {
        return IntersectedInvertedIndex(filters.map { it.filter(index) })
    }

}