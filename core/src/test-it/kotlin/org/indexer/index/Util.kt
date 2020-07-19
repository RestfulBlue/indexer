package org.indexer.index

import org.indexer.analyze.WhiteSpaceAnalyzer
import org.indexer.document.Source
import org.indexer.index.inverted.ReadableInvertedIndex
import org.indexer.source.CompositeTermSource
import org.indexer.source.FileInput
import org.indexer.source.FileInputTest
import org.indexer.tokenization.NOOPTokenizer
import org.indexer.tokenization.Tokenizer
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

enum class Dataset(private val filename: String) {
    CAT("cat.txt"),
    CATTLE("cattle.txt"),
    CHICKEN("chicken.txt"),
    DOG("dog.txt"),
    GOAT("goat.txt"),
    SHEEP("sheep.txt");

    val path = Paths.get(FileInputTest::class.java.classLoader.getResource(filename)?.toURI()!!)

    val data = String(Files.readAllBytes(path))

    fun getTermSource(tokenizer : Tokenizer = NOOPTokenizer()) = CompositeTermSource(
        Source(name, filename),
        FileInput(path.toFile().absolutePath, 100),
        WhiteSpaceAnalyzer(),
        tokenizer
    )

    fun validateTermPositions(term: String, index: ReadableInvertedIndex<Long>) {
        val iterator = index.iterator()
        var pos: Int = data.indexOf(term)
        while (pos >= 0) {
            assertEquals(pos, iterator.next().toInt())
            pos = data.indexOf(term, pos + 1)
        }
    }
}

fun getDatasetsWithTerm(term: String): Set<Dataset> {
    val result = HashSet<Dataset>()
    for (dataset in Dataset.values()) {
        if (dataset.data.contains(term)) {
            result.add(dataset)
        }
    }
    return result
}

fun getPathForResource(path: String): String {
    return Paths
        .get(FileInputTest::class.java.classLoader.getResource(path)?.toURI()!!)
        .toFile()
        .absolutePath
}
