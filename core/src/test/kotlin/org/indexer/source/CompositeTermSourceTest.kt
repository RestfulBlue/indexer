package org.indexer.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.runBlocking
import org.indexer.analyze.WhiteSpaceAnalyzer
import org.indexer.document.Source
import org.indexer.document.TermInfo
import org.indexer.tokenization.SubstringTokenizer
import org.junit.Test

class CompositeTermSourceTest {

    @Test
    fun testWhiteSpaceTokenizer() {
        runBlocking {
            val terms = CompositeTermSource(
                Source("test", "test"),
                object : Input {
                    override suspend fun getInputFlow(): Flow<TermInfo> = flow {
                        emit(TermInfo("zxcv ", 0))
                        emit(TermInfo("qwer ", 4))
                        emit(TermInfo("asdf ", 8))
                    }
                },
                WhiteSpaceAnalyzer(),
                SubstringTokenizer()
            ).getTermFlow().toSet(HashSet()).map { it.term }


            assert(terms.contains("zxcv"))
            assert(terms.contains("qwer"))
            assert(terms.contains("asdf"))
        }
    }

}