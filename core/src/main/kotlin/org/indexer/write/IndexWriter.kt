package org.indexer.write

import org.indexer.source.TermSource

/**
 * indexing all the data from specified termSource to index
 */
interface IndexWriter {

    /**
     * @param termSource which should be indexed
     */
    suspend fun index(termSource: TermSource)

}