package org.indexer.index.document

import org.indexer.document.Document
import org.indexer.index.inverted.ReadableInvertedIndex

/**
 * Interface which must implement document storage which supports reading
 * Probably in memory storage can support both read and write, but only read operation usually
 * supported for file-based indexes
 */
interface ReadableDocumentStorage {

    /**
     * find document by id in the storage
     * @param documentId id of the document which need to be found
     * @return document
     */
    suspend fun getDocumentById(documentId : Int) : Document

    /**
     * @param documentId document to scan
     * @return iterator of all terms, which are present in this document
     */
    suspend fun getDocumentTerms(documentId: Int) : Iterator<String>

    /**
     * @param documentId document to look at
     * @param term - term used to search
     * @return inverted index with offset of all occurrances of specified term
     */
    suspend fun getDocumentTermOffsets(documentId : Int, term : String) : ReadableInvertedIndex<Long>
}