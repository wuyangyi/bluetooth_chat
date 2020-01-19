package com.zz.bluetooth_chat.mvp.group

import android.content.Context
import android.util.Log
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.base_adapter.CommonAdapter
import com.zz.bluetooth_chat.base.base_adapter.ViewHolder
import com.zz.bluetooth_chat.bean.UserBean
import kotlinx.android.synthetic.main.item_group_user.view.*

/**
 * author: wuyangyi
 * date  : 2020-01-15
 * e-mail: wuyangyi@haitou.cc
 * desc  : 附近的设备adapter
 */

class GroupDeviceAdapter(context: Context, layoutId : Int, datas: ArrayList<UserBean>) : CommonAdapter<UserBean>(context, layoutId, datas) {
    override fun convert(holder: ViewHolder, t: UserBean, position: Int) {
        holder.getTextView(R.id.tvName).text = t.deviceName
        Log.d("item：", "${t.deviceName}    :   ${t.deviceId}")
    }
}