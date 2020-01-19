package com.zz.bluetooth_chat.mvp.start

import com.zz.bluetooth_chat.base.BaseView
import com.zz.bluetooth_chat.base.IBasePresenter


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   :
 */

interface StartContract{
    interface View : BaseView {
        fun setJumpText(text: String)

        fun startToHome()
    }

    interface Presenter : IBasePresenter {
        fun startTime()

        fun stopTime()
    }
}