package com.zz.bluetooth_chat.bean

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/11
 * desc   : 用户
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class UserBean(
    var id: Long? = null,

    var deviceId: String = "", //唯一
    var name: String = "",
    var logo: String = "",
    var deviceName: String = "",
    var sex: Int? = 0,   //0未知、1男、2女
    var onLine: Boolean = true, //是否在线

    var needLogin: Boolean = false, //是否需要登录

    var password: String = "", //密码
    var synopsis: String = "", //简介
    var phone: String = "", //手机号
    var email: String = "", //email

    var lastMessage: String = "", //最后一次的消息
    var lastMessageTime: String = "" //最后一条消息的时间
) : BaseListBean(), Parcelable {

    companion object {
        val USER_INFO_NAME = "kotlinbluetoothchat_user_info"

        fun fromSqlToData(cursor: Cursor) : UserBean {
            var userBean = UserBean()
            if (cursor == null) {
                return userBean
            }
            userBean.id = cursor.getLong(cursor.getColumnIndex("id"))
            userBean.deviceId = cursor.getString(cursor.getColumnIndex("deviceId"))
            userBean.name = cursor.getString(cursor.getColumnIndex("name"))
            userBean.logo = cursor.getString(cursor.getColumnIndex("logo"))
            userBean.deviceName = cursor.getString(cursor.getColumnIndex("deviceName"))
            userBean.sex = cursor.getInt(cursor.getColumnIndex("mSex"))
            userBean.onLine = cursor.getString(cursor.getColumnIndex("onLine")) == "true"
            userBean.lastMessage = cursor.getString(cursor.getColumnIndex("lastMessage"))
            userBean.lastMessageTime = cursor.getString(cursor.getColumnIndex("lastMessageTime"))
            userBean.longTime = cursor.getLong(cursor.getColumnIndex("lastMessageLongTime"))
            userBean.synopsis = cursor.getString(cursor.getColumnIndex("synopsis"))
            userBean.phone = cursor.getString(cursor.getColumnIndex("phone"))
            userBean.email = cursor.getString(cursor.getColumnIndex("email"))
            return userBean
        }
    }



    fun saveUserData(context: Activity) {
        val sharePreferences = context.getSharedPreferences(USER_INFO_NAME, MODE_PRIVATE)
        val editor = sharePreferences.edit()
        editor.putLong("id", id ?: -1)
        editor.putString("deviceId", deviceId)
        editor.putString("name", name)
        editor.putString("logo", logo)
        editor.putString("deviceName", deviceName)
        editor.putInt("mSex", sex ?: 0)
//        editor.putBoolean("onLine", onLine ?: false)
        editor.putBoolean("needLogin", needLogin ?: false)
        editor.putString("password", password)
        editor.putString("synopsis", synopsis)
        editor.putString("phone", phone)
        editor.putString("email", email)
        editor.apply()
    }

    fun initData(context: Activity) {
        val sharePreferences = context.getSharedPreferences(USER_INFO_NAME, MODE_PRIVATE)
        id = sharePreferences.getLong("id", -1)
        deviceId = sharePreferences.getString("deviceId", "").toString()
        name = sharePreferences.getString("name", "").toString()
        logo = sharePreferences.getString("logo", "").toString()
        deviceName = sharePreferences.getString("deviceName", "").toString()
        sex = sharePreferences.getInt("mSex", 0)
//        onLine = sharePreferences.getBoolean("onLine", false)
        password = sharePreferences.getString("password", "").toString()
        needLogin = sharePreferences.getBoolean("needLogin", false)
        synopsis = sharePreferences.getString("synopsis", "").toString()
        phone = sharePreferences.getString("phone", "").toString()
        email = sharePreferences.getString("email", "").toString()
    }

    fun toSql() : ContentValues {
        var values = ContentValues()
        values.put("id", id)
        values.put("deviceId", deviceId)
        values.put("name", name)
        values.put("logo", logo)
        values.put("deviceName", deviceName)
        values.put("mSex", sex)
        values.put("onLine", onLine.toString())
        values.put("lastMessage", lastMessage)
        values.put("lastMessageTime", lastMessageTime)
        values.put("lastMessageLongTime", longTime)
        values.put("synopsis", synopsis)
        values.put("phone", phone)
        values.put("email", email)
        return values
    }

}