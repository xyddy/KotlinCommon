package com.zb.baselibs.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerHolder<B : ViewDataBinding?>(val binding: B) : RecyclerView.ViewHolder(
    binding!!.root
)