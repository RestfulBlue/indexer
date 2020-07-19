package org.indexer.analyze

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.indexer.document.TermInfo
import org.junit.Test
import kotlin.test.assertEquals

class WhiteSpaceAnalyzerTest {

    @Test
    fun checkWordCountTest() {
        runBlocking {
            val analyzer = WhiteSpaceAnalyzer()
            val result: MutableList<TermInfo> = ArrayList()
            analyzer.analyze(flow {
                emit(TermInfo("qwe ", 0))
                emit(TermInfo("789 ", 4))
                emit(TermInfo("321", 8))
            }).toList(result)

            assertEquals(3, result.size)
        }
    }

}