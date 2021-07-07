package com.zb.baselibs.views.imagebrowser

import com.zb.baselibs.views.imagebrowser.listener.ImageEngine
import com.zb.baselibs.views.imagebrowser.listener.OnClickListener
import com.zb.baselibs.views.imagebrowser.listener.OnLongClickListener
import com.zb.baselibs.views.imagebrowser.model.ImageBrowserConfig
import java.util.*

class MNImageBrowser private constructor() {
    private val imageBrowserConfig: ImageBrowserConfig = ImageBrowserConfig()

    companion object {
        fun with(): MNImageBrowser {
            return MNImageBrowser()
        }
    }

    fun setImageList(imageList: ArrayList<String>): MNImageBrowser {
        imageBrowserConfig.imageList = imageList
        return this
    }

    fun setCurrentPosition(position: Int): MNImageBrowser {
        imageBrowserConfig.position = position
        return this
    }

    fun setTransformType(transformType: ImageBrowserConfig.TransformType): MNImageBrowser {
        imageBrowserConfig.transformType = transformType
        return this
    }

    fun setImageEngine(imageEngine: ImageEngine): MNImageBrowser {
        imageBrowserConfig.imageEngine = imageEngine
        return this
    }

    fun setOnClickListener(onClickListener: OnClickListener): MNImageBrowser {
        imageBrowserConfig.onClickListener = onClickListener
        return this
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener): MNImageBrowser {
        imageBrowserConfig.onLongClickListener = onLongClickListener
        return this
    }

    fun setIndicatorType(indicatorType: ImageBrowserConfig.IndicatorType): MNImageBrowser {
        imageBrowserConfig.indicatorType = indicatorType
        return this
    }


    fun show(startBack: StartBack) {
        //判断是不是空
        if (imageBrowserConfig.imageList.size <= 0) {
            return
        }
        if (imageBrowserConfig.imageEngine == null) {
            return
        }
        MNImage.imageBrowserConfig = imageBrowserConfig
        startBack.onStartActivity()
    }

    interface StartBack {
        fun onStartActivity()
    }


}
