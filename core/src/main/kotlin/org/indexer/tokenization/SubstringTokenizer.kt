package org.indexer.tokenization

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.indexer.document.TermInfo

class SubstringTokenizer : Tokenizer {
    override suspend fun tokenize(data: Flow<TermInfo>): Flow<TermInfo>  {
        return data.transform {
            for( from in it.term.indices){
                for(to in from + 1 until it.term.length+1){
                    emit(TermInfo(it.term.substring(from, to).intern(), it.offset + from))
                }
            }
        }
    }
}