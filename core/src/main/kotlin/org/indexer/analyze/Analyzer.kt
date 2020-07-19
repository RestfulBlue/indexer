package org.indexer.analyze

import kotlinx.coroutines.flow.Flow
import org.indexer.document.TermInfo


interface Analyzer {

    /**
     * Deconstruct source flow into terms using some algorithm
     * For example flow can contain record 'hello wor',0  and 'ld',9
     * If we deconstruct source flow with whitespace strategy we will get this
     * term infos : 'hello',0   and 'world',6
     *
     * @param flow source input of data. For example can be just a stream of chars from file
     * @return analyzed flow. For example we can split source input by whitespace
     */
    fun analyze(flow : Flow<TermInfo>) : Flow<TermInfo>

}