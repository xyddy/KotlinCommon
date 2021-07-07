package com.zb.baselibs.vm

import androidx.appcompat.app.AppCompatActivity

open class BaseLibsViewModel : BaseReactiveViewModel() {
    lateinit var activity: AppCompatActivity

    open fun onDestroy() {

    }
}