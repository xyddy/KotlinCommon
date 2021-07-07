package com.zb.baselibs.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.zb.baselibs.R
import com.zb.baselibs.adapter.BaseAdapter
import com.zb.baselibs.bean.ShareItem
import com.zb.baselibs.databinding.DfYmShareBinding
import com.zb.baselibs.dialog.BaseDialogFragment

class YMShareDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    lateinit var binding: DfYmShareBinding
    lateinit var adapter: BaseAdapter<ShareItem>
    private val shareList = ArrayList<ShareItem>()


    override val layoutId: Int
        get() = R.layout.df_ym_share

    override fun onStart() {
        super.onStart()
        cleanPadding()
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfYmShareBinding
    }

    override fun initUI() {

        shareList.add(ShareItem(R.drawable.share_wx_ico, "微信"))
        shareList.add(ShareItem(R.drawable.share_wxcircle_ico, "朋友圈"))
        shareList.add(ShareItem(R.drawable.share_qq_ico, "QQ"))
        shareList.add(ShareItem(R.drawable.share_qqzore_ico, "QQ空间"))

        adapter = BaseAdapter(activity, R.layout.item_ym_share, shareList, this)
        binding.dialog = this
    }


    fun show(manager: FragmentManager?) {
        show(manager!!, "YMShareDF")
    }

    fun cancel(view: View) {
        dismiss()
    }
}