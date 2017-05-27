package com.erobbing.iflysdkdemo.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.erobbing.iflysdkdemo.R;
import com.erobbing.iflysdkdemo.ui.view.HelpAdas;
import com.erobbing.iflysdkdemo.ui.view.HelpAllView;
import com.erobbing.iflysdkdemo.ui.view.HelpDod;
import com.erobbing.iflysdkdemo.ui.view.HelpMusic;
import com.erobbing.iflysdkdemo.ui.view.HelpNav;
import com.erobbing.iflysdkdemo.ui.view.HelpPhone;
import com.erobbing.iflysdkdemo.ui.view.HelpQuery;
import com.erobbing.iflysdkdemo.ui.view.HelpSystem;
import com.erobbing.iflysdkdemo.ui.view.HelpVoice;
import com.erobbing.iflysdkdemo.ui.view.PagerAdapterX;
import com.erobbing.iflysdkdemo.ui.view.ViewPagerX;
import com.iflytek.clientadapter.mvw.CMvwSession;
import com.iflytek.clientadapter.tts.CTtsSession;
import com.iflytek.sdk.interfaces.ISvwUiListener;
import com.iflytek.sdk.interfaces.ITtsUiListener;
import com.iflytek.sdk.manager.FlySvwManager;
import com.iflytek.sdk.manager.FlyTtsManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class HelpActivityNew1 extends Activity implements
        RadioGroup.OnCheckedChangeListener, ViewPagerX.OnPageChangeListener {
    private static final String TAG = HelpActivityNew1.class.getSimpleName();

    private TextView tipTxt;
    private ViewPagerX viewpager;
    private RadioGroup mRadioGroup;
    private MyAdapter adapter;
    private List<View> viewList;

    private static final int MSG_TTS_PLAY = 10001;
    private static final int OP_TIMEOUT = 10000;
    private Timer opTimer;

    private String tip = "试试屏幕上的说法";
    private String nomalTip = "您可以这样说";
    private String oneShotTip = "您可以直接说：";

    private boolean bTtsEnd = false;
    private boolean bRegister = false;
    private String helpCategory;

    private RadioButton radio;

    private final String CANCEL_ACTION = "com.iflytek.aufly.CANCEL_ACTION";

    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d("AudioFocus", "focusChage = " + focusChange);

        }
    };

    // ---------------------继承开始--------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setContentView(R.layout.activity_help);

        CTtsSession.getInstance(this).create(AudioManager.STREAM_MUSIC);

        this.tipTxt = (TextView) findViewById(R.id.tip_txt);
        this.viewpager = (ViewPagerX) findViewById(R.id.viewpager);
        this.mRadioGroup = (RadioGroup) findViewById(R.id.rdgp);

        this.viewList = new ArrayList<View>();
        viewList.add(new HelpAllView(this));
        viewList.add(new HelpPhone(this));
        viewList.add(new HelpNav(this));
        viewList.add(new HelpDod(this));
        viewList.add(new HelpAdas(this));
        viewList.add(new HelpSystem(this));
        viewList.add(new HelpMusic(this));
        viewList.add(new HelpVoice(this));
        viewList.add(new HelpQuery(this));

        this.adapter = new MyAdapter();
        this.viewpager.setAdapter(adapter);
        // 注册点击事件
        mRadioGroup.setOnCheckedChangeListener(this);
        viewpager.addOnPageChangeListener(this);
        final Intent intent = getIntent();
        helpRbManger(intent);// 帮助界面的跳转

        // // 注册接收广播
        // IntentFilter intentFilter = new IntentFilter();
        // intentFilter.addAction(CANCEL_ACTION);
        // registerReceiver(mBroadcastRecevier, intentFilter);
        // bRegister = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent()");
        helpRbManger(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        createTimer();
        setScenWakeup();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // finish();
        try {
            Thread.sleep(200);
            finish();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        // if (!bTtsEnd) {
        // SpeechServiceUtil.getInstance().stopTts();
        // }
        CTtsSession.getInstance(this).stop();
        stopTimer();
        closeScenWakeup();
        super.onStop();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        // 去注册
        if (bRegister) {
            unregisterReceiver(mBroadcastRecevier);
            bRegister = false;
        }

    }

    // ----------------继承结束----------------//

    protected void helpRbManger(Intent intent) {
        // 清楚类型记录
        helpCategory = null;
        if (null != intent) {
            if (intent.hasExtra("CATEGORY")) {
                helpCategory = intent.getStringExtra("CATEGORY");
            }
        }
        Log.d(TAG, "helpCategory = " + helpCategory);
        bTtsEnd = false;

        // if ("语音".equals(helpCategory)) {
        // mRadioGroup.check(R.id.rb_home);
        // tip = "您可以试试屏幕上的这些说法";
        // tipTxt.setText(nomalTip);
        // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
        // return;
        // }

        if ("电话".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_sort);
            // RadioButton rbtn = (RadioButton) findViewById(R.id.rb_sort);
            // rbtn.setChecked(true);
            // rbtn.requestFocus();
            // rbtn.setTextSize(26);
            radio = (RadioButton) findViewById(R.id.rb_sort);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以说：打电话给某某，也可以试试屏幕上的其他说法";
            tipTxt.setText(nomalTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            FlyTtsManager.getInstance().speak(tip, l);
            return;
        }

        if ("导航".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_brand);
            radio = (RadioButton) findViewById(R.id.rb_brand);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以说：导航到某某地，也可以试试屏幕上的其他说法";
            tipTxt.setText(nomalTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            CTtsSession.getInstance(this).startSpeak(tip, l);
            return;
        }

        if ("行车记录".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_shop_car);
            radio = (RadioButton) findViewById(R.id.rb_shop_car);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以这样说";
            tipTxt.setText(nomalTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            CTtsSession.getInstance(this).startSpeak(tip, l);
            return;
        }

        if ("ADAS".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_personal);
            radio = (RadioButton) findViewById(R.id.rb_personal);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以这样说";
            tipTxt.setText(nomalTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            CTtsSession.getInstance(this).startSpeak(tip, l);
            return;
        }

        if ("系统".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_personal1);
            radio = (RadioButton) findViewById(R.id.rb_personal1);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以这样说";
            tipTxt.setText(nomalTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            CTtsSession.getInstance(this).startSpeak(tip, l);
            return;
        }

        if ("声音".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_personaV);
            radio = (RadioButton) findViewById(R.id.rb_personaV);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以试试“音量调大一点”,也可以试试屏幕上的其他说法";
            tipTxt.setText(nomalTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            CTtsSession.getInstance(this).startSpeak(tip, l);
            return;
        }

        if ("音乐".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_persona2);
            radio = (RadioButton) findViewById(R.id.rb_persona2);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以说：听一首智者之歌，也可以试试屏幕上的其他说法";
            tipTxt.setText(nomalTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            CTtsSession.getInstance(this).startSpeak(tip, l);
            return;
        }

        if ("OneShot".equals(helpCategory)) {
            mRadioGroup.check(R.id.rb_personal3);
            radio = (RadioButton) findViewById(R.id.rb_personal3);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            tip = "您可以这样说";
            tipTxt.setText(oneShotTip);
            // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
            CTtsSession.getInstance(this).startSpeak(tip, l);
            return;
        }

        mRadioGroup.check(R.id.rb_home);
        // tip = "没听懂您的意思，您可以试试屏幕上的这些说法";
        tip = "您可以试试屏幕上的这些说法";
        radio = (RadioButton) findViewById(R.id.rb_home);
        radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        tipTxt.setText(nomalTip);
        // mHandler.sendEmptyMessageDelayed(MSG_TTS_PLAY, 500);
        CTtsSession.getInstance(this).startSpeak(tip, l);
    }

    private void createTimer() {
        if (null != opTimer) {
            opTimer.cancel();
        }
        opTimer = new Timer();
        opTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                finish();
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

    // ------------------实现的接口--------------------//
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int index = group.indexOfChild(group.findViewById(checkedId));
        int currentItem = viewpager.getCurrentItem();
        for (int i = 0; i < 8; i++) {
            radio = (RadioButton) findViewById(checkedId);
            radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
            radio.setTextColor(Color.parseColor("#59ffffff"));
        }

        RadioButton childAt2 = (RadioButton) mRadioGroup.getChildAt(index);
        childAt2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        childAt2.setTextColor(Color.parseColor("#ffffff"));

        if (index != currentItem) {
            viewpager.setCurrentItem(index);
        }

        if (checkedId == R.id.rb_personal3) {
            tipTxt.setText(oneShotTip);
        } else {
            tipTxt.setText(nomalTip);
        }
        resetTimer();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetTimer();
        RadioButton childAt = (RadioButton) mRadioGroup.getChildAt(position);
        for (int i = 0; i < 8; i++) {
            RadioButton childAtPosition = (RadioButton) mRadioGroup.getChildAt(i);
            childAtPosition.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
            childAtPosition.setTextColor(Color.parseColor("#59ffffff"));
        }

        childAt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        childAt.setTextColor(Color.parseColor("#ffffff"));

        if (childAt.getId() == R.id.rb_personal3) {
            tipTxt.setText(oneShotTip);
        }
        if (!childAt.isChecked()) {
            childAt.setChecked(true);

        }
        if (childAt.getId() == R.id.rb_personal3) {
            tipTxt.setText(oneShotTip);
        } else {
            tipTxt.setText(nomalTip);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // ----------------内部类-----------------//
    private class MyAdapter extends PagerAdapterX {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = viewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return viewList == null ? 0 : viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public BroadcastReceiver mBroadcastRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String maction = intent.getAction();
            if (CANCEL_ACTION.equals(maction)) {
                finish();
            }
        }
    };

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
        CMvwSession.getInstance(this).stop(0);
    }

    ITtsUiListener l = new ITtsUiListener() {

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

}
