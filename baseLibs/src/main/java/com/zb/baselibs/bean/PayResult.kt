package com.zb.baselibs.bean

import android.text.TextUtils

class PayResult(rawResult: String) {
    var resultStatus = ""
    var result = ""
    var memo = ""

    init {
        if (!TextUtils.isEmpty(rawResult)) {
            val resultParams = rawResult.split(";".toRegex()).toTypedArray()
            for (resultParam in resultParams) {
                if (resultParam.startsWith("resultStatus")) {
                    resultStatus = gatValue(resultParam, "resultStatus")
                }
                if (resultParam.startsWith("result")) {
                    result = gatValue(resultParam, "result")
                }
                if (resultParam.startsWith("memo")) {
                    memo = gatValue(resultParam, "memo")
                }
            }
        }
    }

    private fun gatValue(content: String, key: String): String {
        val prefix = "$key={"
        return content.substring(
            content.indexOf(prefix) + prefix.length,
            content.lastIndexOf("}")
        )
    }
}