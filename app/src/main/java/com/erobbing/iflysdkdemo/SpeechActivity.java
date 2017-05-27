package com.erobbing.iflysdkdemo;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.erobbing.iflysdkdemo.interfaces.SpeechViewUpdateListenner;
import com.erobbing.iflysdkdemo.ui.EmptyActivity;
import com.erobbing.iflysdkdemo.ui.view.SpeechViewNew2;
import com.erobbing.iflysdkdemo.ui.view.UIControl;
import com.iflytek.clientadapter.constant.ErrorValue;
import com.iflytek.clientadapter.tts.CTtsSession;
import com.iflytek.sdk.interfaces.IHmiUiListener;
import com.iflytek.sdk.interfaces.IResultListener;
import com.iflytek.sdk.interfaces.ISvwUiListener;
import com.iflytek.sdk.interfaces.ITtsUiListener;
import com.iflytek.sdk.manager.FlyAppManager;
import com.iflytek.sdk.manager.FlyCmdManager;
import com.iflytek.sdk.manager.FlyConfigManager;
import com.iflytek.sdk.manager.FlyHmiManager;
import com.iflytek.sdk.manager.FlyOtherManager;
import com.iflytek.sdk.manager.FlySvwManager;
import com.iflytek.sdk.manager.FlyTtsManager;

public class SpeechActivity extends Activity {

    private static final String TAG = "SpeechActivity";

    private static final int BACK_KEY = 0x8002;
    private static final int ON_RECORD_START = 0x6001;
    private static final int ON_RECORD_END = 0x6002;
    private static final int ON_RESULT_TEXT = 0x6003;
    private static final int ON_VOLUME_CHANGED = 0x6004;
    private static final int ON_ERROR = 0x6005;
    // 选择情况消息发送
    public final static int MSG_HANG_CALL = 10083;
    public final static int MSG_LISTEN_CALL = 10084;
    public final static int MSG_PRE_PAGE = 10085;
    public final static int MSG_NEXT_PAGE = 10086;
    public final static int MSG_MAKE_CALL = 10087;
    public final static int MSG_CANCLE = 10088;

    // 导航选择页面正在加载
    public final static int MSG_NAVI_LOADING = 10089;
    public final static int MSG_NAVI_CHANGEDATA = 10090;

    public final static int MSG_RESTART_INTERACTION = 10091;
    public final static int MSG_NAVI_SELECTED = 10092;
    public final static int MSG_NAVI_CLOSEPAGE = 10093;
    public final static int MSG_APP_CLOSEPAGE = 10094;
    public final static int MSG_CMD_CLOSEPAGE = 10095;

    private WindowManager mWindowManager;
    private static Handler mHandler;
    private LinearLayout mSpeechActivity;
    private SpeechViewUpdateListenner mSpeechViewNew;
    private Context mContext = null;
    // 判断当前导航选择后是否有下一次选择（针对两点导航）
    private boolean hasTwice;
    // public static NaviActivity naviActivity;

    /**
     * 是否在前台 true 在前台 false 不在前台
     */
    private boolean isShowing = true;

    /**
     * "我没听请重说"的次数
     */
    private int sayReaptNumber = 0;

    public static ITtsUiListener mITtsUiListener = new ITtsUiListener() {

        @Override
        public void onProgress(int textindex, int textlen) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPlayCompleted() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPlayBegin() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onInterrupted() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(int errorid) {
            // TODO Auto-generated method stub

        }
    };

    static class MyHandler extends Handler { // 去除HandlerLeak警告
        private final WeakReference<SpeechActivity> mActivity;

