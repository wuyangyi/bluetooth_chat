package com.zz.bluetooth_chat.bean

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : 聊天的bean
 */
data class ChatBean(
    var id : Long,  //id  数据库主键
    var user: UserBean?, //聊天的对方用户
    var isMeSend: Boolean, //是否是自己发送
    var content: String, //发送的内容(文本、图片、语音地址)
    var sendTime: String, //发送的时间
    var create_time: Long, //时间戳
    var soundTime: Float, //语音时长
    var type: Int //类别（文本、图片、语音）
) : BaseListBean() {
    companion object{
        val SEND_MESSAGE_TEXT = 0 //文本
        val SEND_MESSAGE_IMAGE = 1 //图片
        val SEND_MESSAGE_SOUND = 2 //语音
    }
}