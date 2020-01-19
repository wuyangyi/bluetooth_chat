package com.zz.bluetooth_chat.bean.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zz.bluetooth_chat.bean.dao.UserDao

/**
 * author : wuyangyi
 * e-mail : wuyangyi@haitou.cc
 * time   : 2020/01/13
 * desc   : 数据库
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, _DB_NAME, null, _VERSION) {

    companion object{
        //数据库版本
        public val _VERSION: Int = 1
        //数据库名称
        public val _DB_NAME = "bluetooth_chat_db"
    }

    /**
     * 第一次创建数据库时调用 在这方法里面可以进行建表
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(UserDao.CREATE_USER_TABLE)
    }

    /**
    * 版本更新的时候调用
    */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}