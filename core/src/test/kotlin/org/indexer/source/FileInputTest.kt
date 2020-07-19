package org.indexer.source

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class FileInputTest {

    @Test
    fun testInput() {
        runBlocking {
            val url = FileInputTest::class.java.classLoader.getResource("animals.txt")

            for(i in 1 until 100) {
                val path = Paths.get(url.toURI())
                val input = FileInput(path.toFile().absolutePath, i)

                val fileData = String(Files.readAllBytes(path))

                input.getInputFlow().collect {
                    val from = it.offset.toInt()
                    val to = it.offset.toInt() + it.term.length
                    assertEquals(
                        if (to >= fileData.length) fileData.substring(from) else fileData.substring(from, to),
                        it.term
                    )
                }
            }
        }
    }

}