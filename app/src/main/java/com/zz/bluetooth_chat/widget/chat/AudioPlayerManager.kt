package com.zz.bluetooth_chat.widget.chat

import android.media.MediaRecorder
import java.io.File
import java.util.*

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  :
 */
class AudioPlayerManager(private val mDir: String) {

    private var mMediaRecorder: MediaRecorder? = null
    private var isPrepared: Boolean = false //是否准备完毕
    var mCurrentFilePath: String? = null //音频路径


    fun getCurrentFilePath(): String {
        return mCurrentFilePath!!
    }
    /**
     * 随机生成文件名
     * @return
     */
    private val fileName: String
        get() = UUID.randomUUID().toString() + "amr"

    fun prepareAudio() {
        try {
            isPrepared = false
            val dir = File(mDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val fileName = fileName
            val file = File(dir, fileName)
            mCurrentFilePath = file.absolutePath
            mMediaRecorder = MediaRecorder()
            //设置输出文件
            mMediaRecorder!!.setOutputFile(file.absolutePath)
            //设置MediaRecorder的音频原为麦克风
            mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            //设置音频格式
            mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR)
            //设置编码方式
            mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder!!.prepare()
            mMediaRecorder!!.start()
            isPrepared = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获得音量等级(1-maxLevel)
     * @param maxLevel
     * @return
     * mMediaRecorder.getMaxAmplitude()  1 - 32768
     */
    fun getVoiceLevel(maxLevel: Int): Int {
        if (isPrepared) {
            try {
                return maxLevel * mMediaRecorder!!.maxAmplitude / 32768 + 1
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

        }
        return 1
    }

    //释放资源
    fun release() {
        mMediaRecorder!!.stop()
        mMediaRecorder!!.reset()
        mMediaRecorder!!.release()
        mMediaRecorder = null
        isPrepared = false
    }

    //取消发送
    fun cancel() {
        release()
        //删除文件
        if (mCurrentFilePath != null) {
            val file = File(mCurrentFilePath!!)
            file.delete()
            mCurrentFilePath = null
        }
    }

    companion object {
        private var audioPlayerManager: AudioPlayerManager? = null

        fun getInstance(dir: String): AudioPlayerManager {
            if (audioPlayerManager == null) {
                synchronized(AudioPlayerManager::class.java) {
                    audioPlayerManager = AudioPlayerManager(dir)
                }
            }
            return audioPlayerManager!!
        }
    }
}