package org.indexer.write

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.indexer.index.TransactionalIndex
import org.indexer.source.TermSource

/**
 * Index writer which guarantees atomic writes. In case of errors all the data will be reverted.
 * If indexation will be cancelled - everything from transaction will be reverted
 */
class AtomicIndexWriter(index: TransactionalIndex) : IndexWriter {

    private val documentStorage = index.documentStorage()
    private val termStorage = index.termStorage()
    private val transactionStorage = index.transactionStorage()

    suspend fun inTransaction(indexAction: suspend IndexWriter.() -> Unit) {
        val transactionId = transactionStorage.createTransaction()
        val context = TransactionContext(this, transactionId)
        try {
            context.indexAction()
        } catch (e: Exception) {
            withContext(NonCancellable) {
                rollbackTransaction(transactionId)
            }
        } finally {
            withContext(NonCancellable) {
                transactionStorage.removeTransaction(transactionId)
            }
        }
    }

    override suspend fun index(termSource: TermSource) {
        inTransaction {
            index(termSource)
        }
    }

    internal suspend fun indexInTransaction(termSource: TermSource, transactionId: Int) {
        val id = documentStorage.createDocument(termSource.getSource())
        transactionStorage.addDocumentToTransaction(transactionId, id)

        termSource.getTermFlow().collect {
            documentStorage.addTerm(id, it.term, it.offset)
            termStorage.addTerm(it.term, id)
        }
    }

    private suspend fun rollbackTransaction(transactionId: Int) {
        for (documentId in transactionStorage.getTransactionDocuments(transactionId)) {
            // clear terms
            for (term in documentStorage.getDocumentTerms(documentId)) {
                termStorage.removeTerm(term, documentId)
            }
            // clear docs
            documentStorage.removeDocument(documentId)
        }
    }

}

private class TransactionContext(private val writer: AtomicIndexWriter, private val transactionId: Int) : IndexWriter {
    override suspend fun index(termSource: TermSource) {
        writer.indexInTransaction(termSource, transactionId)
    }
}