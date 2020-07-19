package org.indexer.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.indexer.document.TermInfo
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FileInput(private val path: String, private val bufferSize: Int) : Input {

    override suspend fun getInputFlow(): Flow<TermInfo> = flow {
        AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.READ).use {
            val buffer = ByteBuffer.allocate(bufferSize)
            var offset: Long = 0
            var data: ByteBuffer?

            while (true) {
                data = readAsync(it, buffer, offset)
                if (data == null) {
                    break
                }

                data.flip()
                emit(TermInfo(StandardCharsets.UTF_8.decode(data).toString().intern(), offset))
                offset += data.limit()
                data.clear()
            }
        }
    }

    private suspend fun readAsync(channel: AsynchronousFileChannel, buffer: ByteBuffer, pos: Long): ByteBuffer? {
        return suspendCoroutine {
            channel.read<ByteBuffer>(buffer, pos, buffer, object : CompletionHandler<Int, ByteBuffer> {

                override fun failed(exc: Throwable?, attachment: ByteBuffer?) {
                    it.resumeWithException(exc ?: IllegalStateException())
                }

                override fun completed(result: Int?, attachment: ByteBuffer?) {
                    if (result == -1) {
                        it.resume(null)
                        return
                    }
                    it.resume(attachment)
                }

            })
        }
    }
}