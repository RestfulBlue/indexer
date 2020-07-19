package org.indexer.index

import org.indexer.index.document.WriteableDocumentStorage
import org.indexer.index.term.WriteableTermStorage

/**
* Index itself is just a holder for different storages.
* Writeable index should have document and termStorage which supports write operations
*/
interface WriteableIndex {

    fun documentStorage(): WriteableDocumentStorage

    fun termStorage(): WriteableTermStorage
}