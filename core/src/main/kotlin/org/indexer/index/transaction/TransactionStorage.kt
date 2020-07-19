package org.indexer.index.transaction

/**
 * Transactional storage used in order to maintain transaction guarantees,
 * read committed isolation, rollback and commits
 * Stores transactions id and information about which transaction contains specified documented
 * This storages is used during write and read
 * During write it used to rollback. All written data first added to transaction storage.
 * If we have to rollback transaction - data inside storage used to do that
 *
 * During read we use this storage in order to find if document committed or not
 * If storage contains specified document - that document wasn't committed
 *
 * All operations should be atomic
 */
interface TransactionStorage {

    /**
     * initiate new transaction and associate it with unique id
     * @return new unique id of generated transaction
     */
    fun createTransaction(): Int

    /**
     * links document with specified transaction
     * @param transactionId id of transaction, which will hold specified document
     * @param documentId id of document, which will be added to transaction
     */
    fun addDocumentToTransaction(transactionId: Int, documentId: Int)

    /**
     * @return Iterator of all documents of transaction. Usually used during rollback
     */
    fun getTransactionDocuments(transactionId: Int): Iterator<Int>

    /**
     * Checks if storage contains specified document. Used to determine if document committed or not
     * @param documentId target document id
     * @return true if document present in the storage
     */
    fun containsDocument(documentId: Int): Boolean

    /**
     * removes transaction and all the data associated with it
     * @param transactionId id of transaction
     */
    fun removeTransaction(transactionId: Int)

}