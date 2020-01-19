package com.zz.bluetooth_chat.mvp.group

import com.zz.bluetooth_chat.base.BaseListView
import com.zz.bluetooth_chat.base.BaseView
import com.zz.bluetooth_chat.base.IBaseListPresenter
import com.zz.bluetooth_chat.base.IBasePresenter
import com.zz.bluetooth_chat.bean.UserBean

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */
interface GroupContract {
    interface View : BaseListView<UserBean> {
        fun startDiscovery()
    }

    interface Presenter : IBaseListPresenter {
    }
}