package com.zb.baselibs.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.zb.baselibs.app.BaseApp
import org.simple.eventbus.EventBus

abstract class BaseActivity : BaseReactiveActivity() {

    lateinit var mBinding: ViewDataBinding
    lateinit var activity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        BaseApp.addActivity(this)
        mBinding = DataBindingUtil.setContentView(this, getRes())
        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (needEvenBus())
            registerBus()

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needEvenBus())
            removeBus()
    }

    open fun registerBus() {
        EventBus.getDefault().register(this)
    }

    open fun removeBus() {
        EventBus.getDefault().unregister(this)
    }


    abstract fun needEvenBus(): Boolean
    abstract fun getRes(): Int
    abstract fun initView()
}