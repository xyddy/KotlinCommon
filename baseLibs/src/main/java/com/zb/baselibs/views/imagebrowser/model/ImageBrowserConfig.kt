package com.zb.baselibs.views.imagebrowser.model

import com.zb.baselibs.views.imagebrowser.listener.ImageEngine
import com.zb.baselibs.views.imagebrowser.listener.OnClickListener
import com.zb.baselibs.views.imagebrowser.listener.OnLongClickListener
import java.util.*

class ImageBrowserConfig {
    //枚举类型
    enum class TransformType {
        TransformDefault, TransformDepthPage, TransformRotateDown, TransformRotateUp, TransformZoomIn, TransformZoomOutSlide, TransformZoomOut
    }

    //枚举类型
    enum class IndicatorType {
        IndicatorCircle, IndicatorNumber
    }

    var position: Int = 0
    var transformType: TransformType = TransformType.TransformDefault
    var indicatorType: IndicatorType = IndicatorType.IndicatorNumber
    var imageList = ArrayList<String>()
    var imageEngine: ImageEngine? = null
    var onClickListener: OnClickListener? = null
    var onLongClickListener: OnLongClickListener? = null


}