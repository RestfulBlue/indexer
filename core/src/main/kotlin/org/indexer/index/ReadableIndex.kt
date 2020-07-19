package org.indexer.index

import org.indexer.index.document.ReadableDocumentStorage
import org.indexer.index.term.ReadableTermStorage

/**
 * Index itself is just a holder for different storages.
 * Readable index should have document and termStorage
 */
interface ReadableIndex {

    /**
     * @return storage which supports read operations
     */
    fun documentStorage() : ReadableDocumentStorage

    /**
     * @return storage which supports read operations
     */
    fun termStorage() : ReadableTermStorage

}