package com.zz.bluetooth_chat.base

import android.app.Application
import android.content.Context
import com.hjq.toast.ToastUtils
import com.zz.bluetooth_chat.util.ToastSystemStyle

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   :
 */

class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        mApplication = this
        ToastUtils.init(this, ToastSystemStyle())
    }

    companion object {
        private var mApplication: BaseApplication? = null
        fun getContext(): Context? {
            return mApplication
        }
    }
}