package com.zb.baselibs.views.imagebrowser.listener

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

interface OnClickListener {
    fun onClick(activity: AppCompatActivity, view: ImageView, position: Int, url: String)
}