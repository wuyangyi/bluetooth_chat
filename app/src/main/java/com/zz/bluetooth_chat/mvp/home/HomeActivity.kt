package com.zz.bluetooth_chat.mvp.home

import com.zz.bluetooth_chat.base.BaseActivity

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */
class HomeActivity : BaseActivity<HomeFragment>() {
    override fun getFragment(): HomeFragment {
        return HomeFragment()
    }

}