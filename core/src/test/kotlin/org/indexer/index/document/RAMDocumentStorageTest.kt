package org.indexer.index.document

import kotlinx.coroutines.runBlocking
import org.indexer.document.Source
import org.indexer.exception.DocumentNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RAMDocumentStorageTest {

    @Test
    fun testNormalUsage() {
        runBlocking {
            val storage = RAMDocumentStorage()

            val id = storage.createDocument(Source("first", "first"))
            assertEquals(0, id)
            assertEquals(1, storage.createDocument(Source("second", "second")))

            storage.addTerm(id, "test", 10)
            storage.addTerm(id, "test", 30)
            storage.addTerm(id, "test2", 15)

            assertEquals("first", storage.getDocumentById(id).source.type)

            val terms = storage.getDocumentTerms(id).asSequence().toSet()
            assertEquals(2, terms.size)
            assert(terms.contains("test"))
            assert(terms.contains("test2"))

            val offsets = storage.getDocumentTermOffsets(id, "test").iterator().asSequence().toSet()
            assert(offsets.contains(10))
            assert(offsets.contains(30))

            val secondOffsets = storage.getDocumentTermOffsets(id, "test2").iterator().asSequence().toSet()
            assert(secondOffsets.contains(15))

            storage.removeDocument(id)
            assertFailsWith<DocumentNotFoundException> { storage.getDocumentById(id) }
        }
    }

    @Test
    fun checkExceptions() {
        runBlocking {
            val storage = RAMDocumentStorage()

            assertFailsWith<DocumentNotFoundException> { storage.getDocumentById(0) }
            assertFailsWith<DocumentNotFoundException> { storage.getDocumentTerms(0) }
            assertFailsWith<DocumentNotFoundException> { storage.getDocumentTermOffsets(0, "test") }
        }
    }


}