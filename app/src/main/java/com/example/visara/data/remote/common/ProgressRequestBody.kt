package com.example.visara.data.remote.common

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.source
import java.io.File

class ProgressRequestBody(
    private val file: File,
    private val mediaType: MediaType?,
    private val progressListener: (percent: Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? {
        return mediaType
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val buffer = Buffer()
        file.source().use { source ->
            val totalLength = contentLength()
            var totalBytesRead: Long = 0
            var lastReportedPercent = 0
            var lastReportTime = System.currentTimeMillis()

            val bufferSize = 8 * 1024L // Read 8 KB per chunk
            var read: Long

            while (source.read(buffer, bufferSize).also { read = it } != -1L) {
                sink.write(buffer, read)
                totalBytesRead += read

                val currentPercent = (totalBytesRead * 100 / totalLength).toInt()
                val currentTime = System.currentTimeMillis()
                val percentDelta = currentPercent - lastReportedPercent

                // Only report if:
                // - Percent changed at least 5%, or
                // - At least 300ms passed since last report
                if (percentDelta >= 5 || currentTime - lastReportTime >= 300) {
                    lastReportedPercent = currentPercent
                    lastReportTime = currentTime
                    progressListener(currentPercent.coerceIn(0, 100))
                }
            }

            // Ensure the listener is notified with 100% when upload finishes
            if (lastReportedPercent < 100) {
                progressListener(100)
            }
        }
    }
}
