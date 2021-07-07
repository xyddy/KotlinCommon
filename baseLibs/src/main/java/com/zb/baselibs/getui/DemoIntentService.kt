package com.zb.baselibs.getui

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.igexin.sdk.GTIntentService
import com.igexin.sdk.message.GTCmdMessage
import com.igexin.sdk.message.GTNotificationMessage
import com.igexin.sdk.message.GTTransmitMessage
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.createNotificationCompatBuilder
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.utils.isAppRunning
import com.zb.baselibs.utils.saveString
import org.json.JSONObject

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DemoIntentService : GTIntentService() {

    override fun onReceiveServicePid(p0: Context?, p1: Int) {
    }

    /**
     * 接受channelId
     */
    override fun onReceiveClientId(p0: Context?, p1: String?) {
        saveString("channelId", p1.toString())
        Log.e("channelId", p1.toString())
    }

    /**
     * 透传信息
     */
    override fun onReceiveMessageData(context: Context, msg: GTTransmitMessage) {
        val payload = msg.payload
        if (payload != null) {
            val data = String(payload)
            val nmc = NotificationManagerCompat.from(context)
            val jsonObject: JSONObject
            try {
                jsonObject = JSONObject(data)
                val customContent = jsonObject.optJSONObject("custom_content")
                val openType = jsonObject.optInt("open_type")
                val description = jsonObject.optString("description")
                val title = jsonObject.optString("title")
                val builder = createNotificationCompatBuilder(title, description)
                when {
                    openType == 1 -> {
                        val intents = arrayOfNulls<Intent>(2)
                        intents[0] = Intent.makeRestartActivityTask(ComponentName(context, Class.forName(BaseApp.noticeClassList[0])))
                        intents[1] = Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.optString("url")))
                        intents[1]!!.addCategory(Intent.CATEGORY_BROWSABLE)
                        intents[1]!!.component = null
                        intents[1]!!.selector = null
                        val contentIntent = PendingIntent.getActivities(context, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT)
                        // 指定内容意图
                        builder.setContentIntent(contentIntent)
                        nmc.notify(null, 1, builder.build())
                    }
                    customContent == null -> {
                        val intentMain = Intent(context, Class.forName(BaseApp.noticeClassList[0]))
                        val contextIntent = PendingIntent.getActivity(context, 0, intentMain, PendingIntent.FLAG_UPDATE_CURRENT)
                        builder.setContentIntent(contextIntent)
                        nmc.notify(null, 2, builder.build())
                    }
                    else -> {
                        val userId = jsonObject.optJSONObject("custom_content").optLong("userId")
                        val activityContent = jsonObject.optJSONObject("custom_content").optJSONObject("Activity")
                        if (userId != getLong("userId")) {
                            return
                        }
                        if (isAppRunning()) {
                            Log.i("isAppRunning", "isAppRunning == true")
                            val intent1 = Intent(context, Class.forName(BaseApp.noticeClassList[1]))
                            intent1.putExtra("activityContent", activityContent.toString())
                            val contentIntent = PendingIntent.getActivity(context, 3, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
                            // 指定内容意图
                            builder.setContentIntent(contentIntent)
                        } else {
                            Log.i("isAppRunning", "isAppRunning == false")
                            val intents = arrayOfNulls<Intent>(2)
                            intents[0] = Intent.makeRestartActivityTask(ComponentName(context, Class.forName(BaseApp.noticeClassList[0])))
                            intents[1] = Intent(context, Class.forName(BaseApp.noticeClassList[1]))
                            intents[1]!!.putExtra("activityContent", activityContent.toString())
                            val contentIntent = PendingIntent.getActivities(context, 3, intents, PendingIntent.FLAG_UPDATE_CURRENT)
                            // 指定内容意图
                            builder.setContentIntent(contentIntent)
                        }
                        nmc.notify(null, 3, builder.build())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 离线上线通知
     */
    override fun onReceiveOnlineState(p0: Context?, p1: Boolean) {
    }

    /**
     * 各种事件处理回执
     */
    override fun onReceiveCommandResult(p0: Context?, p1: GTCmdMessage?) {
    }

    /**
     * 通知到达，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageArrived(p0: Context?, data: GTNotificationMessage) {
    }

    /**
     * 通知点击，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageClicked(p0: Context?, data: GTNotificationMessage) {
    }
}