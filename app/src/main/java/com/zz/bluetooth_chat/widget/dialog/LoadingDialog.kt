package com.zz.bluetooth_chat.widget.dialog

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zz.bluetooth_chat.R


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   :
 */
class LoadingDialog(private val mActivity: Activity?) {
    private val HANDLE_DELAY = 0
    private val DEFAULT_ALPHA = 0.8f
    private var sLoadingDialog: AlertDialog? = null
    private var mAnimationDrawable: AnimationDrawable? = null
    private var layoutView: View? = null
    private var iv_hint_img: ImageView? = null
    private var tv_hint_text: TextView? = null

    private var callback :LoadingCallback? = null

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == HANDLE_DELAY && sLoadingDialog != null) {
                hideDialog()
            }
        }
    }

    /**
     * 设置加载成功或失败的回调
     */
    fun setCallBackClick(callback: LoadingCallback) {
        this.callback = callback
    }

    /**
     * 显示错误的状态
     *
     * @param text 错误或失败状态的提示消息
     */
    fun showStateError(text: String) {
        initDialog(R.mipmap.msg_box_remind, text, false)
        sendHideMessage()
    }

    /**
     * 显示成功的状态
     *
     * @param text 正确或成功状态的提示消息
     */
    fun showStateSuccess(text: String) {
        initDialog(R.mipmap.msg_box_succeed, text, false)
        sendHideMessage()
    }

    /**
     * 显示进行中的状态
     *
     * @param text 进行中的提示消息
     */
    fun showStateIng(text: String) {
        initDialog(R.drawable.frame_loading_grey, text, false)
        handleAnimation(true)
    }

    /**
     * 进行中的状态变为结束
     */
    fun showStateEnd() {
        handleAnimation(false)
        hideDialog()
    }

    fun onDestroy() {
        if (sLoadingDialog != null) {
            sLoadingDialog!!.dismiss()
        }
    }

    /**
     * 处理动画
     *
     * @param status true 开启动画，false 关闭动画
     */
    private fun handleAnimation(status: Boolean) {
        mAnimationDrawable = iv_hint_img!!.drawable as AnimationDrawable
        requireNotNull(mAnimationDrawable) { "load animation not be null" }
        if (status) {
            if (!mAnimationDrawable!!.isRunning) {
                mAnimationDrawable!!.start()
            }
        } else {
            if (mAnimationDrawable!!.isRunning) {
                mAnimationDrawable!!.stop()
            }
        }
    }


    private fun initDialog(imgRsId: Int?, hintContent: String, outsideCancel: Boolean) {
        if (sLoadingDialog == null) {
            layoutView = LayoutInflater.from(mActivity).inflate(R.layout.view_hint_info1, null)
            iv_hint_img = layoutView!!.findViewById(R.id.iv_hint_img) as ImageView
            tv_hint_text = layoutView!!.findViewById(R.id.tv_hint_text) as TextView
            sLoadingDialog = AlertDialog.Builder(mActivity, R.style.loadingDialogStyle)
                .setCancelable(outsideCancel)
                .create()
            sLoadingDialog!!.setCanceledOnTouchOutside(outsideCancel)
            sLoadingDialog!!.setOnDismissListener { setWindowAlpha(1.0f) }
        }
        tv_hint_text!!.text = hintContent
        iv_hint_img!!.setImageResource(imgRsId!!)
        showDialog()
        sLoadingDialog!!.setContentView(layoutView!!)// 必须放在show方法后面
    }

    /**
     * 发送关闭窗口的延迟消息
     */
    private fun sendHideMessage() {
        val message = Message.obtain()
        message.what = HANDLE_DELAY
        mHandler.sendMessageDelayed(message, SUCCESS_ERROR_STATE_TIME.toLong())
    }

    // Dialog有可能在activity销毁后，调用，这样会发生dialog找不到窗口的错误，所以需要先判断是否有activity
    private fun showDialog() {
        setWindowAlpha(DEFAULT_ALPHA)
        if (mActivity != null && isValidActivity(mActivity)) {
            sLoadingDialog!!.show()
        }
    }

    private fun hideDialog() {
        if (mActivity != null && isValidActivity(mActivity)) {
            sLoadingDialog!!.dismiss()
        }
        if (callback != null) {
            callback!!.loadCallback()
        }
    }

    /**
     * 判断一个界面是否还存在
     * 使用场景：比如  一个activity被销毁后，它的dialog还要执行某些操作，比如dismiss和show这样是不可以的
     * 因为 dialog是属于activity的
     *
     * @param c
     * @return
     */
    @TargetApi(17)
    private fun isValidActivity(c: Activity?): Boolean {
        if (c == null) {
            return false
        }

        return !(c.isDestroyed || c.isFinishing)
    }

    private fun setWindowAlpha(alpha: Float) {
        val params = mActivity!!.window.attributes
        params.alpha = alpha
        params.verticalMargin = 100f
        mActivity.window.attributes = params
    }

    companion object {
        private val SUCCESS_ERROR_STATE_TIME = 1500// 成功或者失败的停留时间
    }

    interface LoadingCallback{
        fun loadCallback()
    }
}
