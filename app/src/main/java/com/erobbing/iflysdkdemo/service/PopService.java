package com.erobbing.iflysdkdemo.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erobbing.iflysdkdemo.R;
import com.erobbing.iflysdkdemo.SpeechActivity;
import com.erobbing.iflysdkdemo.SpeechDemoApplication;
import com.iflytek.sdk.interfaces.IHmiUiListener;
import com.iflytek.sdk.manager.FlyHmiManager;

import android.provider.Settings;

/**
 * 空
 *
 * @author pengtong
 */
public class PopService extends Service {

    private static final String TAG = PopService.class.getSimpleName();
    private static final int NOTIFY_ID = 1;

    private RelativeLayout mFloatLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private TextView speechText;
    private Handler mHandler;

    private ImageView mCircle;
    private ImageView mCircleBar;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        /*Notification notification = new Notification();
		PendingIntent p_intent = PendingIntent.getActivity(this, 0, new Intent(
				this, SpeechActivity.class), 0);
		notification.setLatestEventInfo(this, "AutoFlyService", "汽车语音助理",
				p_intent);
		startForeground(NOTIFY_ID, notification);*/

        // zhangzhaolei add new method
        Notification.Builder builder = new Notification.Builder(this);
        //builder.setContentInfo("补充内容");
        builder.setContentText("汽车语音助理");
        builder.setContentTitle("AutoFlyService");
        builder.setSmallIcon(R.drawable.recording1);
        builder.setTicker("新消息");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(this, SpeechActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(NOTIFY_ID, notification);


        Log.d(TAG, "PopService");
        mWindowManager = (WindowManager) getApplication().getSystemService(
                Context.WINDOW_SERVICE);
        params = SpeechDemoApplication.getApp().getWmParams();
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        mFloatLayout = (RelativeLayout) inflater.inflate(
                R.layout.activity_main_dialog, null);
        params.height = 100;
        params.gravity = Gravity.BOTTOM;
        // 设置图片格式，效果为背景透明
        params.format = PixelFormat.RGBA_8888;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags = params.flags
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.alpha = 50;
        mFloatLayout.setVisibility(View.GONE);

        mHandler = new MyHandler();
        //zhangzhaolei + permission for SYSTEM_ALERT_WINDOW api >= 23
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent in = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(in);
            } else {
                mWindowManager.addView(mFloatLayout, params);
            }
        } else {
            mWindowManager.addView(mFloatLayout, params);
        }
    }

    /**
     * 初始化语音控件
     */
    private void initSpeechViewDialog() {

        // 计算mic区域的宽度和高度
        mCircle = (ImageView) mFloatLayout.findViewById(R.id.circle);
        mCircleBar = (ImageView) mFloatLayout.findViewById(R.id.circle_bar);
        speechText = (TextView) mFloatLayout.findViewById(R.id.circle_bar_text);

        Animation rotateAnim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_slow);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        mCircle.startAnimation(rotateAnim);

        mCircle.setVisibility(View.VISIBLE);
        mCircleBar.setVisibility(View.VISIBLE);
        speechText.setVisibility(View.INVISIBLE);
        mFloatLayout.setVisibility(View.VISIBLE);

        FlyHmiManager.getInstance().setListener(new IHmiUiListener() {

            @Override
            public void onVolume(int volume) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onShowTips(String tips) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onResultText() onShowTips = " + tips);
                Message msg = new Message();
                msg.what = ON_RESULT_TEXT;
                msg.obj = tips;
                if (null != mHandler) {
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onResultText(String focus, String result) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onResultText() result = " + result);
                Message msg = new Message();
                msg.what = ON_RESULT_TEXT;
                if (TextUtils.isEmpty(result)) {
                    result = "抱歉没有识别到结果";
                }
                msg.obj = result;
                if (null != mHandler) {
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onRecordStart() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRecordEnd() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onInteractionStart() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onInteractionEnd() {
                // TODO Auto-generated method stub
                if (null != mHandler) {
                    mHandler.sendEmptyMessageAtTime(ON_CLOSE, 1000);
                }
            }

            @Override
            public void onError(int errorid, String tips) {
                // TODO Auto-generated method stub
                if (null != mHandler) {
                    mHandler.sendEmptyMessageAtTime(ON_CLOSE, 1000);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        String operation = null;
        if (intent != null
                && intent
                .hasExtra("com.iflytek.autofly.speechservice.operation")) {
            operation = intent
                    .getStringExtra("com.iflytek.autofly.speechservice.operation");
            Log.d(TAG, "com.iflytek.autofly.speechservice.operation "
                    + operation);
        }
        if ("EXIT".equals(operation)) {
            if (mFloatLayout != null) {
                mFloatLayout.setVisibility(View.GONE);
            }
            FlyHmiManager.getInstance().cancelInteraction();
            // 停止定时器(关闭界面)
            stopTimer();
        } else if ("INTERACTION".equals(operation)) {
            initSpeechViewDialog();
            // 启动定时器(关闭界面)
            resetTimer();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    private static final int ON_RECORD_START = 0x6001;
    private static final int ON_RECORD_END = 0x6002;
    private static final int ON_RESULT_TEXT = 0x6003;
    private static final int ON_VOLUME_CHANGED = 0x6004;
    private static final int ON_CLOSE = 0x6005;
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

    class MyHandler extends Handler { // 去除HandlerLeak警告

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ON_RECORD_START:
                    break;
                case ON_VOLUME_CHANGED:
                    break;
                case ON_RECORD_END:
                    break;
                case ON_RESULT_TEXT:
                    if (null != speechText) {
                        speechText.setText((String) msg.obj);
                        speechText.setVisibility(View.VISIBLE);
                    }
                    if (null != mHandler) {
                        mHandler.sendEmptyMessageDelayed(ON_CLOSE, 1000);
                    }
                    break;
                case ON_CLOSE:
                    if (mFloatLayout != null) {
                        if (null != mCircle) {
                            mCircle.clearAnimation();
                        }
                        mFloatLayout.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /* 时间定时器 */
    private static final int OP_TIMEOUT = 20000;
    private Timer opTimer;

    private void createTimer() {
        if (null != opTimer) {
            opTimer.cancel();
        }
        opTimer = new Timer();
        opTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                // 关闭界面显示
                if (null != mHandler) {
                    mHandler.sendEmptyMessageAtTime(ON_CLOSE, 1000);
                }
            }
        }, OP_TIMEOUT);
    }

    private void resetTimer() {
        stopTimer();
        createTimer();
    }

    private void stopTimer() {
        if (null != opTimer) {
            opTimer.cancel();
        }
    }

	/* 时间定时器end */

}
