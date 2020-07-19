package org.indexer.source

import kotlinx.coroutines.flow.Flow
import org.indexer.document.TermInfo

/**
 * Represent source of everything. Should return sequence of
 */
interface Input {

    /**
     * @return flow of input terms.
     * For example, FileInput should return all sequence of chars from file,
     * without any preprocessing
     */
    suspend fun getInputFlow(): Flow<TermInfo>

}