package com.zb.baselibs.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.zb.baselibs.app.BaseApp

abstract class BaseReceiver(var context: Context, name: String) : BroadcastReceiver() {

    abstract override fun onReceive(context: Context, intent: Intent)

    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        context.registerReceiver(this, IntentFilter("${BaseApp.projectName}_$name"))
    }
}
