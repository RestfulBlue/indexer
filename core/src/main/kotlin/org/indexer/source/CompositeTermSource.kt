package org.indexer.source

import kotlinx.coroutines.flow.Flow
import org.indexer.analyze.Analyzer
import org.indexer.document.Source
import org.indexer.document.TermInfo
import org.indexer.tokenization.Tokenizer

class CompositeTermSource(
    private val source : Source,
    private val input: Input,
    private val analyzer: Analyzer,
    private val tokenizer: Tokenizer
) : TermSource {

    override fun getSource(): Source {
        return source
    }

    override suspend fun getTermFlow(): Flow<TermInfo> {
        return tokenizer.tokenize(analyzer.analyze(input.getInputFlow()))
    }
}
