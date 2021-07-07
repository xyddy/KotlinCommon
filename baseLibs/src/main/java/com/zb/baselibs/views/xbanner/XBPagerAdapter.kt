package com.zb.baselibs.views.xbanner

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class XBPagerAdapter(listener: BannerPageListener?, imagecount: Int) :
    PagerAdapter() {
    var mData: MutableList<View>? = ArrayList()
    var mBannerPageListner: BannerPageListener?
    var mImageCount: Int
    var view: View? = null

    /**
     * Add data,will not clear the data already exists
     *
     * @param data the ImageViews to be added
     */
    fun addData(data: List<View>?) {
        if (mData != null) {
            mData!!.addAll(data!!)
            notifyDataSetChanged()
        }
    }

    /**
     * Reset the data
     */
    fun setData(data: List<View>?) {
        if (mData != null) {
            mData!!.clear()
            mData!!.addAll(data!!)
            notifyDataSetChanged()
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return mData!!.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        try {
            container.addView(mData!![position])
        } catch (e: IllegalStateException) {
            if (mData!![position].parent != null) {
                (mData!![position].parent as ViewGroup).removeView(mData!![position])
            }
            container.addView(mData!![position])
        }
        view = mData!![position]
        view!!.setOnClickListener { v: View? ->
            if (mBannerPageListner != null) {
                mBannerPageListner!!.onBannerClick(getTruePos(position))
            }
        }
        return mData!![position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}
    private fun getTruePos(pos: Int): Int {
        //get the position of the indicator
        var truepos = (pos - 1) % mImageCount
        if (truepos < 0) {
            truepos = mImageCount - 1
        }
        return truepos
    }

    fun releaseAdapter() {
        mBannerPageListner = null
        if (mData != null && mData!!.size > 0) {
            for (i in mData!!.indices) {
                mData!![i].setOnClickListener(null)
            }
            mData!!.clear()
        }
        if (view != null) view!!.setOnClickListener(null)
        view = null
    }

    fun interface BannerPageListener {
        fun onBannerClick(item: Int)
        fun onBannerDragging(item: Int) {}
        fun onBannerIdle(item: Int) {}
    }

    init {
        mBannerPageListner = listener
        mImageCount = imagecount
    }
}