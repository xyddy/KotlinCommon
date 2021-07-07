package com.zb.baselibs.views.imagebrowser.listener

import android.content.Context
import android.widget.ImageView

interface ImageEngine {
    fun loadImage(context: Context, url: String, imageView: ImageView)
}