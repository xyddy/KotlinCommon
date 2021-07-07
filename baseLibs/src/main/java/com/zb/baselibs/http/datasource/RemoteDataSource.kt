package com.zb.baselibs.http.datasource

import android.util.Log
import com.zb.baselibs.bean.IHttpWrapBean
import com.zb.baselibs.http.callback.RequestCallback
import com.zb.baselibs.http.exception.ServerCodeBadException
import com.zb.baselibs.http.viewmodel.IUIActionEvent
import kotlinx.coroutines.Job

/**
 * @Author: leavesC
 * @Date: 2020/5/4 0:55
 * @Desc:
 * @GitHub：https://github.com/leavesC
 */
abstract class RemoteDataSource<Api : Any>(
    iUiActionEvent: IUIActionEvent?,
    apiServiceClass: Class<Api>
) : BaseRemoteDataSource<Api>(iUiActionEvent, apiServiceClass) {

    fun <Data> enqueueLoading(
        apiFun: suspend Api.() -> IHttpWrapBean<Data>,
        msg: String = "",
        baseUrl: String = "",
        callbackFun: (RequestCallback<Data>.() -> Unit)? = null
    ): Job {
        return enqueue(
            apiFun = apiFun,
            showLoading = true,
            msg = msg,
            baseUrl = baseUrl,
            callbackFun = callbackFun
        )
    }

    fun <Data> enqueue(
        apiFun: suspend Api.() -> IHttpWrapBean<Data>,
        showLoading: Boolean = false,
        msg: String = "",
        baseUrl: String = "",
        callbackFun: (RequestCallback<Data>.() -> Unit)? = null
    ): Job {
        return launchMain {
            val callback = if (callbackFun == null) null else RequestCallback<Data>().apply {
                callbackFun.invoke(this)
            }
            try {
                if (showLoading) {
                    showLoading(coroutineContext[Job], msg)
                }
                callback?.onStart?.invoke()
                val response: IHttpWrapBean<Data>
                try {
                    response = apiFun.invoke(getApiService(baseUrl))
                    Log.e("TAG", response.toString())

                    if (response.httpIsFailed) {
                        throw ServerCodeBadException(response)
                    }
                } catch (throwable: Throwable) {
                    handleException(throwable, callback)
                    return@launchMain
                }
                onGetResponse(callback, response.data)
            } finally {
                try {
                    callback?.onFinally?.invoke()
                } finally {
                    if (showLoading) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    fun <Data> enqueueOriginLoading(
        apiFun: suspend Api.() -> Data,
        msg: String = "",
        baseUrl: String = "",
        callbackFun: (RequestCallback<Data>.() -> Unit)? = null
    ): Job {
        return enqueueOrigin(
            apiFun = apiFun,
            msg = msg,
            showLoading = true,
            baseUrl = baseUrl,
            callbackFun = callbackFun
        )
    }

    fun <Data> enqueueOrigin(
        apiFun: suspend Api.() -> Data,
        msg: String = "",
        showLoading: Boolean = false,
        baseUrl: String = "",
        callbackFun: (RequestCallback<Data>.() -> Unit)? = null
    ): Job {
        return launchMain {
            val callback = if (callbackFun == null) null else RequestCallback<Data>().apply {
                callbackFun.invoke(this)
            }
            try {
                if (showLoading) {
                    showLoading(coroutineContext[Job], msg)
                }
                callback?.onStart?.invoke()
                val response: Data
                try {
                    response = apiFun.invoke(getApiService(baseUrl))
                } catch (throwable: Throwable) {
                    handleException(throwable, callback)
                    return@launchMain
                }
                onGetResponse(callback, response)
            } finally {
                try {
                    callback?.onFinally?.invoke()
                } finally {
                    if (showLoading) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    private suspend fun <Data> onGetResponse(callback: RequestCallback<Data>?, data: Data) {
        callback?.let {
            withNonCancellable {
                callback.onSuccess?.let {
                    withMain {
                        it.invoke(data)
                    }
                }
                callback.onSuccessIO?.let {
                    withIO {
                        it.invoke(data)
                    }
                }
            }

        }
    }
}