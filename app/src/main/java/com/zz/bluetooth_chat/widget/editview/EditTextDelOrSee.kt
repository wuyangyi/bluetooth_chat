package com.zz.bluetooth_chat.widget.editview

import android.content.Context
import android.text.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.zz.bluetooth_chat.R
import kotlinx.android.synthetic.main.view_edittext_widget.view.*

/**
 * author: wuyangyi
 * date  : 2020-01-16
 * e-mail: wuyangyi@haitou.cc
 * desc  : 密码和账号文本框，带清除按钮和密码可见按钮
 */

class EditTextDelOrSee : FrameLayout {
    private var isShowPass = false //是否显示密码

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_edittext_widget, this)
        var array = context.obtainStyledAttributes(attrs, R.styleable.EditTextDelOrSee)
        val hintText = array.getString(R.styleable.EditTextDelOrSee_edtHintText)
        val inputType = array.getInteger(R.styleable.EditTextDelOrSee_inputType, 0)
        val maxLength = array.getInteger(R.styleable.EditTextDelOrSee_maxLength, 0)
        array.recycle()

        if (!TextUtils.isEmpty(hintText)) {
            edt_text.hint = hintText
        }

        if (maxLength != 0) {
            edt_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
        }

        iv_del.visibility = View.GONE

        when(inputType) {
            0 -> edt_text.inputType = InputType.TYPE_CLASS_TEXT //文本
            1 -> edt_text.inputType = InputType.TYPE_CLASS_PHONE //手机号
            2 -> edt_text.inputType = InputType.TYPE_CLASS_NUMBER //数字
            3 -> {//密码框
                doPwdIco()
                iv_see.visibility = View.VISIBLE
            }
        }

        iv_see.setOnClickListener {
            isShowPass = !isShowPass
            doPwdIco()
        }

        edt_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_text.text.toString().isEmpty()) {
                    iv_del.visibility = View.GONE
                } else {
                    iv_del.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        iv_del.setOnClickListener(OnClickListener {
            edt_text.setText("")
            iv_del.visibility = View.GONE
        })
    }

    private fun doPwdIco() {
        if (isShowPass) {
            edt_text.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            iv_see.setImageResource(R.mipmap.ico_show_pass)
        } else {
            edt_text.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            iv_see.setImageResource(R.mipmap.ico_hide_pass)
        }
        edt_text.setSelection(edt_text.editableText.length)
    }

    fun getEdtCenter(): EditText {
        return edt_text
    }
}