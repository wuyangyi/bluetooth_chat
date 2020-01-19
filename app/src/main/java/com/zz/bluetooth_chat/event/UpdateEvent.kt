package com.zz.bluetooth_chat.event

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : eventBus传递消息用
 */

class UpdateEvent {
    companion object{
        val USER_INFO_UPDATE = 0 //用户信息更改
    }

    var send: Any
    var type: Int

    constructor(sender: Any, type: Int) {
        this.send = sender
        this.type = type
    }
}