package org.indexer.tokenization

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.indexer.document.TermInfo
import kotlin.test.Test

class SubstringTokenizerTest {

    @Test
    fun test() {
        runBlocking {
            val tokenizer = SubstringTokenizer()
            val data = HashSet<TermInfo>()
            tokenizer.tokenize(flowOf(TermInfo("test", 0))).collect {
                data += it
            }

            assert(data.contains(TermInfo("test", 0)))
            assert(data.contains(TermInfo("t", 0)))
            assert(data.contains(TermInfo("e", 1)))
            assert(data.contains(TermInfo("s", 2)))
            assert(data.contains(TermInfo("t", 3)))
            assert(data.contains(TermInfo("est", 1)))
            assert(data.contains(TermInfo("st", 2)))
            assert(data.contains(TermInfo("es", 1)))
        }
    }

}