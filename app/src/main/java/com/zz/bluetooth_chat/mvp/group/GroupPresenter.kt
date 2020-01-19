package com.zz.bluetooth_chat.mvp.group

import android.util.Log

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */

class GroupPresenter(rootView: GroupContract.View) : GroupContract.Presenter {
    private var mRootView: GroupContract.View = rootView

    override fun requestNetData(maxId: Int, page: Int, isLoadMore: Boolean) {
        mRootView.startDiscovery()
    }

}