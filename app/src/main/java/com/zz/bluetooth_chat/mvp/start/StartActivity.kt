package com.zz.bluetooth_chat.mvp.start

import com.zz.bluetooth_chat.base.BaseActivity


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   : 启动页
 */

class StartActivity : BaseActivity<StartFragment>() {
    override fun getFragment(): StartFragment {
        return StartFragment()
    }

}
