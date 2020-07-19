package org.indexer.index.inverted

/**
 * Interface which must be implemented on writeable inverted index
 */
interface WriteableInvertedIndex<T> {

    /**
     * Add documentId value to inverted index
     * @param value value which should be added
     */
    fun add(value: T)

    /**
     * remove value from inverted index
     * @param value which should be removed
     */
    fun remove(value: T)

    /**
     * @return size of this inverted index
     */
    fun size(): Int

}