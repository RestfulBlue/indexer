package org.indexer.read.extract

import kotlinx.coroutines.flow.Flow
import org.indexer.document.Document
import org.indexer.index.ReadableIndex
import org.indexer.index.inverted.ReadableInvertedIndex

interface DocumentExtractor {

    /**
     * Extracts document projection from storage using specified inverted index
     * @param index index with documents
     * @param documentsInvertedIndex inverted index of document ids
     * @return flow of projected documents
     */
    fun extractDocument(
        index: ReadableIndex,
        documentsInvertedIndex: ReadableInvertedIndex<Int>
    ): Flow<DocumentProjection>

}

data class DocumentProjection(val document: Document, val terms: Map<String, ReadableInvertedIndex<Long>>)
