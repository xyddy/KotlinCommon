package com.zb.baselibs.bean

import android.view.View

class Ads {
    var adId: Long = 0L //id
    var smallImage: String = "" //小图地址  //app的图片
    var bigImage: String = ""//大图地址
    var webImage: String = "" //网页端图片
    var outLink: String = "" //广告链接的地址
    var adTitle: String = ""//广告标题
    var adRes: Int = 0
    var view: View? = null
}
