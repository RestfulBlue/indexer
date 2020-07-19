package org.indexer.index

import org.indexer.index.transaction.TransactionStorage

/**
 * In order to use atomic read and writes on some index it has to be readwrite storage
 * And also it should have transaction storage
 */
interface TransactionalIndex : ReadWriteIndex {

    /**
     * @return transaction storage for specified rw index
     */
    fun transactionStorage() : TransactionStorage

}