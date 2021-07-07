package com.zb.baselibs.activity

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.zb.baselibs.http.viewmodel.IUIActionEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * @Author: leavesC
 * @Date: 2020/10/22 10:27
 * @Desc: BaseActivity
 * @GitHub：https://github.com/leavesC
 */
open class BaseReactiveFragment : Fragment(), IUIActionEventObserver {

    override val lifecycleSupportedScope: CoroutineScope
        get() = lifecycleScope

    override val lContext: Context?
        get() = activity

    override val lLifecycleOwner: LifecycleOwner
        get() = this

    private var loadDialog: ProgressDialog? = null

    override fun showLoading(job: Job?, msg: String?) {
        dismissLoading()
        loadDialog = ProgressDialog(lContext).apply {
            setMessage(msg)
            setCancelable(true)
            setCanceledOnTouchOutside(false)
            //用于实现当弹窗销毁的时候同时取消网络请求
            setOnDismissListener {
                job?.cancel()
            }
            show()
        }
    }

    override fun dismissLoading() {
        loadDialog?.takeIf { it.isShowing }?.dismiss()
        loadDialog = null
    }

    override fun showToast(msg: String) {
        if (msg.isNotBlank()) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun finishView() {
        activity!!.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissLoading()
    }

}