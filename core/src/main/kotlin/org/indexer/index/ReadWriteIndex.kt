package org.indexer.index

import org.indexer.index.document.ReadWriteDocumentStorage
import org.indexer.index.term.ReadWriteTermStorage

/**
 * Index itself is just a holder for different storages.
 * ReadWrite index should have document and termStorage both supporting reading and write operations
 */
interface ReadWriteIndex : ReadableIndex, WriteableIndex {

    override fun documentStorage(): ReadWriteDocumentStorage

    override fun termStorage(): ReadWriteTermStorage

}