package org.indexer.index.document

import org.indexer.document.Document
import org.indexer.document.Source
import org.indexer.exception.DocumentNotFoundException
import org.indexer.index.inverted.RAMInvertedIndex
import org.indexer.index.inverted.ReadableInvertedIndex
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class RAMDocumentStorage : ReadWriteDocumentStorage {

    private val idGenerator = AtomicInteger()
    private val docs : MutableMap<Int, RAMDocumentEntry> = ConcurrentHashMap()

    override suspend fun createDocument(source: Source): Int {
        val doc = Document(idGenerator.getAndIncrement(), source)
        docs[doc.id] = RAMDocumentEntry(doc)
        return doc.id
    }

    override suspend fun addTerm(documentId: Int, term: String, offset: Long) {
        // avoid using compute here in order to parallel thread can insert terms in a document at the same time
        val entry = docs[documentId] ?: throw DocumentNotFoundException()
        entry
            .terms
            .computeIfAbsent(term) { RAMInvertedIndex() }
            .add(offset)
    }

    override suspend fun getDocumentById(documentId: Int): Document {
        return docs[documentId]?.document ?: throw DocumentNotFoundException()
    }

    override suspend fun getDocumentTerms(documentId: Int): Iterator<String> {
        return docs[documentId]?.terms?.keys?.iterator()
            ?: throw DocumentNotFoundException()
    }

    override suspend fun getDocumentTermOffsets(documentId: Int, term: String): ReadableInvertedIndex<Long> {
        return docs[documentId]?.terms?.get(term)
            ?: throw DocumentNotFoundException()
    }

    override suspend fun removeDocument(documentId: Int) {
        docs.remove(documentId)
    }
}