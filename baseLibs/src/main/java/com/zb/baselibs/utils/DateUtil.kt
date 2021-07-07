@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.zb.baselibs.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object DateUtil {
    const val yyyy_MM_dd = "yyyy-MM-dd"
    const val yyyy_MM_dd_nyr = "yyyy年MM月dd日"
    const val CN_MM_dd = "MM月dd日"
    const val MM_dd_HH_mm = "MM/dd HH:mm"
    const val yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    const val yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm"
    const val HH_mm = "HH:mm"
    private val constellations = arrayOf(
        arrayOf("摩羯座", "水瓶座"),
        arrayOf("水瓶座", "双鱼座"),
        arrayOf("双鱼座", "白羊座"),
        arrayOf("白羊座", "金牛座"),
        arrayOf("金牛座", "双子座"),
        arrayOf("双子座", "巨蟹座"),
        arrayOf("巨蟹座", "狮子座"),
        arrayOf("狮子座", "处女座"),
        arrayOf("处女座", "天秤座"),
        arrayOf("天秤座", "天蝎座"),
        arrayOf("天蝎座", "射手座"),
        arrayOf("射手座", "摩羯座")
    )

    //星座分割时间
    private val date = intArrayOf(20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22)

    /**
     * 当前日期
     *
     * @param pattern
     * @return
     */

    fun getNow(pattern: String): String {
        val df = SimpleDateFormat(pattern)
        return df.format(Calendar.getInstance().time)
    }

    /**
     * 得到几天后或几天前的时间
     *
     * @param day 几天后 >0;
     * 几天前 <0;
     * @return
     */
    fun getOtherDate(dateStr: String, day: Int): String {
        val now = Calendar.getInstance()
        now.time = strToDate(dateStr, yyyy_MM_dd)
        now[Calendar.DATE] = now[Calendar.DATE] + day
        return dateToStr(now.time, yyyy_MM_dd)
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    fun getDaysByYearMonth(year: Int, month: Int): Int {
        val a = Calendar.getInstance()
        a[Calendar.YEAR] = year
        a[Calendar.MONTH] = month - 1
        a[Calendar.DATE] = 1
        a.roll(Calendar.DATE, -1)
        return a[Calendar.DATE]
    }

    /**
     * 判断一年的第几周
     *
     * @param datetime
     * @return
     * @throws java.text.ParseException
     */
    fun whatWeek(datetime: String): Int {
        val format = SimpleDateFormat(yyyy_MM_dd)
        val date = format.parse(datetime)
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.time = date!!
        return calendar[Calendar.WEEK_OF_YEAR]
    }

    /**
     * 比较2个日期
     *
     * @param DATE1
     * @param DATE2
     * @param type  相隔天数 1000f * 3600f * 24f;
     * 相隔小时 1000f * 3600f;
     * 相隔分钟 1000f * 60f;
     * @return
     */
    fun getDateCount(DATE1: String, DATE2: String, dateType: String, type: Float): Int {
        return (compareTime(DATE1, DATE2, dateType) / type).toInt()
    }

    /**
     * 聊天记录时间
     *
     * @param dateStr
     * @return
     */
    fun getChatTime(dateStr: String): String {
        val chatTime: String
        val date = strToDate(dateStr, yyyy_MM_dd_HH_mm_ss)
        val dayCount = getDateCount(
            getNow(yyyy_MM_dd),
            dateStr.substring(0, 10),
            yyyy_MM_dd,
            1000f * 3600f * 24f
        ).toDouble()
        chatTime = if (dayCount == 0.0) { // 今天
            when {
                date!!.hours < 7 -> {
                    "凌晨 " + dateStr.substring(11, 16)
                }
                date.hours < 13 -> {
                    "上午 " + dateStr.substring(11, 16)
                }
                date.hours < 18 -> {
                    "下午 " + dateStr.substring(11, 16)
                }
                else -> {
                    "晚上 " + dateStr.substring(11, 16)
                }
            }
        } else {
            dateStr.substring(5, 10) + " " + getWeek(dateStr) + " " + dateStr.substring(11, 16)
        }
        return chatTime
    }

    /**
     * 说说时间
     *
     * @param strDate
     * @return
     */
    fun getTimeToToday(strDate: String): String {
        val dfs = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss)
        val between: Long
        between = try {
            val end = Date()
            val begin = dfs.parse(strDate)
            end.time - begin.time // 得到两者的毫秒数
        } catch (e: ParseException) {
            return ""
        }
        val day = between / (24 * 60 * 60 * 1000)
        val hour = between / (60 * 60 * 1000) - day * 24
        val min = between / (60 * 1000) - day * 24 * 60 - hour * 60
        if (day > 0) {
            return if (day < 7) day.toString() + "天前" else if (day < 14) "1星期前" else if (day < 21) "2星期前" else if (day < 28) "3星期前" else "1个月前"
        }
        if (hour > 0) {
            return hour.toString() + "小时前"
        }
        return if (min > 0) {
            min.toString() + "分钟前"
        } else {
            "刚刚"
        }
    }

    /**
     * 星座生成 传进是日期格式为: yyyy-mm-dd
     *
     * @param time
     */
    fun getConstellations(time: String?): String {
        if (time == null || time.isEmpty()) {
            return ""
        }
        val data = time.split("-".toRegex()).toTypedArray()
        val day = date[data[1].toInt() - 1]
        val cl1 = constellations[data[1].toInt() - 1]
        return if (data[2].toInt() >= day) {
            cl1[1]
        } else {
            cl1[0]
        }
    }

    /**
     * 岁数
     */
    fun getAge(birthday: String, age: Int): Int {
        if (birthday.isEmpty()) return age
        if (TextUtils.equals(birthday, "1990-01-01")) return age
        val now = getNow(yyyy_MM_dd)
        val nowYear = now.substring(0, 4).toInt()
        val birthYear = birthday.substring(0, 4).toInt()
        return nowYear - birthYear + 1
    }

    /**
     * 根据日期取得星期几
     *
     * @param dateStr
     * @return
     */
    private fun getWeek(dateStr: String): String {
        val date = strToDate(dateStr, yyyy_MM_dd)
        val weeks = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        val cal = Calendar.getInstance()
        cal.time = date
        var weekIndex = cal[Calendar.DAY_OF_WEEK] - 1
        if (weekIndex < 0) {
            weekIndex = 0
        }
        return weeks[weekIndex]
    }

    /**
     * 将字符串型(中文格式)转成日期型
     *
     * @param str     "2002-07-01 22:09:55"
     * @param pattern yyyy_MM_dd_HH_mm_ss
     * @return
     */
    private fun strToDate(str: String, pattern: String): Date? {
        val date: Date?
        return try {
            val fmt = SimpleDateFormat(pattern, Locale.CHINA)
            date = fmt.parse(str)
            date
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 将日期时间型转成字符串
     *
     * @param date
     * @param pattern yyyy_MM_dd_HH_mm_ss
     * @return
     */
    private fun dateToStr(date: Date, pattern: String): String {
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(date)
    }

    /**
     * 比较2个日期毫秒大小
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    private fun compareTime(DATE1: String, DATE2: String, dateType: String): Long {
        val dt1 = strToDate(DATE1, dateType)
        val dt2 = strToDate(DATE2, dateType)
        return dt1!!.time - dt2!!.time
    }
}