package com.zz.bluetooth_chat.mvp.chat

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.base_adapter.ItemViewDelegate
import com.zz.bluetooth_chat.base.base_adapter.ViewHolder
import com.zz.bluetooth_chat.bean.ChatBean
import com.zz.bluetooth_chat.util.AntiShakeUtils
import com.zz.bluetooth_chat.util.AppConfig
import com.zz.bluetooth_chat.util.DateUtil
import com.zz.bluetooth_chat.util.Utils

/**
 * author: wuyangyi
 * date  : 2020-01-18
 * e-mail: wuyangyi@haitou.cc
 * desc  : 我发送的消息
 */

class ChatItem : ItemViewDelegate<ChatBean> {

    private var context: Context
    private var mListBean: ArrayList<ChatBean>
    private var mOnViewLongClick: OnViewLongClick? = null
    private var mImageClick: ImageClick? = null
    private var mSoundClick: SoundClick? = null

    constructor(context: Context, listBean: ArrayList<ChatBean>) {
        this.context = context
        this.mListBean = listBean
    }

    fun setImageClick(imageClick: ImageClick) {
        this.mImageClick = imageClick
    }

    fun setSoundClick(soundClick: SoundClick) {
        this.mSoundClick = soundClick
    }

    fun setOnViewLongClick(mOnViewLongClick: OnViewLongClick) {
        this.mOnViewLongClick = mOnViewLongClick
    }

    override fun getItemViewLayoutId(): Int {
        return R.layout.item_chat
    }

    override fun isForViewType(item: ChatBean, position: Int): Boolean {
        return item.isMeSend
    }

    override fun convert(
        holder: ViewHolder,
        chatBean: ChatBean,
        lastT: ChatBean,
        position: Int,
        itemCounts: Int
    ) {
        holder.setText(R.id.tv_time, DateUtil.getShowAllTimeForNow(chatBean.sendTime))
        Utils.setUserHead(holder.getImageViwe(R.id.iv_user_head), context)
        holder.setText(R.id.tv_name, AppConfig.user!!.name)
        if (position == mListBean.size - 1) {
            holder.getTextView(R.id.tv_time).visibility = View.VISIBLE
        } else {
            if (DateUtil.getTimeDifference(
                    mListBean[position + 1].sendTime,
                    mListBean[position].sendTime
                ) / 60 >= 1
            ) {
                holder.getTextView(R.id.tv_time).visibility = View.VISIBLE
            } else {
                holder.getTextView(R.id.tv_time).visibility = View.GONE
            }
        }

        holder.getView<View>(R.id.tv_content).setOnLongClickListener { v ->
            if (mOnViewLongClick != null) {
                val y = v.bottom - v.height
                mOnViewLongClick!!.onViewLongClickListener(
                    v.right - v.width / 2,
                    y - v.height / 2,
                    position,
                    chatBean.isMeSend
                )
            }
            true
        }
        holder.getView<View>(R.id.iv_image).setOnLongClickListener(View.OnLongClickListener { v ->
            if (mOnViewLongClick != null) {
                val y = v.bottom - v.height
                mOnViewLongClick!!.onViewLongClickListener(
                    v.right - v.width / 2,
                    y - v.height / 2,
                    position,
                    chatBean.isMeSend
                )
            }
            true
        })

        holder.getView<View>(R.id.llSound).setOnLongClickListener(View.OnLongClickListener { v ->
            if (mOnViewLongClick != null) {
                val y = v.bottom - v.height
                mOnViewLongClick!!.onViewLongClickListener(
                    v.right - v.width / 2,
                    y - v.height / 2,
                    position,
                    chatBean.isMeSend
                )
            }
            true
        })

        if (chatBean.type != ChatBean.SEND_MESSAGE_TEXT) {
            holder.getTextView(R.id.tv_content).visibility = View.GONE
        } else {
            holder.getTextView(R.id.tv_content).visibility = View.VISIBLE
            holder.setText(R.id.tv_content, chatBean.content)
        }
        if (chatBean.type == ChatBean.SEND_MESSAGE_IMAGE) {
            holder.setImageBitmap(R.id.iv_image, Utils.convertStringToIcon(chatBean.content)!!)
            holder.getImageViwe(R.id.iv_image).visibility = View.VISIBLE
        } else {
            holder.getImageViwe(R.id.iv_image).visibility = View.GONE
        }
        if (chatBean.type == ChatBean.SEND_MESSAGE_SOUND) {
            holder.getView<View>(R.id.llSound).visibility = View.VISIBLE
            holder.getTextView(R.id.tvSoundTime).text = (chatBean.soundTime.toInt()).toString() + "\""
        } else {
            holder.getView<View>(R.id.llSound).visibility = View.GONE
        }
        holder.getImageViwe(R.id.iv_image).setOnClickListener(View.OnClickListener { v ->
            if (AntiShakeUtils.isInvalidClick(v)) {
                return@OnClickListener
            }
            if (mImageClick != null) {
                mImageClick!!.onImageClickListener(chatBean.content)
            }
        })
        holder.getView<View>(R.id.llSound).setOnClickListener {
            mSoundClick!!.OnSoundClickListener(
                chatBean.content,
                holder.getImageViwe(R.id.ivSound),
                chatBean.isMeSend
            )
        }
    }


    interface OnViewLongClick {
        fun onViewLongClickListener(x: Int, y: Int, position: Int, isMe: Boolean)
    }

    //图片点击事件
    interface ImageClick {
        fun onImageClickListener(path: String)
    }

    //语音点击事件
    interface SoundClick {
        fun OnSoundClickListener(path: String, imageView: ImageView, isMe: Boolean)
    }
}