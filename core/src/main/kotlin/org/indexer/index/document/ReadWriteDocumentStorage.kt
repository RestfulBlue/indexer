package org.indexer.index.document

/**
 * Interface which must be implemented by storages with support both read and write operations
 */
interface ReadWriteDocumentStorage : ReadableDocumentStorage, WriteableDocumentStorage