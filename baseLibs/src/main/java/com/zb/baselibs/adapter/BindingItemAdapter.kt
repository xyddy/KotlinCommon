package com.zb.baselibs.adapter

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

abstract class BindingItemAdapter<T>(
    activity: AppCompatActivity?,
    @LayoutRes layoutId: Int,
    list: MutableList<T>?
) :
    RecyclerAdapter<T, ViewDataBinding?>(activity, list, layoutId)
