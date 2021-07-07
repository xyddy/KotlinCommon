package com.zb.baselibs.http.interceptor

import android.text.TextUtils
import com.zb.baselibs.utils.OkLog
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.internal.http.StatusLine
import okio.Buffer
import okio.GzipSource
import okio.source
import java.io.IOException
import java.net.HttpURLConnection
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit

open class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNs = System.nanoTime()
        val response = chain.proceed(request)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()
        val okLog = OkLog()


        okLog.start("Response ↓↓↓")
        okLog.log("Code-->" + response.code)
        okLog.log("URL-->" + response.request.url)
        okLog.log("TimeToken-->" + tookMs + "ms")
        if (hasBody(response)) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            var buffer = source.buffer
            var charset = Charset.forName("UTF-8")
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = try {
                    contentType.charset(charset)
                } catch (e: UnsupportedCharsetException) {
                    okLog.end("Response ↑↑↑")
                    return response
                }
            }
            if (contentLength != 0L && contentLength < 32 * 1024 && bodyIsText(contentType)) {
                if (bodyEncodedGzip(response.headers)) {
                    buffer = decodeGzip(buffer)
                }
                okLog.log("________________________________________________________")
                okLog.log("*******************ResponseBody*************************")
                val str = buffer.clone().readString(charset!!)
                okLog.log(str)
            }
            okLog.log("ResponseBody-->" + buffer.size + "byte")
        }
        okLog.end("Response ↑↑↑")
        return response
    }

    open fun bodyEncodedGzip(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && contentEncoding.equals("gzip", ignoreCase = true)
    }

    open fun bodyIsText(contentType: MediaType?): Boolean {
        return contentType != null && ("text" == contentType.type || "json" == contentType.subtype || contentType.subtype.contains("form"))
    }

    @Throws(IOException::class)
    open fun decodeGzip(buffer: Buffer): Buffer {
        val gzipSource = GzipSource(buffer.clone().inputStream().source())
        val count = buffer.size
        val resultBuffer = Buffer()
        gzipSource.read(resultBuffer, count)
        gzipSource.close()
        return resultBuffer
    }

    open fun hasBody(response: Response): Boolean {
        if (response.request.method == "HEAD") {
            return false
        }
        val responseCode = response.code
        return if ((responseCode < StatusLine.HTTP_CONTINUE || responseCode >= 200)
                && responseCode != HttpURLConnection.HTTP_NO_CONTENT && responseCode != HttpURLConnection.HTTP_NOT_MODIFIED) {
            true
        } else contentLength(response.headers) != -1L || "chunked".equals(response.header("Transfer-Encoding"), ignoreCase = true)
    }

    open fun contentLength(headers: Headers): Long {
        var length = headers["Content-Length"]
        if (TextUtils.isEmpty(length)) {
            return -1
        }
        length = length!!.trim { it <= ' ' }
        return try {
            length.toLong()
        } catch (e: NumberFormatException) {
            -1
        }
    }

}