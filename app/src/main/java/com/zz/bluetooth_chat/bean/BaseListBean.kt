package com.zz.bluetooth_chat.bean

import com.zz.bluetooth_chat.util.DateUtil


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   :
 */

open class BaseListBean {
    var maxId: Int = 0
    var longTime: Long = DateUtil.getCurrentTimeMillis()

    fun upLongTime() {
        longTime = DateUtil.getCurrentTimeMillis()
    }
}