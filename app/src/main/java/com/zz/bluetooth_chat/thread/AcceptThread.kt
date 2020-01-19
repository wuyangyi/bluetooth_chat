package com.zz.bluetooth_chat.thread

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : 蓝牙服务器端线程，监听设备连接
 */

class AcceptThread : Thread {
    private var mBtServerSocket: BluetoothServerSocket? = null
    private var mBtSocket: BluetoothSocket? = null
    private var mInputStream: InputStream? = null
    private var mOutputStream: OutputStream? = null
    private var isCanAccept: Boolean = false
    private var isCanRecv: Boolean = false
    private var mHandler: Handler? = null

    constructor(adapter: BluetoothAdapter, uuid: String, handler: Handler) {
        var tmp: BluetoothServerSocket? = null
        mHandler = handler

        try {
            tmp = adapter.listenUsingInsecureRfcommWithServiceRecord(
                "Chat Server",
                UUID.fromString(uuid)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }


        mBtServerSocket = tmp
    }

    override fun run() {
        try {
            if (mBtServerSocket != null) {
                //阻塞
                mBtSocket = mBtServerSocket!!.accept()
                Log.i("服务器", "收到连接")
                mBtServerSocket!!.close()
                mInputStream = mBtSocket!!.inputStream
                mOutputStream = mBtSocket!!.outputStream
                val buffer = ByteArray(1024)  // buffer store for the stream
                var bytes: Int // bytes returned from read()
//              Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = mInputStream!!.read(buffer)
                        // Send the obtained bytes to the UI activity
                        val s = String(buffer, 0, bytes)
                        sendHandlerMsg("$s")

                    } catch (e: IOException) {
                        break
                    }

                }
            }
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun sendHandlerMsg(content: String) {

        val msg = mHandler!!.obtainMessage()
        msg.what = 1001
        msg.obj = content
        mHandler!!.sendMessage(msg)

    }

    fun cancel() {

        try {
            mBtServerSocket!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun write(msg: String) {
        try {
            if (mOutputStream != null) {
                mOutputStream!!.write(msg.toByteArray())
                sendHandlerMsg("me：$msg")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}