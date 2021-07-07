package com.zb.baselibs.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author: leavesC
 * @Date: 2020/4/30 15:22
 * @Desc:
 * @GitHub：https://github.com/leavesC
 */
class HttpWrapBean<T>(
    @SerializedName("code") var httpCode: Int = 0,
    @SerializedName("msg") var httpMsg: String? = "",
    @SerializedName("data") var httpData: T
) : IHttpWrapBean<T> {
    override val code: Int
        get() = httpCode

    override val msg: String
        get() = httpMsg ?: ""

    override val data: T
        get() = httpData

    override val httpIsSuccess: Boolean
        get() = code == 1 || msg == "success"

    override fun toString(): String {
        return "HttpResBean(code=$code, message=$msg, data=$data)"
    }
}