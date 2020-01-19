package com.zz.bluetooth_chat.widget.chat

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.util.DeviceUtils

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */

class ChatDialogManager(private val mContent: Context) {
    private var dialog: Dialog? = null
    private var tvContent: TextView? = null
    private var ivLogo: ImageView? = null
    private var ivSound: ImageView? = null

    init {
        initDialog()
    }

    private fun initDialog() {
        dialog = Dialog(mContent, R.style.MyDialog)
        val view = LayoutInflater.from(mContent).inflate(R.layout.dialog_chat, null)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutParams.width = DeviceUtils.dp2px(mContent, 200f)
        layoutParams.height = layoutParams.width
        tvContent = view.findViewById(R.id.tvContent)
        ivLogo = view.findViewById(R.id.ivLogo)
        ivSound = view.findViewById(R.id.ivSound)
        dialog!!.addContentView(view, layoutParams)
    }

    fun show() {
        if (dialog == null) {
            initDialog()
        }
        dialog!!.show()
    }


    fun dismiss() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
        chatDialogManager = null
    }

    /**
     * 设置状态
     */
    fun setTextContent(action: String) {
        when (action) {
            ACTION_LOADING -> {
                tvContent!!.text = "手指上滑，取消发送"
                tvContent!!.textSize = 12f
                ivSound!!.visibility = View.VISIBLE
                ivLogo!!.setImageDrawable(mContent.resources.getDrawable(R.mipmap.tt_sound_speck))
            }
            ACTION_WANG_CANCEL -> {
                tvContent!!.text = "松开手指，取消发送"
                tvContent!!.textSize = 12f
                ivLogo!!.setImageDrawable(mContent.resources.getDrawable(R.mipmap.tt_sound_cancel))
            }
            ACTION_SHORT -> {
                tvContent!!.text = "说话时间太短"
                tvContent!!.textSize = 15f
                ivLogo!!.setImageDrawable(mContent.resources.getDrawable(R.mipmap.tt_sound_hint))
                ivSound!!.visibility = View.GONE
            }
        }
    }

    fun updateSoundLevel(level: Int) {
        val id: Int
        Log.d("音量等级", level.toString() + "")
        when (level) {
            1 -> id = R.mipmap.tt_sound_volume_01
            2 -> id = R.mipmap.tt_sound_volume_02
            3 -> id = R.mipmap.tt_sound_volume_03
            4 -> id = R.mipmap.tt_sound_volume_04
            5 -> id = R.mipmap.tt_sound_volume_05
            6 -> id = R.mipmap.tt_sound_volume_06
            7 -> id = R.mipmap.tt_sound_volume_07
            else -> id = R.mipmap.tt_sound_volume_07
        }
        ivSound!!.setImageDrawable(mContent.resources.getDrawable(id))
    }

    companion object {
        val ACTION_LOADING = "action_loading"//正在录制
        val ACTION_WANG_CANCEL = "action_want_cancel"//想要取消
        val ACTION_SHORT = "action_short" //说话太短

        private var chatDialogManager: ChatDialogManager? = null

        fun getInstance(context: Context): ChatDialogManager {
            if (chatDialogManager == null) {
                synchronized(ChatDialogManager::class.java) {
                    chatDialogManager = ChatDialogManager(context)
                }
            }
            return chatDialogManager!!
        }
    }
}
