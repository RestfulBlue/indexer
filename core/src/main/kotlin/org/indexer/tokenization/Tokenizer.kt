package org.indexer.tokenization

import kotlinx.coroutines.flow.Flow
import org.indexer.document.TermInfo

interface Tokenizer {

    /**
     * Tokenizer is responsible for tokenization of source terms
     * For example, if it receives term hello it can produce h, he, hel, hell, hello, e, el , etc...
     * It could also generate k-skip-n-gram and so on ( but such logic can also be achieved
     * on a read level, deconstructing query term to such k-skip-n-grams
     */
    suspend fun tokenize(data: Flow<TermInfo>): Flow<TermInfo>

}