package com.zb.baselibs.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.zb.baselibs.R

abstract class BaseDialogFragment : DialogFragment {
    var activity: AppCompatActivity
    private var mIsKeyCanBack = true
    private var mIsOutCanBack = true

    constructor(activity: AppCompatActivity) : super() {
        this.activity = activity
    }

    constructor(
        activity: AppCompatActivity,
        mIsKeyCanBack: Boolean,
        mIsOutCanBack: Boolean,
    ) : super() {
        this.activity = activity
        this.mIsKeyCanBack = mIsKeyCanBack
        this.mIsOutCanBack = mIsOutCanBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setAnim()
        setCanCancel()
        val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            layoutId, container, false
        )
        setDataBinding(dataBinding)
        initUI()
        return dataBinding.root
    }

    private fun setAnim() {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.attributes.windowAnimations = R.style.Animation_Dialog
    }

    private var win: Window? = null
    override fun onStart() {
        super.onStart()
        win = dialog!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        win!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        val params = win!!.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win!!.attributes = params
    }

    fun cleanPadding() {
        win!!.decorView.setPadding(0, 0, 0, 0)
    }

    @JvmOverloads
    fun center(ratio: Double = 0.75) {
        val dialog = dialog
        val params = dialog!!.window!!.attributes
        params.gravity = Gravity.CENTER
        dialog.window!!.attributes = params
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window!!.setLayout(
            (dm.widthPixels * ratio).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * 设置 可取消相关
     */
    private fun setCanCancel() {
        dialog!!.setCanceledOnTouchOutside(mIsOutCanBack) //弹出框外面是否可取消
        dialog!!.setOnKeyListener { dialog: DialogInterface?, keyCode: Int, event: KeyEvent? ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return@setOnKeyListener !mIsKeyCanBack //return true 不往上传递则关闭不了，默认是可以取消，即return false
            } else {
                return@setOnKeyListener false
            }
        }
    }

    abstract val layoutId: Int

    abstract fun setDataBinding(viewDataBinding: ViewDataBinding?)
    abstract fun initUI()

    fun showAllowingStateLoss(manager: FragmentManager, tag: String?) {
        try {
            val dismissed = DialogFragment::class.java.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            dismissed[this] = false
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        try {
            val shown = DialogFragment::class.java.getDeclaredField("mShownByMe")
            shown.isAccessible = true
            shown[this] = true
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }
}