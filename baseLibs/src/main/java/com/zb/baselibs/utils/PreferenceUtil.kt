package com.zb.baselibs.utils

import android.content.Context.MODE_PRIVATE
import com.zb.baselibs.app.BaseApp

//存储key对应的数据
fun saveString(key: String, info: String) {
    val sharedPreferences = BaseApp.context.getSharedPreferences(key, MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(key, info)
    editor.apply()
}

//取key对应的数据
fun getString(key: String): String {
    return BaseApp.context.getSharedPreferences(key, MODE_PRIVATE).getString(key, "") ?: ""

}

//存储key对应的数据
fun saveInteger(key: String, info: Int) {
    val sharedPreferences = BaseApp.context.getSharedPreferences(key, MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt(key, info)
    editor.apply()
}

//取key对应的数据
fun getInteger(key: String): Int {
    return BaseApp.context.getSharedPreferences(key, MODE_PRIVATE).getInt(key, 0)
}

//存储key对应的数据
fun saveLong(key: String, info: Long) {
    val sharedPreferences = BaseApp.context.getSharedPreferences(key, MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong(key, info)
    editor.apply()
}

//取key对应的数据
fun getLong(key: String): Long {
    return BaseApp.context.getSharedPreferences(key, MODE_PRIVATE).getLong(key, 0L)
}

//清空缓存对应key的数据
fun clearData(key: String) {
    BaseApp.context.getSharedPreferences(key, MODE_PRIVATE).edit().clear().apply()
}