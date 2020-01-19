package com.zz.bluetooth_chat.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.lang.reflect.Field

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   : 设备相关工具
 */
object DeviceUtils {

    var bottomMenuHeight = 500 //软键盘高度

    /**
     * 判断是否存在sd卡
     *
     * @return
     */
    val isExitsSdcard: Boolean
        get() = android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED

    /**
     * 获得状态栏高度
     * @param context
     * @return
     */
    fun getStatuBarHeight(context: Context, activity: Activity): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return 0
        }
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var sbar = 38// 默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field.get(obj).toString())
            sbar = context.resources
                .getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        val h = getHeight(activity)
        return Math.max(h, sbar)
    }

    // 刘海高度
    fun getHeight(mAc: Activity): Int {
        val decorView = mAc.window.decorView
        if (decorView != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                val windowInsets = decorView.rootWindowInsets
                if (windowInsets != null) {
                    //                    DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                    //                    return displayCutout.getSafeInsetTop();
                }
            }
        }
        return 0
    }

    /**
     * 获得屏幕密度
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    fun hideSoftKeyboard(context: Context, view: View?) {
        if (view == null) {
            return
        }
        val inputMethodManager = context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken, 0
            )
        }
    }

    fun sp2px(context: Context, sp: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (sp * fontScale + 0.5f).toInt()
    }

    fun px2sp(context: Context, pxVal: Float): Int {
        return (pxVal / context.resources.displayMetrics.scaledDensity).toInt()
    }

    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun px2dp(context: Context, pxVal: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxVal / scale).toInt()
    }

    fun dpToPixel(context: Context, dp: Float): Float {
        return dp * (getDisplayMetrics(context).densityDpi / 160f)
    }
}
