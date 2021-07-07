package com.zb.baselibs.views.imagebrowser

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.views.imagebrowser.listener.ImageEngine
import com.zb.baselibs.views.imagebrowser.listener.OnClickListener
import com.zb.baselibs.views.imagebrowser.listener.OnLongClickListener
import com.zb.baselibs.views.imagebrowser.model.ImageBrowserConfig

object MNImage {

    lateinit var imageBrowserConfig: ImageBrowserConfig

    fun imageBrowser(
        sourceImageList: ArrayList<String>,
        index: Int,
        callBack: MNImageBrowser.StartBack,
        clickListener: OnClickListener?,
        longClickListener: OnLongClickListener?
    ) {
        if (clickListener != null) {
            if (longClickListener != null) {
                MNImageBrowser.with()
                    .setTransformType(ImageBrowserConfig.TransformType.TransformDepthPage)
                    .setIndicatorType(ImageBrowserConfig.IndicatorType.IndicatorNumber)
                    .setCurrentPosition(index)
                    .setImageEngine(object : ImageEngine {
                        override fun loadImage(context: Context, url: String, imageView: ImageView) {
                            val cropOptions = RequestOptions().fitCenter()
                            Glide.with(context).asBitmap().load(url).apply(cropOptions)
                                .into(object : SimpleTarget<Bitmap?>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap?>?,
                                    ) {
                                        val width = resource.width
                                        val height = resource.height
                                        val para: ViewGroup.LayoutParams = imageView.layoutParams
                                        if (width >= height) {
                                            para.width = BaseApp.W
                                            para.height =
                                                (resource.height.toFloat() * BaseApp.W / resource.width
                                                    .toFloat()).toInt()
                                        } else {
                                            para.width =
                                                (resource.width.toFloat() * BaseApp.H / resource.height
                                                    .toFloat()).toInt()
                                            para.height = BaseApp.H
                                        }
                                        imageView.layoutParams = para
                                        imageView.setImageBitmap(resource)
                                    }
                                })
                        }
                    })
                    .setOnClickListener(clickListener)
                    .setOnLongClickListener(longClickListener)
                    .setImageList(sourceImageList)
                    .show(callBack)
            }
        }
    }
}