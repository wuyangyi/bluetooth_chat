package com.zz.bluetooth_chat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.zz.bluetooth_chat.R
import kotlinx.android.synthetic.main.view_my_item_buttom.view.*

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 我的一个点击选项
 */
class MyItemButton : FrameLayout {
    constructor(context: Context): super(context){
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet):super(context, attrs){
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init(context, attrs)
    }

    @SuppressLint("Recycle")
    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.view_my_item_buttom, this)
        val array =  context.obtainStyledAttributes(attrs, R.styleable.MyItemButton)
        val leftImage = array.getDrawable(R.styleable.MyItemButton_leftImage)
        val rightImage = array.getDrawable(R.styleable.MyItemButton_rightImage)
        val leftText = array.getString(R.styleable.MyItemButton_leftText)
        val rightText = array.getString(R.styleable.MyItemButton_rightText)
        val rightHint = array.getString(R.styleable.MyItemButton_rightHintText)
        val leftTextColor = array.getColorStateList(R.styleable.MyItemButton_leftTextColor)
        val rightTextColor = array.getColorStateList(R.styleable.MyItemButton_rightTextColor)
        val rightHintColor = array.getColorStateList(R.styleable.MyItemButton_rightHintColor)
        val showLine = array.getBoolean(R.styleable.MyItemButton_showLine, true)
        array.recycle()

        if (!TextUtils.isEmpty(leftText)) {
            tv_bt_left.text = leftText
        }
        if(leftTextColor != null) {
            tv_bt_left.setTextColor(leftTextColor)
        }
        if(rightTextColor != null) {
            tv_bt_right.setTextColor(rightTextColor)
        }
        if (rightHintColor != null) {
            tv_bt_right.setHintTextColor(rightHintColor)
        }
        if (!TextUtils.isEmpty(rightText)) {
            tv_bt_right.text = rightText
        }
        if (!TextUtils.isEmpty(rightHint)) {
            tv_bt_right.hint = rightHint
        }
        v_buttom_driver.visibility = if (showLine) View.VISIBLE else View.GONE
        if(leftImage == null) {
            iv_bt_left.visibility = View.GONE
        } else{
            iv_bt_left.visibility = View.VISIBLE
            iv_bt_left.setImageDrawable(leftImage)
        }

        if(rightImage == null) {
            iv_bt_right.visibility = View.GONE
        } else{
            iv_bt_right.visibility = View.VISIBLE
            iv_bt_right.setImageDrawable(rightImage)
        }
    }

    fun getIvLeft() : ImageView {
        return iv_bt_left
    }

    fun getIvRight() : ImageView {
        return iv_bt_right
    }

    fun getTvLeft() : TextView {
        return tv_bt_left
    }

    fun getTvRight() : TextView {
        return tv_bt_right
    }
}