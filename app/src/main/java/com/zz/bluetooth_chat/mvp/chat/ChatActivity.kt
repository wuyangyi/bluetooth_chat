package com.zz.bluetooth_chat.mvp.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zz.bluetooth_chat.base.BaseActivity
import com.zz.bluetooth_chat.bean.UserBean

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : 聊天
 */

class ChatActivity : BaseActivity<ChatFragment>() {
    override fun getFragment(): ChatFragment {
        return ChatFragment.newInstance(intent.extras!!)
    }

    companion object {
        val CHAT_USER_INFO = "chat_user_info"

        fun startToChatActivity(context: Context, user: UserBean) {
            val intent = Intent(context, ChatActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CHAT_USER_INFO, user)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

}