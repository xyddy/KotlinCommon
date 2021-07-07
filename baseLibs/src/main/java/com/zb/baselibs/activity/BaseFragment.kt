package com.zb.baselibs.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseFragment : BaseReactiveFragment() {

    lateinit var mBinding: ViewDataBinding
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, getRes(), container, false)
            rootView = mBinding.root
            initView()
        }
        return rootView
    }

    abstract fun getRes(): Int

    abstract fun initView()
}