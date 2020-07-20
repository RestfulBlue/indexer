package org.indexer.index.term

import org.indexer.index.inverted.EmptyInvertedIndex
import org.indexer.index.inverted.RAMInvertedIndex
import org.indexer.index.inverted.ReadWriteInvertedIndex
import org.indexer.index.inverted.ReadableInvertedIndex
import java.util.concurrent.ConcurrentHashMap

class RAMTermStorage : ReadWriteTermStorage {

    private val data: MutableMap<String, ReadWriteInvertedIndex<Int>> = ConcurrentHashMap()

    override fun getTermIndex(term: String): ReadableInvertedIndex<Int> {
        return data[term] ?: EmptyInvertedIndex()
    }

    override suspend fun addTerm(term: String, documentId: Int) {
        data.computeIfAbsent(term) {
            RAMInvertedIndex()
        }.add(documentId)
    }

    override suspend fun removeTerm(term: String, documentId: Int) {
        data.compute(term) { _, index ->
            index?.remove(documentId)
            // remove inverted index if it's became empty ( for example during transaction rollback)
            if (index?.size() == 0) {
                return@compute null
            }
            return@compute index
        }
    }
}