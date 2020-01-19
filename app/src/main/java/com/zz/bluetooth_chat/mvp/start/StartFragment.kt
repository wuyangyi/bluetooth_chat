package com.zz.bluetooth_chat.mvp.start

import android.util.Log
import android.view.View
import android.widget.TextView
import com.zz.bluetooth_chat.R
import com.zz.bluetooth_chat.base.BaseFragment
import com.zz.bluetooth_chat.bean.UserBean
import com.zz.bluetooth_chat.util.AppConfig
import com.zz.bluetooth_chat.util.BlueUtil
import com.zz.bluetooth_chat.util.DeviceUtils
import com.zz.bluetooth_chat.util.DeviceUtils.bottomMenuHeight
import com.zz.bluetooth_chat.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_start.*


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/14
 * desc   : 启动页
 */

class StartFragment : BaseFragment<StartContract.Presenter>(), StartContract.View {

    private var needLogin = false

    override fun initView(rootView: View) {
        bottomMenuHeight = DeviceUtils.getScreenHeight(activity!!) / 5 * 2
        initListener()
    }

    private fun initListener() {
        tvJump.setOnClickListener {
            startToHome()
        }
    }

    override fun initData() {
        AppConfig.initUserInfo(activity!!)
        mPresenter = StartPresenter(this)

        if (!checkBlueIsOpen()) {
            openBlue()
        } else {
            mPresenter.startTime()
        }
    }

    override fun openBlueSuccess() {
        super.openBlueSuccess()
        mPresenter.startTime()

    }

    override fun openBlueFail() {
        super.openBlueFail()
        mPresenter.startTime()
    }

    override fun getBodyLayoutId(): Int {
        return R.layout.fragment_start
    }

    override fun showToolbar(): Boolean {
        return false
    }

    override fun setUseStatusBar(): Boolean {
        return true
    }

    override fun setUseStatusView(): Boolean {
        return false
    }


    override fun setStatusBarGrey(): Boolean {
        return true
    }

    override fun setJumpText(text: String) {
        tvJump.text = text
    }

    override fun startToHome() {
        mPresenter.stopTime()
        goHome(true)
    }

}