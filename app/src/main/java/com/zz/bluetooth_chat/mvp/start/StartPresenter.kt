package com.zz.bluetooth_chat.mvp.start

import android.os.CountDownTimer
import com.zz.bluetooth_chat.base.BaseView
import com.zz.bluetooth_chat.util.AppConfig.Companion.S_TO_MS_SPACING


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   :
 */

class StartPresenter(rootView: StartContract.View) : StartContract.Presenter {
    private val mTimeOut = 5 * S_TO_MS_SPACING

    internal var timer: CountDownTimer =
        object : CountDownTimer(mTimeOut.toLong(), S_TO_MS_SPACING.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                rootView.setJumpText("${millisUntilFinished / S_TO_MS_SPACING}s  |  跳过")//显示倒数的秒速
            }

            override fun onFinish() {
                rootView.startToHome()
            }
        }


    override fun startTime() {
        timer.start()
    }

    override fun stopTime() {
        timer.cancel()
    }

}