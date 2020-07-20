package org.indexer.index.term

import org.indexer.index.inverted.ReadableInvertedIndex

/**
 * Interface which must be implement by term storages which supports read operations
 */
interface ReadableTermStorage {

    /**
     * Return an inverted index for specified term
     * @param term term to look for
     * @return inverted index for specified term
     */
    fun getTermIndex(term: String): ReadableInvertedIndex<Int>

}