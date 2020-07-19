package org.indexer.index.document

import org.indexer.document.Document
import org.indexer.index.inverted.ReadWriteInvertedIndex
import java.util.concurrent.ConcurrentHashMap

data class RAMDocumentEntry(
    val document: Document,
    val terms: MutableMap<String, ReadWriteInvertedIndex<Long>> = ConcurrentHashMap()
)