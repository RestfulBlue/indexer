package org.indexer.index.inverted

/**
 * Interface which must implemented inverted index which supports both read and write operations
 */
interface ReadWriteInvertedIndex<T> : ReadableInvertedIndex<T>, WriteableInvertedIndex<T>