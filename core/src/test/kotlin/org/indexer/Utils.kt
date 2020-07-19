package org.indexer

import org.indexer.index.ReadableIndex
import org.indexer.index.inverted.ReadableInvertedIndex
import org.indexer.read.filter.Filter
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

suspend fun assertThrow(excClass: KClass<*>, action: suspend () -> Unit): Exception {
    var exception: Exception? = null
    try {
        action()
    } catch (e: Exception) {
        exception = e
    }

    assertNotNull(exception, "Expected exception was not thrown")
    assertEquals(exception::class, excClass)
    return exception
}

fun <T> createInvertedFromList(list: List<T>): ReadableInvertedIndex<T> {
    return object : ReadableInvertedIndex<T> {
        override fun iterator(): Iterator<T> {
            return list.iterator()
        }
    }
}

fun createFilterFromList(list: List<Int>): Filter {
    return object : Filter {
        override fun filter(index: ReadableIndex): ReadableInvertedIndex<Int> {
            return object : ReadableInvertedIndex<Int> {
                override fun iterator(): Iterator<Int> {
                    return list.iterator()
                }
            }
        }

    }

}