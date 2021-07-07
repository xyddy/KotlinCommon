package com.zb.baselibs.utils

import com.zb.baselibs.app.BaseApp
import java.io.IOException
import java.io.InputStream
import java.util.*

object SimulateNetAPI {
    /**
     * 获取去最原始的数据信息
     *
     * @return json data
     */
    fun getOriginalFundData(fileName: String?): String? {
        val input: InputStream
        try {
            input = fileName?.let { BaseApp.context.assets.open(it) }!!
            return convertStreamToString(input)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * input 流转换为字符串
     *
     * @param is
     * @return
     */
    private fun convertStreamToString(`is`: InputStream): String? {
        var s: String? = null
        try {
            val scanner = Scanner(`is`, "UTF-8").useDelimiter("\\A")
            if (scanner.hasNext()) {
                s = scanner.next()
            }
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return s
    }
}