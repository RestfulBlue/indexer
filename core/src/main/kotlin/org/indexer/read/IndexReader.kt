package org.indexer.read

import kotlinx.coroutines.flow.Flow
import org.indexer.read.extract.DocumentExtractor
import org.indexer.read.extract.DocumentProjection
import org.indexer.read.filter.Filter

interface IndexReader {

    /**
     * Performs search in an index using specified filter and document extractor
     * @return flow of document projections, which applies to specified filter
     */
    suspend fun search(filter: Filter, documentExtractor: DocumentExtractor): Flow<DocumentProjection>

}