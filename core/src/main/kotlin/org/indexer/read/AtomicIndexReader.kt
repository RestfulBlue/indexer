package org.indexer.read

import kotlinx.coroutines.flow.Flow
import org.indexer.index.TransactionalIndex
import org.indexer.index.inverted.FilteredInvertedIndex
import org.indexer.read.extract.DocumentExtractor
import org.indexer.read.extract.DocumentProjection
import org.indexer.read.filter.Filter

/**
 * Index reader, which guarantees read committed isolation level
 * by filtering all the documents, specified in a transaction storage
 */
class AtomicIndexReader(private val index: TransactionalIndex) : IndexReader {

    override suspend fun search(filter: Filter, documentExtractor: DocumentExtractor): Flow<DocumentProjection> {
        val readCommittedFilter = FilteredInvertedIndex(filter.filter(index)) {
            !index.transactionStorage().containsDocument(it)
        }
        return documentExtractor.extractDocument(index, readCommittedFilter)
    }
}