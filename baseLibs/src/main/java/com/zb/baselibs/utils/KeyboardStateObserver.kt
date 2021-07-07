package com.zb.baselibs.utils

import android.R
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class KeyboardStateObserver private constructor(activity: AppCompatActivity) {
    private var isFull = false
    private val mChildOfContent: View
    private var usableHeightPrevious = 0
    private var listener: OnKeyboardVisibilityListener? = null
    fun setKeyboardVisibilityListener(listener: OnKeyboardVisibilityListener?, isFull: Boolean) {
        this.listener = listener
        this.isFull = isFull
    }

    private fun possiblyResizeChildOfContent() {
        val usableHeightNow = computeUsableHeight(mActivity)
        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard = mChildOfContent.rootView.height
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            if (heightDifference > usableHeightSansKeyboard / 5) {
                if (listener != null) {
                    listener!!.onKeyboardHeight(heightDifference)
                }
            } else {
                if (listener != null) {
                    listener!!.onKeyboardHide()
                }
            }
            usableHeightPrevious = usableHeightNow
            Log.d(
                TAG,
                "usableHeightNow: $usableHeightNow | usableHeightSansKeyboard:$usableHeightSansKeyboard | heightDifference:$heightDifference"
            )
        }
    }

    private fun computeUsableHeight(activity: AppCompatActivity?): Int {
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        val point = getNavigationBarSize(activity)
        var bottomHeight = point.y
        if (bottomHeight < 100) bottomHeight *= 2
        return if (isFull) r.bottom + bottomHeight else r.bottom - r.top + bottomHeight // 全屏模式下： return r.bottom
    }

    fun interface OnKeyboardVisibilityListener {
        fun onKeyboardHeight(height: Int)
        fun onKeyboardHide() {}
    }

    //获取是否存在NavigationBar
    private fun checkDeviceHasNavigationBar(): Boolean {
        return dpi - mActivity!!.windowManager.defaultDisplay.height > 0
    }

    private val dpi: Int
        get() {
            var dpi = 0
            val display: Display = mActivity!!.windowManager.defaultDisplay
            val dm = DisplayMetrics()
            val c: Class<*>
            try {
                c = Class.forName("android.view.Display")
                val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
                method.invoke(display, dm)
                dpi = dm.heightPixels
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return dpi
        }

    companion object {
        private val TAG = KeyboardStateObserver::class.java.simpleName
        private var mActivity: AppCompatActivity? = null
        fun getKeyboardStateObserver(activity: AppCompatActivity): KeyboardStateObserver {
            mActivity = activity
            return KeyboardStateObserver(activity)
        }

        fun getNavigationBarSize(context: AppCompatActivity?): Point {
            val appUsableSize = getAppUsableScreenSize(context)
            val realScreenSize = getRealScreenSize(context)

            // navigation bar on the right
            if (appUsableSize.x < realScreenSize.x) {
                return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
            }

            // navigation bar at the bottom
            return if (appUsableSize.y < realScreenSize.y) {
                Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
            } else Point()

            // navigation bar is not present
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        fun getAppUsableScreenSize(context: AppCompatActivity?): Point {
            val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size
        }

        fun getRealScreenSize(context: AppCompatActivity?): Point {
            val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getRealSize(size)
            return size
        }
    }

    init {
        val content: FrameLayout = activity.findViewById<FrameLayout>(R.id.content)
        mChildOfContent = content.getChildAt(0)
        mChildOfContent.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
    }
}