package org.indexer.index.term

/**
 * Interface which must be implemented by all term storages which supports write operations
 */
interface WriteableTermStorage {

    /**
     * Adds documentId to inverted index of specified term
     * @param term of target inverted index
     * @param documentId id to add to inverted index
     */
    suspend fun addTerm(term : String, documentId: Int)

    /**
     * removes documentId from inverted index of specified term
     * @param term of target index
     * @param documentId to remove
     */
    suspend fun removeTerm(term : String, documentId: Int)

}