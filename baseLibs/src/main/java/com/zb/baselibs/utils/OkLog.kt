package com.zb.baselibs.utils

import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OkLog {
    private val tag = "OKHttp"
    private val lineSeparator = System.getProperty("line.separator")
    private val jsonIndent = 4
    private val suffix = ".java"

    fun log(msg: String) {
        var mMsg = msg
        if (TextUtils.isEmpty(mMsg)) {
            return
        }
        mMsg = mMsg.trim { it <= ' ' }
        val message: String
        message = try {
            when {
                mMsg.startsWith("{") -> {
                    val jsonObject = JSONObject(mMsg)
                    jsonObject.toString(jsonIndent)
                }
                mMsg.startsWith("[") -> {
                    val jsonArray = JSONArray(mMsg)
                    jsonArray.toString(jsonIndent)
                }
                else -> {
                    mMsg
                }
            }
        } catch (e: JSONException) {
            mMsg
        }
        val lines = lineSeparator?.toRegex()?.let { message.split(it).toTypedArray() }
        if (lines != null) {
            for (line in lines) {
                Log.i(tag, "║ $line")
            }
        }
    }

    fun start(hint: String) {
        printLine(tag, true, hint)
        log(getStackTrace() + lineSeparator)
    }

    fun end(hint: String) {
        printLine(tag, false, hint)
    }

    private fun printLine(tag: String, isTop: Boolean, hint: String) {
        var hint: String? = hint
        val top = "╔════════ %s ══════════════════════════════════"
        val bot = "╚════════ %s ══════════════════════════════════"
        hint = if (TextUtils.isEmpty(hint)) "════════" else hint
        val fotmated = String.format(if (isTop) top else bot, hint)
        Log.i(tag, fotmated)
    }


    private fun getStackTrace(): String {
        val index = 4
        val stackTrace = Thread.currentThread().stackTrace
        val targetElement = stackTrace[index]
        var className = targetElement.className
        val classNameInfo = className.split("\\.".toRegex()).toTypedArray()
        if (classNameInfo.isNotEmpty()) {
            className = classNameInfo[classNameInfo.size - 1] + suffix
        }
        if (className.contains("$")) {
            className = className.split("\\$".toRegex()).toTypedArray()[0] + suffix
        }
        val methodName = targetElement.methodName
        var lineNumber = targetElement.lineNumber
        if (lineNumber < 0) {
            lineNumber = 0
        }
        return "[ ($className:$lineNumber)#$methodName ] "
    }
}