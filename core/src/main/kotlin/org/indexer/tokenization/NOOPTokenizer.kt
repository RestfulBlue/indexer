package org.indexer.tokenization

import kotlinx.coroutines.flow.Flow
import org.indexer.document.TermInfo

class NOOPTokenizer : Tokenizer {
    override suspend fun tokenize(data: Flow<TermInfo>): Flow<TermInfo> {
        return data
    }
}