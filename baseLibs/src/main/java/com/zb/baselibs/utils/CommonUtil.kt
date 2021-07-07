package com.zb.baselibs.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.igexin.sdk.PushManager
import com.jiang.awesomedownloader.core.AwesomeDownloader
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import com.zb.baselibs.R
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.CityInfo
import com.zb.baselibs.bean.DistrictInfo
import com.zb.baselibs.bean.ProvinceInfo
import com.zb.baselibs.db.CityDb
import com.zb.baselibs.db.DistrictDb
import com.zb.baselibs.db.ProvinceDb
import org.json.JSONArray
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 初始化第三方配置
 */
fun initUtil(activity:AppCompatActivity) {
    // 个推初始化
    PushManager.getInstance().initialize(BaseApp.context)
    // 友盟初始化
    UMConfigure.init(
        BaseApp.context, BaseApp.ymData[0], BaseApp.projectName,
        UMConfigure.DEVICE_TYPE_PHONE, ""
    )
    // 选用AUTO页面采集模式
    MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    // 打开统计SDK调试模式
    UMConfigure.setLogEnabled(true)
    // 微信设置
    PlatformConfig.setWeixin(BaseApp.ymData[1], BaseApp.ymData[2])
    PlatformConfig.setWXFileProvider("${BaseApp.context.packageName}.fileprovider")
    // QQ设置
    PlatformConfig.setQQZone(BaseApp.ymData[3], BaseApp.ymData[4])
    PlatformConfig.setQQFileProvider("${BaseApp.context.packageName}.fileprovider")

    AwesomeDownloader.initWithDefaultMode(activity)
    AwesomeDownloader.option.showNotification = false
}

/**
 * MD5加密
 */
fun encode(password: String): String {
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
        val digest: ByteArray = instance.digest(password.toByteArray())//对字符串加密，返回字节数组
        var sb = StringBuffer()
        for (b in digest) {
            var i: Int = b.toInt() and 0xff//获取低八位有效值
            var hexString = Integer.toHexString(i)//将整数转化为16进制
            if (hexString.length < 2) {
                hexString = "0$hexString"//如果是一位的话，补0
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return ""
}

/**
 * EditText的输出
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

/**
 * 创建通知
 */
fun createNotificationCompatBuilder(
    title: String, description: String,
): NotificationCompat.Builder {
    val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationCompat.Builder(
            BaseApp.context,
            getChannelId(BaseApp.context.notificationManager())
        )
    } else {
        NotificationCompat.Builder(BaseApp.context, "${BaseApp.context.packageName}.notification")
    }

    // 通知内容
    val mRemoteViews = RemoteViews(BaseApp.context.packageName, R.layout.remote_layout)
    mRemoteViews.setTextViewText(R.id.tv_content, description)
    mRemoteViews.setTextViewText(R.id.tv_title,
        if (title.isEmpty()) BaseApp.notificationChannelName else title)
    builder.setContent(mRemoteViews)
    builder.setOngoing(false)
    builder.setAutoCancel(true) // 设置这个标志当用户单击面板就可以让通知将自动取消
    builder.setDefaults(Notification.DEFAULT_ALL)
    builder.setSmallIcon(BaseApp.noticeLogo)
    return builder
}

/**
 * 创建通道
 */
@RequiresApi(api = Build.VERSION_CODES.O)
fun getChannelId(notificationManager: NotificationManager): String {
    val channel = NotificationChannel(
        "${BaseApp.context.packageName}.notification",
        BaseApp.notificationChannelName,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    channel.enableLights(true) //显示桌面红点
    channel.lightColor = Color.RED
    channel.setShowBadge(true)
    notificationManager.createNotificationChannel(channel)
    return channel.id
}

/**
 * 播放通知声音
 */
fun msgSound() {
    val mPlayer: MediaPlayer = MediaPlayer.create(BaseApp.context, R.raw.msn)
    mPlayer.start()
    // 播放声音
    BaseApp.fixedThreadPool.execute {
        SystemClock.sleep(800)
        mPlayer.stop()
        mPlayer.release() //释放资源
    }
}

/**
 * 是否后台运行
 */
fun isAppRunning(): Boolean {
    val am = BaseApp.context.activityManager()
    val list = am.getRunningTasks(100)
    var isAppRunning = false
    for (info in list) {
        if (info.topActivity!!.packageName == BaseApp.context.packageName || info.baseActivity!!.packageName == BaseApp.context.packageName) {
            isAppRunning = true
            break
        }
    }
    return isAppRunning
}


/**
 * 获取城市信息
 */
fun getArea() {
    if (!ProvinceDb.instance.isExist()) {
        BaseApp.fixedThreadPool.execute {
            val data = SimulateNetAPI.getOriginalFundData("cityData.json")
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val provinceJSON = array.optJSONObject(i)
                // 省
                val provinceInfo = ProvinceInfo(
                    provinceJSON.optString("value").toLong(),
                    provinceJSON.optString("label")
                )
                ProvinceDb.instance.insertData(provinceInfo)

                // 市
                val cityArray = provinceJSON.optJSONArray("children")
                if (cityArray != null) {
                    for (j in 0 until cityArray.length()) {
                        val cityJSON = cityArray.optJSONObject(j)
                        val cityInfo = CityInfo(
                            cityJSON.optString("value").toLong(),
                            cityJSON.optString("label"),
                            provinceInfo.provinceId
                        )
                        CityDb.instance.insertData(cityInfo)

                        // 地区
                        val districtArray = cityJSON.optJSONArray("children")
                        if (districtArray != null) {
                            for (k in 0 until districtArray.length()) {
                                val districtJSON = districtArray.optJSONObject(k)
                                val districtInfo = DistrictInfo(
                                    districtJSON.optString("value").toLong(),
                                    districtJSON.optString("label"),
                                    cityInfo.cityId
                                )
                                DistrictDb.instance.insertData(districtInfo)
                            }
                        }
                    }
                }
            }
        }
    }
}