package org.indexer.read.extract

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.indexer.index.ReadableIndex
import org.indexer.index.inverted.ReadableInvertedIndex

class DefaultDocumentExtractor(private val terms: List<String>) : DocumentExtractor {

    override fun extractDocument(
        index: ReadableIndex,
        documentsInvertedIndex: ReadableInvertedIndex<Int>
    ): Flow<DocumentProjection> = flow {
        for (docId in documentsInvertedIndex.iterator()) {
            with(index.documentStorage()) {
                val projectedTerms: MutableMap<String, ReadableInvertedIndex<Long>> = HashMap()

                for (term in terms) {
                    projectedTerms[term] = getDocumentTermOffsets(docId, term)
                }

                emit(DocumentProjection(getDocumentById(docId), projectedTerms))
            }
        }
    }
}