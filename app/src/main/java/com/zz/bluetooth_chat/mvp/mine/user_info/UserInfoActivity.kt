package com.zz.bluetooth_chat.mvp.mine.user_info

import com.zz.bluetooth_chat.base.BaseActivity

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 账号信息
 */

class UserInfoActivity : BaseActivity<UserInfoFragment>() {
    override fun getFragment(): UserInfoFragment {
        return UserInfoFragment()
    }

}