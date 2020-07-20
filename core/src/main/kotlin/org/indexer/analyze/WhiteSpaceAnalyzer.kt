package org.indexer.analyze

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.indexer.document.TermInfo

class WhiteSpaceAnalyzer : Analyzer {

    private var lastIndex: Long = 0
    private val builder = StringBuilder()

    override fun analyze(flow: Flow<TermInfo>): Flow<TermInfo> = flow {
        flow.collect {
            for (i in it.term.indices) {
                val symb = it.term[i]

                if (symb == ' ') {
                    emit(TermInfo(builder.toString(), lastIndex))
                    lastIndex += builder.length + 1
                    builder.clear()
                } else {
                    builder.append(symb)
                }
            }
        }

        if (builder.isNotEmpty()) {
            emit(TermInfo(builder.toString(), lastIndex))
        }
    }
}