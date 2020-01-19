package com.zz.bluetooth_chat.util

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * author: wuyangyi
 * date  : 2020-01-15
 * e-mail: wuyangyi@haitou.cc
 * desc  : 时间工具类
 */

object DateUtil {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * 获得当前系统时间
     * 格式：2020-01-15 18:01:50
     */
    fun getNowTime(): String {

        var date = Date()
        return simpleDateFormat.format(date)
    }

    /**
     * 获得当前时间戳
     */
    fun getCurrentTimeMillis() : Long {
        return System.currentTimeMillis()
    }

    /**
     * 获得两个时间的差
     * 单位：s
     */
    fun getTimeDifference(firstTime: String, lastTime: String) : Long {
        try {
            val lastDate = simpleDateFormat.parse(lastTime)
            val firstDate = simpleDateFormat.parse(firstTime)
            var time: Long = firstDate.time - lastDate.time
            time = abs(time)
            Log.d("时间差", "${time / 1000}")
            return time / 1000
        }catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获得时间与当前时间的差
     */
    fun getNowTimeDifference(time: String): Long {
        try {
            val lastDate = Date()
            val firstDate = simpleDateFormat.parse(time)
            var time: Long = lastDate.time - firstDate.time
            time = abs(time)
            return time / 1000
        }catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取与当前时间相比需要显示的时间
     * 1.与当前时间的差不超过1小时，显示**分钟前
     * 2.若超过一小时，时间是同一天，显示14:52
     * 3.若为昨天，显示：昨天 14:52
     * 4.若为今年，显示07-04 14:52
     * 5.若为很久的时间显示2020-01-15 14:52
     */
    fun getShowAllTimeForNow(time: String): String {
        val lastDate = Date()
        val firstDate = simpleDateFormat.parse(time)
        var defference = (lastDate.time - firstDate.time) / 60 / 1000
        val cFirst = Calendar.getInstance()
        val cLast = Calendar.getInstance()
        cFirst.time = firstDate
        cLast.time = lastDate
        val y = getTimeByLong(lastDate.time - 24 * 60 * 60 * 1000)
        val yD = simpleDateFormat.parse(y)
        val cY = Calendar.getInstance()
        cY.time = yD
        return if(defference < 1) {
            "刚刚"
        } else if(defference < 60) { //1小时内
            "${defference}分钟以前"
        } else if(cFirst.get(Calendar.YEAR) == cLast.get(Calendar.YEAR) && cFirst.get(Calendar.MONTH) == cLast.get(Calendar.MONTH) && cFirst.get(Calendar.DAY_OF_MONTH) == cLast.get(Calendar.DAY_OF_MONTH)) { //今天
            String.format("%02d:%02d", cFirst.get(Calendar.HOUR_OF_DAY), cFirst.get(Calendar.MINUTE))
        } else if (cFirst.get(Calendar.YEAR) == cY.get(Calendar.YEAR) && cFirst.get(Calendar.MONTH) == cY.get(Calendar.MONTH) && cFirst.get(Calendar.DAY_OF_MONTH) == cY.get(Calendar.DAY_OF_MONTH)) {
            String.format("昨天 %02d:%02d", cFirst.get(Calendar.HOUR_OF_DAY), cFirst.get(Calendar.MINUTE))
        } else if (cFirst.get(Calendar.YEAR) == cLast.get(Calendar.YEAR)) {
            time.substring(time.indexOf("-")+1, time.lastIndexOf(":"))
        } else {
            time.substring(0, time.lastIndexOf(":"))
        }
    }

    /**
     * 获取与当前时间相比需要显示的时间(只显示2020-01-11到天)
     * 1.若为今天显示：今天
     * 2.若为昨天，显示：昨天
     * 3.若为今年，显示07-04
     * 4.若为很久的时间显示2020-01-15
     */
    fun getShowTimeForNowDay(time: String): String {
        val lastDate = Date()
        val firstDate = simpleDateFormat.parse(time)
        var defference = (lastDate.time - firstDate.time) / 60 / 1000
        val cFirst = Calendar.getInstance()
        val cLast = Calendar.getInstance()
        cFirst.time = firstDate
        cLast.time = lastDate
        val y = getTimeByLong(lastDate.time - 24 * 60 * 60 * 1000)
        val yD = simpleDateFormat.parse(y)
        val cY = Calendar.getInstance()
        cY.time = yD
        return if(cFirst.get(Calendar.YEAR) == cLast.get(Calendar.YEAR) && cFirst.get(Calendar.MONTH) == cLast.get(Calendar.MONTH) && cFirst.get(Calendar.DAY_OF_MONTH) == cLast.get(Calendar.DAY_OF_MONTH)) { //今天
            "今天"
        } else if (cFirst.get(Calendar.YEAR) == cY.get(Calendar.YEAR) && cFirst.get(Calendar.MONTH) == cY.get(Calendar.MONTH) && cFirst.get(Calendar.DAY_OF_MONTH) == cY.get(Calendar.DAY_OF_MONTH)) {
            "昨天"
        } else if (cFirst.get(Calendar.YEAR) == cLast.get(Calendar.YEAR)) {
            time.substring(time.indexOf("-")+1, time.lastIndexOf(" "))
        } else {
            time.substring(0, time.lastIndexOf(" "))
        }
    }

    /**
     * 时间戳转日期
     */
    fun getTimeByLong(time: Long): String {
        return simpleDateFormat.format(time)
    }
}