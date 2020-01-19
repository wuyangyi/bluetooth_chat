package com.zz.bluetooth_chat.util

import android.view.Gravity
import com.hjq.toast.IToastStyle

/**
 * author: wuyangyi
 * date  : 2020-01-14
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */

class ToastSystemStyle : IToastStyle {
    override fun getGravity(): Int {
        return Gravity.CENTER
    }

    override fun getXOffset(): Int {
        return 0
    }

    override fun getYOffset(): Int {
        return 0
    }

    override fun getZ(): Int {
        return 0
    }

    override fun getCornerRadius(): Int {
        return 15
    }

    override fun getBackgroundColor(): Int {
        return -0xcccccd
    }

    override fun getTextColor(): Int {
        return -0x1c1c1d
    }

    override fun getTextSize(): Float {
        return 12f
    }

    override fun getMaxLines(): Int {
        return 3
    }

    override fun getPaddingLeft(): Int {
        return 10
    }

    override fun getPaddingTop(): Int {
        return 7
    }

    override fun getPaddingRight(): Int {
        return paddingLeft
    }

    override fun getPaddingBottom(): Int {
        return paddingTop
    }
}
