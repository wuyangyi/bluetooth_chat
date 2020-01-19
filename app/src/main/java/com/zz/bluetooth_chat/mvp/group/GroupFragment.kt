package com.zz.bluetooth_chat.mvp.group

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.view.View
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment
import com.zz.bluetooth_chat.util.ToastUtils
import com.zz.bluetooth_chat.util.BlueUtil
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.zz.bluetooth_chat.base.BaseListFragment
import com.zz.bluetooth_chat.base.base_adapter.CommonAdapter
import com.zz.bluetooth_chat.base.base_adapter.MultiItemTypeAdapter
import com.zz.bluetooth_chat.base.base_adapter.ViewHolder
import com.zz.bluetooth_chat.bean.UserBean
import com.zz.bluetooth_chat.mvp.chat.ChatActivity
import com.zz.bluetooth_chat.util.AppConfig


/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  : 附近
 */

class GroupFragment: BaseListFragment<GroupPresenter, UserBean>(), MultiItemTypeAdapter.OnItemClickListener, GroupContract.View {

    protected lateinit var animation: AnimationDrawable
    private var deviceList = ArrayList<BluetoothDevice>()

    private var mReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.d("设备信息",device.name + "\n" + device.address)  //保存设备地址与名字
                if (device.name != null && device.name.isNotEmpty()) {
                    if (!checkIsHave(device.address ?: "")) {
                        mListData.add(UserBean(
                            deviceId = device.address,
                            deviceName = device.name
                        ))
                        deviceList.add(device)
                        adapterNotifyDataSetChanged()
                    }

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {  //搜索结束
//                mBluetoothAdapter!!.cancelDiscovery()
//                hideRefreshState(false)
            }

        }
    }

    fun checkIsHave(deviceId: String) : Boolean {
        for (item : UserBean in mListData) {
            if (item.deviceId == deviceId) {
                return true
            }
        }
        return false
    }

    override fun isNeedLoadMore(): Boolean {
        return false
    }

    override fun isNeedRefresh(): Boolean {
        return false
    }

    override fun setRightImage(): Int {
        return R.drawable.frame_loading_white
    }

    override fun initView(rootView: View) {
        super.initView(rootView)
        animation = mIbRightImage.drawable as AnimationDrawable
        if (!animation.isRunning) {
            animation.start()
        }
    }

    override fun initData() {
        mPresenter = GroupPresenter(this)
        if (checkBlueIsOpen()) {
            BlueUtil.setBlueAlwaysCanLook()
//            startRefresh()
            mPresenter.requestNetData(mMaxId, mPage, false)
        }
    }

    override fun startDiscovery() {
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity!!.registerReceiver(mReceiver, filter)
//        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//        activity!!.registerReceiver(mReceiver, filter)
        mBluetoothAdapter!!.startDiscovery()
    }

    override fun setLeftImage(): Int {
        return 0
    }

    override fun setCenterTitle(): String {
        return "附近"
    }

    override fun setUseStatusBar(): Boolean {
        return true
    }

    override fun getAdapter(): RecyclerView.Adapter<ViewHolder> {
        var adapter = GroupDeviceAdapter(context!!, R.layout.item_group_user, mListData)
        adapter.setOnItemClickListener(this@GroupFragment)
        return adapter
    }

    override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            deviceList[position].createBond()
//        }
        ChatActivity.startToChatActivity(activity!!, mListData[position])
    }

    override fun onItemLongClick(
        view: View,
        holder: RecyclerView.ViewHolder,
        position: Int
    ): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothAdapter!!.cancelDiscovery()
        activity!!.unregisterReceiver(mReceiver)
        mReceiver = null

    }

}