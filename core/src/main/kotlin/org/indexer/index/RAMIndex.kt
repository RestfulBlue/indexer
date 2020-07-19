package org.indexer.index

import org.indexer.index.document.RAMDocumentStorage
import org.indexer.index.document.ReadWriteDocumentStorage
import org.indexer.index.term.RAMTermStorage
import org.indexer.index.term.ReadWriteTermStorage
import org.indexer.index.transaction.RAMTransactionStorage
import org.indexer.index.transaction.TransactionStorage

class RAMIndex : TransactionalIndex {

    private val documentStorage = RAMDocumentStorage()
    private val termStorage = RAMTermStorage()
    private val transactionStorage = RAMTransactionStorage()

    override fun documentStorage(): ReadWriteDocumentStorage {
        return documentStorage
    }

    override fun termStorage(): ReadWriteTermStorage {
        return termStorage
    }

    override fun transactionStorage(): TransactionStorage {
        return transactionStorage
    }
}