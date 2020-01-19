package com.zz.bluetooth_chat.mvp.mine

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment
import com.zz.bluetooth_chat.event.UpdateEvent
import com.zz.bluetooth_chat.mvp.mine.user_info.UserInfoActivity
import com.zz.bluetooth_chat.util.AntiShakeUtils
import com.zz.bluetooth_chat.util.AppConfig
import com.zz.bluetooth_chat.util.DeviceUtils
import com.zz.bluetooth_chat.util.Utils
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  : 我的
 */

class MineFragment : BaseFragment<MinePresenter>(), MineContract.View {
    override fun initView(rootView: View) {
        initUser()
        initListener()
    }

    private fun initListener(){
        ib_my_manager.setOnClickListener {
            if (AntiShakeUtils.isInvalidClick(it)) {
                return@setOnClickListener
            }
            startActivity(Intent(activity!!, UserInfoActivity::class.java))
        }
    }

    fun getSex() :Int {
        when {
            AppConfig.user!!.sex == 0 -> return R.mipmap.ic_privary
            AppConfig.user!!.sex == 1 -> return R.mipmap.ic_man
            AppConfig.user!!.sex == 2 -> return R.mipmap.ic_woman
        }
        return R.mipmap.pic_default_secret
    }

    override fun initData() {
    }

    override fun getBodyLayoutId(): Int {
        return R.layout.fragment_mine
    }

    override fun setLeftImage(): Int {
        return 0
    }

    override fun registerEventBus(): Boolean {
        return true
    }

    override fun setCenterTitle(): String {
        return "我的"
    }

    override fun setUseStatusBar(): Boolean {
        return true
    }

    fun onEvent(event: UpdateEvent) {
        Log.d("保存信息回调", "11111")
        if(event.type == UpdateEvent.USER_INFO_UPDATE && event.send == true) {
            initUser()
            Log.d("保存信息回调", "2222")
        }
    }

    private fun initUser() {
        if(AppConfig.user == null) {
            return
        }
        Utils.setUserHead(iv_head, context!!)
        tv_name.text = AppConfig.user?.name ?: ""
        var draw : Drawable? = null
        draw = resources.getDrawable(getSex())
        draw?.setBounds(0, 0, DeviceUtils.dp2px(context!!, 17f), DeviceUtils.dp2px(context!!, 17f))
        tv_name.setCompoundDrawables(null, null, draw, null)
        tv_device_name.text = AppConfig.user!!.deviceName
    }

}