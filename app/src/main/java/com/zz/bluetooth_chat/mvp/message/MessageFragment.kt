package com.zz.bluetooth_chat.mvp.message

import android.view.View
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  : 消息
 */

class MessageFragment : BaseFragment<MessagePresenter>(), MessageContract.View {
    override fun initView(rootView: View) {

    }

    override fun initData() {
    }

    override fun getBodyLayoutId(): Int {
        return R.layout.fragment_message
    }

    override fun setLeftImage(): Int {
        return 0
    }

    override fun setCenterTitle(): String {
        return "趣聊"
    }

    override fun setUseStatusBar(): Boolean {
        return true
    }

}