package org.indexer.index.transaction

import org.indexer.exception.TransactionNotExist
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RAMTransactionStorageTest {

    @Test
    fun testUsage() {

        with(RAMTransactionStorage()) {
            assertFailsWith<TransactionNotExist> { getTransactionDocuments(0) }
            assertFailsWith<TransactionNotExist> { addDocumentToTransaction(0, 0) }
            val id = createTransaction()

            assert(!getTransactionDocuments(id).hasNext())

            addDocumentToTransaction(id, 0)
            addDocumentToTransaction(id, 1)

            assert(getTransactionDocuments(id).asSequence().toSet().size == 2)
            assert(containsDocument(0))
            assert(containsDocument(1))
            assert(!containsDocument(2))

            assertFailsWith<TransactionNotExist> { removeTransaction(id+1) }
            removeTransaction(id)
            assertFailsWith<TransactionNotExist> { getTransactionDocuments(id) }
        }

    }


}