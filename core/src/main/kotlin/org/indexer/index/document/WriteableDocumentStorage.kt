package org.indexer.index.document

import org.indexer.document.Source


/**
 * interface which must implement storage which supports write operations
 * Probably in memory storage can support both read and write, but only read operation usually
 * supported for file-based indexes
 */
interface WriteableDocumentStorage {

    /**
     * @param source describe source of document
     * @return generated id for new document. Id must be unique across storage
     */
    suspend fun createDocument(source: Source): Int

    /**
     * Adds term with specified offset to document with specified id
     * @param documentId id
     * @param term added term
     * @param offset position of term in the document
     */
    suspend fun addTerm(documentId: Int, term: String, offset: Long)

    /**
     * removes document from storage
     * @param documentId id of the document
     */
    suspend fun removeDocument(documentId: Int)

}