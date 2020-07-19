package org.indexer.index.transaction

import org.indexer.exception.TransactionNotExist
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class RAMTransactionStorage : TransactionStorage {

    private val idGenerator = AtomicInteger()
    private val transactions : MutableMap<Int, MutableSet<Int>> = ConcurrentHashMap()
    private val documents : MutableMap<Int, Int> = ConcurrentHashMap()

    override fun createTransaction(): Int {
        val nextId = idGenerator.getAndIncrement()
        transactions[nextId] = ConcurrentHashMap.newKeySet()
        return nextId
    }

    override fun addDocumentToTransaction(transactionId: Int, documentId: Int) {
        val data = transactions[transactionId] ?: throw TransactionNotExist()
        data.add(documentId)
        documents[documentId] = transactionId
    }

    override fun getTransactionDocuments(transactionId: Int): Iterator<Int> {
        val data = transactions[transactionId] ?: throw TransactionNotExist()

        return data.iterator()
    }

    override fun containsDocument(documentId: Int): Boolean {
        val transactionId = documents[documentId] ?: return false
        return transactions.containsKey(transactionId)

    }

    override fun removeTransaction(transactionId: Int) {
        transactions.compute(transactionId) { _, set ->
            if (set == null) {
                throw TransactionNotExist()
            }

            for (document in set) {
                documents.remove(document)
            }

            return@compute null
        }
    }
}