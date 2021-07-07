package com.zb.baselibs.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.zb.baselibs.BR
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.vm.BaseLibsViewModel

class BaseAdapter<T> : BindingItemAdapter<T> {
    private var dialog: BaseDialogFragment? = null
    private var viewModel: BaseLibsViewModel? = null

    constructor(activity: AppCompatActivity?, layoutId: Int, list: MutableList<T>?, dialog: BaseDialogFragment?) :
            super(activity, layoutId, list) {
        this.dialog = dialog
    }

    constructor(activity: AppCompatActivity?, layoutId: Int, list: MutableList<T>?, viewModel: BaseLibsViewModel?) :
            super(activity, layoutId, list) {
        this.viewModel = viewModel
    }

    override fun onBind(holder: RecyclerHolder<ViewDataBinding?>?, t: T, position: Int) {
        if (holder != null) {
            holder.binding!!.setVariable(BR.item, t)
            if (dialog != null) {
                holder.binding.setVariable(BR.dialog, dialog)
            }
        }
    }
}