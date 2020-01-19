package com.zz.bluetooth_chat.bean.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.zz.bluetooth_chat.bean.UserBean
import com.zz.bluetooth_chat.bean.database.DBHelper

/**
 * author: wuyangyi
 * date  : 2020-01-15
 * e-mail: wuyangyi@haitou.cc
 * desc  : 用户操作类
 */

class UserDao(context: Context) {
    private var mContext = context
    private var dbHelper : DBHelper

    init {
        dbHelper = DBHelper(mContext)
    }

    companion object{
        val userTableName = "UserDao"

        val CREATE_USER_TABLE =
            "create table $userTableName (" +
                "id Integer primary key autoincrement" +
                "deviceId TEXT" +
                "name TEXT" +
                "logo TEXT" +
                "deviceName TEXT" +
                "mSex Integer" +
                "onLine TEXT" +
                "lastMessage TEXT" +
                "lastMessageTime TEXT" +
                "lastMessageLongTime INTEGER" +
                "synopsis TEXT" +
                "phone TEXT" +
                "email TEXT" +
                ")"
    }


    /**
     * 插入一条数据
     */
    fun add(user: UserBean): Long {
        var db: SQLiteDatabase = dbHelper.writableDatabase
        return db.insert(userTableName, null, user.toSql())
    }

    /**
     * 删除一条数据
     */
    fun delect(id: Long) {
        var db: SQLiteDatabase = dbHelper.writableDatabase
        db.delete(userTableName, "id=?", arrayOf("$id"))
    }

    /**
     * 修改指定id的数据
     */
    fun updata(user: UserBean) {
        var db: SQLiteDatabase = dbHelper.writableDatabase
        db.update(userTableName, user.toSql(), "id=?", arrayOf("${user.id ?: "-1"}"))
    }

    /**
     * 查询所有数据
     */
    fun findAll(): ArrayList<UserBean> {
        var list = ArrayList<UserBean>()
        var db: SQLiteDatabase = dbHelper.writableDatabase
        var sql = "select * from $userTableName order by lastMessageLongTime desc"
        var cursor = db.rawQuery(sql, null)
        if (cursor != null) {
            while (cursor.moveToNext()){
                list.add(UserBean.fromSqlToData(cursor))
            }
        }
        return list
    }


}