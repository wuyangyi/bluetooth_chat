package com.zz.bluetooth_chat.util

import android.view.View
import androidx.annotation.NonNull
import com.zz.bluetooth_chat.R

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   : 防抖动点击
 */

object AntiShakeUtils {

    private val INTERNAL_TIME: Long = 1000 //时间1s

    /**
     * Whether this click event is invalid.
     *
     * @param target       被点击的控件
     * @param internalTime 防抖时间
     * @return true, 在防抖时间内点击了
     */
    @JvmOverloads
    fun isInvalidClick(@NonNull target: View, internalTime: Long = INTERNAL_TIME): Boolean {
        val curTimeStamp = System.currentTimeMillis() //获得系统时间，即当前点击时的时间
        var lastClickTimeStamp: Long = 0 //存放上次点击的时间
        //给view设置名为last_click_time的tag标签并存储上一次点击的时间，在一定时间内只取一次点击事件
        val o = target.getTag(R.id.last_click_time)
        if (o == null) {
            target.setTag(R.id.last_click_time, curTimeStamp)
            return false
        }
        lastClickTimeStamp = o as Long
        //这次点击时间 - 上次点击时间 < 防抖时间 则表示该点击事件在防抖时间内点击的
        val isInvalid = curTimeStamp - lastClickTimeStamp < internalTime
        if (!isInvalid)
            target.setTag(R.id.last_click_time, curTimeStamp)
        return isInvalid
    }
}