        public MyHandler(SpeechActivity speechActivity) {
            mActivity = new WeakReference<SpeechActivity>(speechActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SpeechActivity speechActivity = mActivity.get();
            // Log.d(TAG, "msg.what=" + msg.what);
            switch (msg.what) {
                case ON_RECORD_START:
                    UIControl.getInstance().getUIListenner()
                            .updateUIInRecodingState();
                    break;
                case ON_VOLUME_CHANGED:
                    UIControl.getInstance().getUIListenner()
                            .updateUIRecodingVolume(msg.arg1);
                    break;
                case ON_RECORD_END:
                    UIControl.getInstance().getUIListenner()
                            .updateUIInWaitingResultState();
                    break;
                case ON_RESULT_TEXT:
                    Bundle data = msg.getData();
                    UIControl
                            .getInstance()
                            .getUIListenner()
                            .updateUIAfterResult(data.getString("focus"),
                                    (String) msg.obj);

                    if ("pattern".equals(data.getString("focus"))
                            && "查看版本号".equals((String) msg.obj)) {
                        String version = FlyConfigManager.getInstance()
                                .getSpeechVersion();
                        Toast.makeText(speechActivity, version, Toast.LENGTH_LONG).show();
                    }
                    break;
                case ON_ERROR:
                    break;
                case MSG_NEXT_PAGE:
                    UIControl.getInstance().getTelListener().nextPage();
                    break;
                case MSG_PRE_PAGE:
                    UIControl.getInstance().getTelListener().prePage();
                    break;
                case BACK_KEY:
                    speechActivity.finish();
                    break;
                case MSG_RESTART_INTERACTION:
                    FlyHmiManager.getInstance().startInteraction();
                    break;
                case MSG_APP_CLOSEPAGE:
                    String operation = "";
                    String name = "";
                    try {
                        JSONObject mJsonObject = new JSONObject((String) msg.obj);
                        operation = mJsonObject.getString("operation");
                        name = mJsonObject.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(speechActivity, name + " " + operation,
                            Toast.LENGTH_LONG).show();
                    if ("EXIT".equalsIgnoreCase(operation)) {
                        FlyTtsManager.getInstance().create(
                                AudioManager.STREAM_MUSIC);
                        FlyTtsManager.getInstance().speak("已为您关闭" + name,
                                mITtsUiListener);
                    }
                    if ("LAUNCH".equalsIgnoreCase(operation)) {
                        FlyTtsManager.getInstance().create(
                                AudioManager.STREAM_MUSIC);
                        FlyTtsManager.getInstance().speak("已为您打开" + name,
                                mITtsUiListener);
                    }
                    break;
                case MSG_CMD_CLOSEPAGE:
                    String category = "";
                    String nameCMD = "";
                    try {
                        JSONObject mJsonObject = new JSONObject((String) msg.obj);
                        category = mJsonObject.getString("category");
                        nameCMD = mJsonObject.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(speechActivity, category, Toast.LENGTH_LONG)
                            .show();
                    dealresullt(category, nameCMD);
                    break;
                default:
                    break;
            }
        }
    }

    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d("AudioFocus", "focusChage = " + focusChange);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mHandler = new MyHandler(this);
        mContext = SpeechDemoApplication.getContext();
        SpeechDemoApplication.addActivity(this);
    }

    public static void dealresullt(String category, String nameCMD) {

        if ("音量控制".equalsIgnoreCase(category)) {
            if ("音量+".equalsIgnoreCase(nameCMD)) {
                FlyTtsManager.getInstance().create(AudioManager.STREAM_MUSIC);
                FlyTtsManager.getInstance().speak("已为您加大音量", mITtsUiListener);
            }
            if ("音量-".equalsIgnoreCase(nameCMD)) {
                FlyTtsManager.getInstance().create(AudioManager.STREAM_MUSIC);
                FlyTtsManager.getInstance().speak("已为您减小音量", mITtsUiListener);
            }
        }
        if ("亮度控制".equalsIgnoreCase(category)) {
            if ("亮度+".equalsIgnoreCase(nameCMD)) {
                FlyTtsManager.getInstance().create(AudioManager.STREAM_MUSIC);
                FlyTtsManager.getInstance().speak("屏幕已调亮", mITtsUiListener);
            }
            if ("亮度-".equalsIgnoreCase(nameCMD)) {
                FlyTtsManager.getInstance().create(AudioManager.STREAM_MUSIC);
                FlyTtsManager.getInstance().speak("屏幕已调暗", mITtsUiListener);
            }
        }

    }

    private void initElse() {
        FlyHmiManager.getInstance().setListener(new IHmiUiListener() {

            @Override
            public void onVolume(int volume) {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = ON_VOLUME_CHANGED;
                msg.arg1 = volume;
                mHandler.sendMessage(msg);
                // Log.d(TAG, "--->onVolume :" + volume);
            }

            @Override
            public void onResultText(String focus, String result) {
                // TODO Auto-generated method stub
                Log.d(TAG, "focus=" + focus + ", onResult=" + result);
                Message msg = new Message();
                msg.what = ON_RESULT_TEXT;
                if (TextUtils.isEmpty(result)) {
                    result = "抱歉没有识别到结果";
                }
                msg.obj = result;
                Bundle data = new Bundle();
                data.putString("focus", focus);
                msg.setData(data);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onRecordStart() {
                // TODO Auto-generated method stub
                Log.d(TAG, "--->onRecordStart");
                mHandler.sendEmptyMessage(ON_RECORD_START);
            }

            @Override
            public void onRecordEnd() {
                // TODO Auto-generated method stub
                Log.d(TAG, "--->onRecordEnd");
                mHandler.sendEmptyMessage(ON_RECORD_END);
            }

            @Override
            public void onInteractionStart() {
                // TODO Auto-generated method stub
                Log.d(TAG, "--->onInteractionStart");
            }

            @Override
            public void onInteractionEnd() {
                // TODO Auto-generated method stub
                Log.d(TAG, "--->onInteractionEnd");
                SpeechDemoApplication.removeALLActivity();
                finish();
            }

            @Override
            public void onError(int errorid, String tips) {
                // TODO Auto-generated method stub
                if (!isShowing) {
                    Log.e(TAG, "onError() speechactivity 在后台");
                    return;
                }
                onRecordEnd();
                if (10007 == errorid) {
                    FlyTtsManager.getInstance().create(
                            AudioManager.STREAM_MUSIC);
                    FlyTtsManager.getInstance().speak("", new ITtsUiListener() {

                        @Override
                        public void onProgress(int textindex, int textlen) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onPlayCompleted() {
                            // TODO Auto-generated method stub
                            onShowTips("可能网络不太好，请稍后再试试吧");
                        }

                        @Override
                        public void onPlayBegin() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onInterrupted() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onError(int errorid) {
                            // TODO Auto-generated method stub

                        }
                    });
                } else if (20004 == errorid || 10118 == errorid
                        || 10015 == errorid) {
                    setReaptNumber(getReaptNumber() + 1);
                    if (getReaptNumber() > 3) {
                        return;
                    }
                    String tip = "我没听清，再说一遍吧";
                    if (getReaptNumber() > 2) {
                        tip = "还是没听到您说话";
                    }
                    onResultText("main", tip);
                    FlyTtsManager.getInstance().create(
                            AudioManager.STREAM_MUSIC);
                    FlyTtsManager.getInstance().speak(tip,
                            new ITtsUiListener() {

                                @Override
                                public void onProgress(int textindex,
                                                       int textlen) {
                                    // TODO Auto-generated method stub
                                }

                                @Override
                                public void onPlayCompleted() {
                                    // TODO Auto-generated method stub
                                    // 开始交互
                                    if (getReaptNumber() <= 2) {
                                        mHandler.sendEmptyMessageDelayed(
                                                MSG_RESTART_INTERACTION, 500);
                                    } else {
                                        Intent intent = new Intent();
                                        intent.setComponent(new ComponentName(
                                                "com.erobbing.iflysdkdemo",
                                                "com.erobbing.iflysdkdemo.ui.HelpActivityNew1"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                        finish();
                                    }

                                }

                                @Override
                                public void onPlayBegin() {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onInterrupted() {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onError(int errorid) {
                                    // TODO Auto-generated method stub

                                }
                            });
                }

            }

            @Override
            public void onShowTips(String tips) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(SpeechActivity.this, EmptyActivity.class);
                intent.putExtra("tips", tips);
                startActivity(intent);
            }
        });

        FlyOtherManager.getInstance().setListener(new IResultListener() {

            @Override
            public boolean onResultHandle(String result) {
                // TODO 应该区分开在传递

                // 关闭弹出框
                Intent intent1 = new Intent("com.iflytek.autofly.POP_SPEECH");
                intent1.setPackage("com.erobbing.iflysdkdemo");
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("com.iflytek.autofly.speechservice.operation",
                        "EXIT");
                startService(intent1);

                // 开启帮助界面
                Intent intent = new Intent();
                intent.putExtra("CATEGORY", getCategory(result));
                intent.setComponent(new ComponentName("com.erobbing.iflysdkdemo",
                        "com.erobbing.iflysdkdemo.ui.HelpActivityNew1"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                return false;
            }
        });

        FlyAppManager.getInstance().setListener(new IResultListener() {

            @Override
            public boolean onResultHandle(String result) {
                Log.d(TAG, "FlyAppManager result:" + result);
                Message msg = Message.obtain();
                msg.what = MSG_APP_CLOSEPAGE;
                msg.obj = result;
                mHandler.sendMessage(msg);
                return false;
            }
        });

        FlyCmdManager.getInstance().setListener(new IResultListener() {

            @Override
            public boolean onResultHandle(String result) {
                Log.d(TAG, "FlyCmdManager result:" + result);
                Message msg = Message.obtain();
                msg.what = MSG_CMD_CLOSEPAGE;
                msg.obj = result;
                mHandler.sendMessage(msg);
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN);
        setAutoMuteStatus(true);

        if (ErrorValue.INIT_SUCCESS != FlyHmiManager.getInstance()
                .isInitComplete()) {
            Log.d(TAG, "onResume() start loading");
            Intent intent = new Intent(this, LoadActivity.class);
            startActivity(intent);
            return;
        }

        isShowing = true;
        setReaptNumber(0);

        // 停止tts交互
        CTtsSession.getInstance(this).stop();

        initElse();
        initSpeechView(this);

        setScenWakeup();

        UIControl.getInstance().setListenner(mSpeechViewNew);
        // 开始交互
        FlyHmiManager.getInstance().startInteraction();
        Log.d(TAG, "--->开始交互 !!!");
        Log.d(TAG, "SpeechActivity----------->onResume");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doFinish();
        isShowing = false;
        Log.d(TAG, "SpeechActivity----------->onPause");
    }

    /**
     * 进入后台
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "SpeechActivity----------->onStop");
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        setAutoMuteStatus(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SpeechActivity----------->onDestory");
        if (mSpeechViewNew != null) {
            mSpeechViewNew.destroyView();
        }
        Log.d(TAG, "SpeechActivity onDestroy Instance = " + this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void setAutoMuteStatus(boolean isMute) {
        Intent intent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
        intent.putExtra("KEY_TYPE", 10047);
        if (isMute) {
            intent.putExtra("EXTRA_MUTE", 1);
            // intent.putExtra("EXTRA_CASUAL_MUTE", 1);
        } else {
            intent.putExtra("EXTRA_MUTE", 0);
            // intent.putExtra("EXTRA_CASUAL_MUTE", 0);
        }
        sendBroadcast(intent);
    }

    /**
     * 执行清理工作
     */
    public void doFinish() {
        // 关闭交互
        closeScenWakeup();
        setReaptNumber(0);
        FlyHmiManager.getInstance().cancelInteraction();
        if (mWindowManager != null && null != mSpeechActivity) {
            mWindowManager.removeView(mSpeechActivity);
        }
        this.finish();
    }

    private void getDisplaysMetrics() {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        Log.d(TAG, "Save DisplayMetrics = " + displaysMetrics.heightPixels
                + "*" + displaysMetrics.widthPixels);
        Log.d(TAG, "width =" + displaysMetrics.widthPixels);
        Log.d(TAG, "height =" + displaysMetrics.heightPixels);
    }

    /**
     * 初始化语音控件
     */
    private void initSpeechView(final Context context) {

        // 计算mic区域的宽度和高度
        getDisplaysMetrics();
        setContentView(R.layout.activity_speech);
        mSpeechActivity = (LinearLayout) findViewById(R.id.speech_activity);
        mSpeechViewNew = (SpeechViewNew2) findViewById(R.id.dialog_speechview);

    }

    /**
     * 处理键盘事件
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatch key code" + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mHandler.sendEmptyMessage(BACK_KEY);
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "SpeechActivity----------->onNewIntent");
    }

    /**
     * 设置主场景取消命令
     */
    private void setScenWakeup() {
        FlySvwManager.getInstance().start("main", "取消", new ISvwUiListener() {

            @Override
            public void onError(int errorid) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCusWakeup(String scene, int id, String result,
                                    int score) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    /**
     * 关闭场景唤醒
     */
    private void closeScenWakeup() {
        FlySvwManager.getInstance().stop(0);
    }

    private String getCategory(String result) {
        // TODO Auto-generated method stub
        String category = "";
        if (TextUtils.isEmpty(result)) {
            return "";
        }

        try {
            JSONObject jsObj = new JSONObject(result);
            if (jsObj.has("semantic")) {
                JSONObject jsObj1 = jsObj.getJSONObject("semantic");
                JSONObject jsObj2 = jsObj1.getJSONObject("slots");
                category = jsObj2.getString("category");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return category;
    }

    private void setReaptNumber(int number) {
        this.sayReaptNumber = number;
    }

    private int getReaptNumber() {
        return this.sayReaptNumber;
    }

}
