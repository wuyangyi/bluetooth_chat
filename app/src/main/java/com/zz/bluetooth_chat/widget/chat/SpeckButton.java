package com.zz.bluetooth_chat.widget.chat;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

import com.zz.bluetooth_chat.R;
import com.zz.bluetooth_chat.util.Utils;

/**
 * author: wuyangyi
 * date  : 2020-01-17
 * e-mail: wuyangyi@haitou.cc
 * desc  : 聊天按下说话按钮
 */
public class SpeckButton extends AppCompatTextView {
    private ChatDialogManager mChatDialogManager;
    private AudioPlayerManager mAudioPlayerManager;

    private float mTime = 0;

    private boolean isRecording;
    private boolean isWantCancel;

    public SpeckButton(Context context) {
        this(context, null);
    }

    public SpeckButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeckButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mChatDialogManager = ChatDialogManager.Companion.getInstance(context);
        String dir = Utils.INSTANCE.getAPP_SYSTEM_PATH() + "recorder_audio";
        mAudioPlayerManager = AudioPlayerManager.Companion.getInstance(dir);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        double down = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down = event.getY();
                setBackground(getResources().getDrawable(R.drawable.bg_speck_down));
                setText("松开 结束");
                handler.sendEmptyMessage(MSG_AUDIO_PREPARED);
                break;
            case MotionEvent.ACTION_MOVE:
                double d =  -(event.getY() - down);
                if (d > getHeight()) {
                    isWantCancel = true;
                    setText("松开 取消");
                    mChatDialogManager.setTextContent(ChatDialogManager.Companion.getACTION_WANG_CANCEL());
                } else {
                    isWantCancel = false;
                    setText("松开 结束");
                    mChatDialogManager.setTextContent(ChatDialogManager.Companion.getACTION_LOADING());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isWantCancel) {//取消
                    mAudioPlayerManager.cancel();
                    handler.sendEmptyMessage(MSG_DIALOG_DIMISS);
                } else if (mTime < 0.6) {
                    mChatDialogManager.setTextContent(ChatDialogManager.Companion.getACTION_SHORT());
                    mAudioPlayerManager.cancel();
                    setBackground(getResources().getDrawable(R.drawable.bg_speck_no_down));
                    setText("按住 说话");
                    handler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else {//正常结束
                    mAudioPlayerManager.release();
                    handler.sendEmptyMessage(MSG_DIALOG_DIMISS);
                    if (mOnWellFinish != null) {
                        mOnWellFinish.onFinish(mAudioPlayerManager.getCurrentFilePath(), mTime);
                    }
                }
                release();
                break;
        }
        return isEnabled();
    }

    private void release() {
        isRecording = false;
        mTime = 0;
        isWantCancel = false;
    }

    public static final int MSG_AUDIO_PREPARED = 0x100;
    public static final int MSG_VOICE_CHANGED = 0x101; //音量改变
    public static final int MSG_DIALOG_DIMISS = 0x102; //时间短

    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    mChatDialogManager.setTextContent(ChatDialogManager.Companion.getACTION_LOADING());
                    mChatDialogManager.show();
                    mAudioPlayerManager.prepareAudio();
                    isRecording = true;
                    new Thread(mGetLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mChatDialogManager.updateSoundLevel(mAudioPlayerManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    setBackground(getResources().getDrawable(R.drawable.bg_speck_no_down));
                    setText("按住 说话");
                    mChatDialogManager.dismiss();
                    break;
            }
        }
    };

    //获取音量大小及时间
    private Runnable mGetLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    handler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private OnWellFinish mOnWellFinish;

    public void setOnWellFinish(OnWellFinish onWellFinish) {
        this.mOnWellFinish = onWellFinish;
    }

    public interface OnWellFinish {
        void onFinish(String path, float time);
    }
}
