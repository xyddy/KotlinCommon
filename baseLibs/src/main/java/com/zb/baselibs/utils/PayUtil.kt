package com.zb.baselibs.utils

import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alipay.sdk.app.PayTask
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.base.EvenConfig
import com.zb.baselibs.bean.AliPay
import com.zb.baselibs.bean.PayResult
import com.zb.baselibs.bean.WXPay
import org.simple.eventbus.EventBus

fun wxPay(it: WXPay) {
    val req = PayReq()
    req.appId = it.appid
    req.partnerId = it.partnerid
    req.prepayId = it.prepayid
    req.nonceStr = it.noncestr
    req.timeStamp = it.timestamp
    req.packageValue = "Sign=WXpay"
    req.sign = it.sign
    WXAPIFactory.createWXAPI(BaseApp.context, it.appid).sendReq(req)
}

fun aliPay(activity: AppCompatActivity, it: AliPay) {
    BaseApp.fixedThreadPool.execute {
        // 构造PayTask 对象
        val alipay = PayTask(activity)
        // 调用支付接口，获取支付结果
        val result: String = alipay.pay(it.payInfo)
        activity.runOnUiThread {
            val payResult = PayResult(result)
            val resultStatus: String = payResult.resultStatus
            if (TextUtils.equals(resultStatus, "9000")) {
                Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show()
                EventBus.getDefault().post("支付成功", EvenConfig.payBack)
            } else {
                if (TextUtils.equals(resultStatus, "8000")) {
                    Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}