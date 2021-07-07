package com.zb.baselibs.views.xbanner

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.VideoView
import com.zb.baselibs.bean.Ads

fun interface ImageLoader {
    fun loadImages(context: Context?, ads: Ads?, image: ImageView?, position: Int)
    fun loadVideoViews(context: Context?, ads: Ads?, videoView: VideoView?) {}
    fun loadView(linearLayout: LinearLayout?, adView: View?) {}
}