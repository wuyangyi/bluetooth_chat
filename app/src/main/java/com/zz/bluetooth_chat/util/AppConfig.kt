package com.zz.bluetooth_chat.util

import android.app.Activity
import com.zz.bluetooth_chat.bean.UserBean

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */
class AppConfig {
    companion object{
        val S_TO_MS_SPACING = 1000 // s 和 ms 的比例

        var user: UserBean? = null //用户信息

        fun initUserInfo(activity: Activity) {
            var user = UserBean()
            user.initData(activity)
            AppConfig.user = user
        }
    }
}