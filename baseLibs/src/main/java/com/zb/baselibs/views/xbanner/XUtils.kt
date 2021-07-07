package com.zb.baselibs.views.xbanner

import android.widget.ImageView
import com.zb.baselibs.R
import com.zb.baselibs.bean.Ads
import java.util.*

object XUtils {
    private val imageList = ArrayList<String>()
    fun XBanner.showBanner(
        adList: List<Ads>, bannerType: Int, imageLoader: ImageLoader?,
        callBack: CallBack?, showPosition: XBanner.CallBack?,
    ) {
        imageList.clear()
        if (callBack != null) {
            for (item in adList) {
                imageList.add(item.smallImage)
            }
        }
        try {
            this.setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                .setAds(adList)
                .setBannerTypes(bannerType)
                .setImageLoader(imageLoader)
                .setBannerPageListener { item ->
                    callBack?.click(
                        item,
                        imageList
                    )
                }
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(
                    R.drawable.banner_circle_pressed,
                    R.drawable.banner_circle_unpressed
                )
                .isAutoPlay(false)
                .setCallBack(showPosition)
                .start()
        } catch (ignored: Exception) {
        }
    }

    interface CallBack {
        fun click(position: Int, imageList: ArrayList<String>?)
    }
}