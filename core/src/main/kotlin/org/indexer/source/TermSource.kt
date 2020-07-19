package org.indexer.source

import kotlinx.coroutines.flow.Flow
import org.indexer.document.Source
import org.indexer.document.TermInfo

interface TermSource {

    /**
     * @return source of this terms
     */
    fun getSource() : Source

    /**
     * @return processed flow of terms, which passed analyzation and tokenization
     */
    suspend fun getTermFlow() : Flow<TermInfo>

}