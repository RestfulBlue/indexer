package org.indexer.index.inverted

/**
 * Interface which must implement readable inverted index
 */
interface ReadableInvertedIndex<T>  {

    /**
     * @return creates new iterator or cursor on inverted index.
     * must return values in ascending order
     */
    fun iterator() : Iterator<T>

}