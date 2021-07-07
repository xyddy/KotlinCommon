package com.zb.baselibs.app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.umeng.commonsdk.UMConfigure
import com.zb.baselibs.R
import com.zb.baselibs.utils.screenHeight
import com.zb.baselibs.utils.screenWidth
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseApp : MultiDexApplication() {

    companion object {
        @JvmField
        var H: Int = 0

        @JvmField
        var W: Int = 0

        var phoneRegex = Regex("^[0-9]{11}$")
        lateinit var context: Context
        lateinit var fixedThreadPool: ExecutorService
        private var mActivityList = LinkedList<AppCompatActivity>()
        var baseUrl: String = ""
        var appType: String = ""
        var projectName: String = ""
        lateinit var ymData: Array<String>
        var notificationChannelName: String = ""
        lateinit var noticeClassList: Array<String>
        var noticeLogo: Int = 0

        fun addActivity(activity: AppCompatActivity) {
            mActivityList.addFirst(activity)
        }

        /**
         * 退出应用时调用
         */
        fun exit() {
            for (activity in mActivityList) {
                activity.finish()
            }
            mActivityList.clear()
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        W = context.screenWidth()
        H = context.screenHeight()
        fixedThreadPool = Executors.newFixedThreadPool(3)

        MultiDex.install(this)

        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, R.color.black_252)
            return@setDefaultRefreshHeaderCreator ClassicsHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(
                context
            ).setDrawableSize(20F)
        }
        baseUrl = getBaseUrl()
        appType = getAppType()
        projectName = getProjectName()
        ymData = getYMData()
        notificationChannelName = getNotificationChannelName()
        noticeClassList = getNoticeClassList()
        noticeLogo = getNoticeLogo()
        // 友盟预初始化
        UMConfigure.preInit(context, ymData[0], null)
    }

    abstract fun getBaseUrl(): String
    abstract fun getAppType(): String
    abstract fun getProjectName(): String
    abstract fun getYMData(): Array<String>
    abstract fun getNotificationChannelName(): String
    abstract fun getNoticeClassList(): Array<String>
    abstract fun getNoticeLogo(): Int
}