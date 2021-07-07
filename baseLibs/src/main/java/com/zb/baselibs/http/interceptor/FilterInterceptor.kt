package com.zb.baselibs.http.interceptor

import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.utils.getString
import com.zb.baselibs.utils.versionName
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @Author: leavesC
 * @Date: 2020/2/25 16:10
 * @Desc:
 * @GitHub：https://github.com/leavesC
 */
class FilterInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val httpBuilder = originalRequest.url.newBuilder()
        httpBuilder.addQueryParameter("pfDevice", "Android")
            .addQueryParameter("pfAppType", BaseApp.appType)
            .addQueryParameter("pfAppVersion", BaseApp.context.versionName())
            .addQueryParameter("fullVersion", BaseApp.context.versionName() + ".1")
            .addQueryParameter("userId", getLong("userId").toString())
            .addQueryParameter("sessionId", getString("sessionId"))
        val requestBuilder = originalRequest.newBuilder()
            .url(httpBuilder.build())
        return chain.proceed(requestBuilder.build())
    }

}
