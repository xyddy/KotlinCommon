package com.zb.baselibs.http

import android.widget.Toast
import com.google.gson.GsonBuilder
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.http.datasource.RemoteExtendDataSource
import com.zb.baselibs.http.interceptor.FilterInterceptor
import com.zb.baselibs.http.interceptor.LoggingInterceptor
import com.zb.baselibs.http.viewmodel.IUIActionEvent
import github.leavesc.monitor.MonitorInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * @Author: leavesC
 * @Date: 2020/6/23 0:37
 * @Desc:
 * @GitHub：https://github.com/leavesC
 */
class MainDataSource<Api : Any>(iActionEvent: IUIActionEvent?, apiServiceClass: Class<Api>) : RemoteExtendDataSource<Api>(
    iActionEvent,
    apiServiceClass
) {
    companion object {

        private val httpClient: OkHttpClient by lazy {
            createHttpClient()
        }

        private fun createHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                .readTimeout(60L, TimeUnit.SECONDS)
                .writeTimeout(60L, TimeUnit.SECONDS)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(FilterInterceptor())
                .addInterceptor(LoggingInterceptor())
                .addInterceptor(MonitorInterceptor(BaseApp.context))
            return builder.build()
        }
    }

    /**
     * 由子类实现此字段以便获取 baseUrl
     */
    override val baseUrl: String
        get() = BaseApp.baseUrl

    /**
     * 允许子类自己来实现创建 Retrofit 的逻辑
     * 外部无需缓存 Retrofit 实例，ReactiveHttp 内部已做好缓存处理
     * 但外部需要自己判断是否需要对 OKHttpClient 进行缓存
     * @param baseUrl
     */
    override fun createRetrofit(baseUrl: String): Retrofit {
        val mGson = GsonBuilder()
            .setLenient() // 设置GSON的非严格模式setLenient()
            .create()
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .build()
    }

    override fun showToast(msg: String) {
        Toast.makeText(BaseApp.context, msg, Toast.LENGTH_SHORT).show()
    }

}