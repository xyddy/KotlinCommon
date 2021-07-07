package com.zb.baselibs.bean

data class WXPay(
    var timestamp: String = "",
    var sign: String = "",
    var partnerid: String = "",
    var noncestr: String = "",// 32位内的随机串，防重发
    var appid: String = "", // 应用唯一标识，在微信开放平台提交应用审核通过后获得
    var prepayid: String = "",
)

data class AliPay(var payInfo: String = "")
