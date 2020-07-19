package org.indexer.tokenization

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.indexer.document.TermInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class NOOPTokenizerTest {

    @Test
    fun test() {
        runBlocking {
            val tokenizer = NOOPTokenizer()
            val flow : Flow<TermInfo> = flow {}
            assertEquals(flow, tokenizer.tokenize(flow))
        }
    }
}