package com.zz.bluetooth_chat.util

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.zz.bluetooth_chat.base.BaseApplication


/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   : Toast工具类
 */
object ToastUtils {
    private var toastText: Toast? = null
    private var toastView: Toast? = null

    fun showToast(resID: Int) {
        showToast(BaseApplication.getContext()!!, Toast.LENGTH_SHORT, resID)
    }

    fun showToast(text: String) {
        showToast(BaseApplication.getContext()!!, Toast.LENGTH_SHORT, text)
    }

    fun showToast(ctx: Context, resID: Int) {
        showToast(ctx, Toast.LENGTH_SHORT, resID)
    }

    fun showToast(ctx: Context, text: String) {
        showToast(ctx, Toast.LENGTH_SHORT, text)
    }

    fun showLongToast(ctx: Context, resID: Int) {
        showToast(ctx, Toast.LENGTH_LONG, resID)
    }

    fun showLongToast(resID: Int) {
        showToast(BaseApplication.getContext()!!, Toast.LENGTH_LONG, resID)
    }

    fun showLongToast(ctx: Context, text: String) {
        showToast(ctx, Toast.LENGTH_LONG, text)
    }

    fun showLongToast(text: String) {
        showToast(BaseApplication.getContext()!!, Toast.LENGTH_LONG, text)
    }

    fun showToast(ctx: Context, duration: Int, resID: Int) {
        showToast(ctx, duration, ctx.getString(resID))
    }

    /**
     * Toast一个图片
     */
    fun showToastImage(ctx: Context, resID: Int): Toast {
        val toast = Toast.makeText(ctx, "", Toast.LENGTH_SHORT)
        val mNextView = toast.view
        mNextView?.setBackgroundResource(resID)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
        return toast
    }

    /**
     * Toast防止时间累积
     * android9.0，重复使用同一个Toast会hide掉当前的，则会为空，所以在android9.0以上直接每次使用就实例化一个Toast使用, 并不用防止重复的显示的问题
     */
    fun showToast(
        ctx: Context, duration: Int,
        text: String
    ) {
        if (NotificationManagerCompat.from(ctx).areNotificationsEnabled()) { //通知权限是否打开
            if (toastText != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                toastText!!.setText(text)
                toastText!!.duration = duration
            } else {
                toastText = Toast.makeText(ctx, text, duration)
                toastText!!.setGravity(Gravity.CENTER, 0, 0)
            }
            toastText!!.show()
        } else {
            com.hjq.toast.ToastUtils.show(text)
        }
    }

    /**
     * toast一个自定义布局
     */
    fun showToast(v: View, context: Context) {
        if (toastView != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            toastView!!.view = v
            toastView!!.duration = Toast.LENGTH_SHORT
            toastView!!.show()
        } else {
            toastView = Toast.makeText(context, "", Toast.LENGTH_SHORT)
            toastView!!.view = v
            toastView!!.setGravity(Gravity.CENTER, 0, 0)
            toastView!!.duration = Toast.LENGTH_SHORT
            toastView!!.show()
        }
    }
}