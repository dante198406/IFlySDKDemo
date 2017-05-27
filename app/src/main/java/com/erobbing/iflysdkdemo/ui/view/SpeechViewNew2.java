package com.erobbing.iflysdkdemo.ui.view;

import com.erobbing.iflysdkdemo.R;
import com.erobbing.iflysdkdemo.interfaces.SpeechViewUpdateListenner;
import com.iflytek.sdk.manager.FlyHmiManager;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 模式融合后的新版语音控件
 */
public class SpeechViewNew2 extends LinearLayout implements
        SpeechViewUpdateListenner {
    private static final String TAG = "ViaFly_SpeechViewNew";

    private static final int ON_RECORD_START = 0x6001;
    private static final int ON_RECORD_END = 0x6002;
    private static final int ON_RESULT_TEXT = 0x6003;
    private static final int ON_VOLUME_CHANGED = 0x6004;
    private static final int ON_ERROR = 0x6005;
    private int currentState = 0;

    private Context mContext;
    private ImageView mRecording1;
    private ImageView mRecording2;
    private ImageView mRecording3;
    private ImageView mWaiting;
    private ImageView mBusiness;
    private TextView mSpeechState;
    private TextView mSpeechResult;
    private boolean bRecordingAnimbyVolume = false;
    private AlphaAnimation alphaAnimation1;
    private AlphaAnimation alphaAnimation2;
    private AlphaAnimation alphaAnimation3;
    private long RECORDING_ANIM_DURATION = 1000;
    private int RECORDING_ANIM_REPEATCOUNT = 30;
    private int mPrevious;
    private AnimationDrawable animationDrawable;

    public SpeechViewNew2(Context context) {
        super(context);
        Log.d(TAG, "SpeechViewNew start");
        mContext = context;
        initView();
        Log.d(TAG, "SpeechViewNew end");
    }

    /**
     * 如果从布局文件中加载就需要此构造方法
     *
     * @param context 上下文
     * @param attrs   布局文件中的参数集合
     */
    public SpeechViewNew2(Context context, AttributeSet attrs) {
        super(context, attrs); // 这一句一定不能去掉
        mContext = context;
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER); // 设置此View的对齐方式为水平方向居中，垂直方向靠下
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View root = layoutInflater
                .inflate(R.layout.main_speech_view_new2, this);
        mSpeechState = (TextView) root.findViewById(R.id.speech_state);
        mRecording1 = (ImageView) root.findViewById(R.id.recording1);
        mRecording2 = (ImageView) root.findViewById(R.id.recording2);
        mRecording3 = (ImageView) root.findViewById(R.id.recording3);
        mWaiting = (ImageView) root.findViewById(R.id.waiting);
        mBusiness = (ImageView) root.findViewById(R.id.business);
        mSpeechResult = (TextView) root.findViewById(R.id.speech_result);
        mRecording1.setVisibility(View.VISIBLE);
    }

    // 初始化完成，进入录音状态
    @Override
    public void updateUIInRecodingState() {
        currentState = ON_RECORD_START;
        if (mWaiting != null && mRecording1 != null && mSpeechState != null) {
            stopSetResult();
            stopWating();
//			mSpeechState.setText(mContext.getString(R.string.start_recording));
//			mSpeechState.setVisibility(VISIBLE);
            playRecordingAnim();
        }
    }

    /**
     * 播放录音动画
     */
    private void playRecordingAnim() {
        if (!bRecordingAnimbyVolume) {
            // AlphaAnimMethod();
            frameAnimMethod();
        }
    }

    /**
     * 录音帧播放动画
     */
    private void frameAnimMethod() {
        mRecording1.setVisibility(VISIBLE);
        mRecording1.setImageResource(R.drawable.recording_animationlist);
        animationDrawable = (AnimationDrawable) mRecording1.getDrawable();
        animationDrawable.start();
    }

    /**
     * 录音渐变动画
     */
    private void AlphaAnimMethod() {
        alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation1.setDuration(RECORDING_ANIM_DURATION);
        // alphaAnimation1.setRepeatMode(Animation.REVERSE);
        // alphaAnimation1.setRepeatCount(1);
        alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation2.setDuration(RECORDING_ANIM_DURATION);
        // alphaAnimation2.setRepeatMode(Animation.REVERSE);
        // alphaAnimation2.setRepeatCount(1);
        alphaAnimation3 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation3.setDuration(RECORDING_ANIM_DURATION);
        // alphaAnimation3.setRepeatMode(Animation.REVERSE);
        // alphaAnimation3.setRepeatCount(1);

        mRecording1.startAnimation(alphaAnimation1);
        alphaAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRecording2.startAnimation(alphaAnimation2);
                mPrevious = 1;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // mRecording2.startAnimation(alphaAnimation2);
                // mPrevious = 1;

            }
        });
        alphaAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mPrevious == 1) {
                    mRecording3.startAnimation(alphaAnimation3);
                }
                if (mPrevious == 3) {
                    mRecording1.startAnimation(alphaAnimation1);
                }
                mPrevious = 2;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // if (mPrevious == 1){
                // mRecording3.startAnimation(alphaAnimation3);
                // }
                // if (mPrevious == 3){
                // mRecording1.startAnimation(alphaAnimation1);
                // }
                // mPrevious =2;
            }
        });
        alphaAnimation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPrevious = 3;
                mRecording2.startAnimation(alphaAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // mPrevious = 3;
                // mRecording2.startAnimation(alphaAnimation2);
            }
        });
        // mRecording1.setVisibility(View.VISIBLE);
        // mRecording2.setVisibility(View.VISIBLE);
        // //mRecording3.setImageResource(R.drawable.recording_layer);
        // mRecording3.setVisibility(View.VISIBLE);
        // ScaleAnimation scaleAnimation = new
        // ScaleAnimation(0.8f,1.0f,0.8f,1.0f,
        // Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        // scaleAnimation.setDuration(400);
        // scaleAnimation.setRepeatCount(10);
        // scaleAnimation.setRepeatMode(Animation.REVERSE);
        // mRecording1.startAnimation(scaleAnimation);
        // mRecording2.startAnimation(scaleAnimation);
        // mRecording3.startAnimation(scaleAnimation);
    }

    @Override
    public void updateUIRecodingVolume(final int volume) {

        if (bRecordingAnimbyVolume) {
            startRecording(volume);
        } else {
            // 运行帧动画
            if (0 == currentState || ON_RESULT_TEXT == currentState) {
                if (null != mSpeechResult) {
                    mSpeechResult.setVisibility(INVISIBLE);
                }
                updateUIInRecodingState();
            }
        }
    }

    // 等待识别结果
    @Override
    public void updateUIInWaitingResultState() {
        if (mWaiting != null && mRecording1 != null && mSpeechState != null) {
//			mSpeechState
//					.setText(mContext.getString(R.string.start_recognition));
            startWaiting();
            stopRecordingAnim();
            stopRecording();
        }
    }

    private void stopRecordingAnim() {
        if (bRecordingAnimbyVolume) {
            stopRecording();
        } else {
            // alphaAnimation1.cancel();
            // alphaAnimation2.cancel();
            // alphaAnimation3.cancel();
            if (animationDrawable != null) {
                animationDrawable.stop();
            }
        }
    }

    @Override
    public void updateUIInCancelState() {
        // if (voiceSpeechView != null) {
        // voiceSpeechView.stop();
        // }
        // if (mMicWaiting != null) {
        // mMicWaiting.stop();
        // mMicWaiting.setVisibility(GONE);
        // mSpeechWaitingBg.setVisibility(GONE);
        // }
        //
        // mMic.setBackgroundResource(R.drawable.mic_button_bg);
        // mMic.setVisibility(VISIBLE);
        // mSpeechTip.setText(mContext.getString(R.string.end_recognition));
    }

    @Override
    public void updateUIInErrorState() {
        if (animationDrawable != null && mWaiting != null) {
            stopRecordingAnim();
            stopWating();
        }
        String tiptemp;
        tiptemp = mContext.getString(R.string.hello_assitant_tip);

        String tip = mContext.getString(R.string.start_tip) + "\"" + tiptemp
                + "\"";
        if (mSpeechState != null) {
//			mSpeechState.setText(tip);
//			mSpeechState.setVisibility(VISIBLE);
        }
    }

    @Override
    public void updateUIAfterResult(String focus, String result) {
        currentState = ON_RESULT_TEXT;
        if (animationDrawable != null && mWaiting != null
                && mRecording3 != null) {
            stopRecordingAnim();
            stopWating();
            startSetResult(focus, result);
        }
        if (!TextUtils.isEmpty(result)) {
            if (result.contains("wifi")) {
                result = result.replace("wifi", "WiFi");
            }
            if (result.length() < 10) {
                // mSpeechResult.setGravity(Gravity.CENTER);
            } else {
                if (result.length() > 20) {
                    result = result.substring(0, 19) + "...";
                }
            }
        }

        if (mSpeechResult != null) {
            mSpeechResult.setText(result);
            mSpeechResult.setVisibility(VISIBLE);

//			if (mSpeechResult.getLineCount() > 1) {
//				mSpeechResult.setGravity(Gravity.LEFT);
//			} else {
//				mSpeechResult.setGravity(Gravity.CENTER);
//			}
        }

        Log.d(TAG, "----设置识别结果");

    }

    @Override
    public void updateUIShowTip(String result) {
        // // TODO Auto-generated method stub
        // if (voiceSpeechView != null) {
        // voiceSpeechView.stop();
        // }
        // if (mMicWaiting != null) {
        // mMicWaiting.stop();
        // mMicWaiting.setVisibility(GONE);
        // mSpeechWaitingBg.setVisibility(GONE);
        // }
        // mSpeechResultView.setVisibility(GONE);
        // // mHandleTip.setText(result);
        // mMic.setVisibility(VISIBLE);
        // mSpeechResultView.setVisibility(INVISIBLE);
        // mSpeechTip.setText(mContext.getString(R.string.start_recording_tip1));
        // mMic.setBackgroundResource(R.drawable.mic_button_bg);
    }

    /**
     * 开始录音界面显示
     *
     * @param volume
     */
    private void startRecording(int volume) {
        if (volume < 150) {
            mRecording1.setVisibility(VISIBLE);
            mRecording2.setVisibility(INVISIBLE);
            mRecording3.setVisibility(INVISIBLE);
        } else if (volume < 400) {
            mRecording1.setVisibility(VISIBLE);
            mRecording2.setVisibility(VISIBLE);
            mRecording3.setVisibility(INVISIBLE);
        } else {
            mRecording1.setVisibility(VISIBLE);
            mRecording2.setVisibility(VISIBLE);
            mRecording3.setVisibility(VISIBLE);
        }
    }

    private void stopRecording() {
        mRecording1.setVisibility(INVISIBLE);
        mRecording2.setVisibility(INVISIBLE);
        mRecording3.setVisibility(INVISIBLE);
    }

    private void startWaiting() {
        Animation rotateAnim = AnimationUtils.loadAnimation(mContext,
                R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        mWaiting.setVisibility(VISIBLE);
        mWaiting.startAnimation(rotateAnim);
    }

    private void stopWating() {
        mWaiting.clearAnimation();
        mWaiting.setVisibility(INVISIBLE);
    }

    private void startSetResult(String focus, String result) {
        mRecording1.setVisibility(INVISIBLE);
        mRecording2.setVisibility(INVISIBLE);
        mWaiting.setVisibility(INVISIBLE);
        mRecording3.setVisibility(VISIBLE);
        mBusiness.setVisibility(VISIBLE);

        if ("app".equals(focus)) {
            if ("打开行车记录".equals(result) || "关闭行车记录".equals(result)) {
                mBusiness.setImageResource(R.drawable.carcorder);
            } else if ("打开WIFI".equals(result) || "关闭WIFI".equals(result)
                    || "打开网络".equals(result) || "关闭网络".equals(result)) {
                mBusiness.setImageResource(R.drawable.wifi);
            } else if ("打开音乐".equals(result) || "关闭音乐".equals(result)) {
                mBusiness.setImageResource(R.drawable.music);
            } else if ("打开设置".equals(result) || "关闭设置".equals(result)) {
                mBusiness.setImageResource(R.drawable.set);
            } else if ("打开ADAS".equals(result) || "关闭ADAS".equals(result)) {
                mBusiness.setImageResource(R.drawable.adas);
            } else if ("打开导航".equals(result) || "关闭导航".equals(result)) {
                mBusiness.setImageResource(R.drawable.navi);
            } else if ("打开地图".equals(result) || "关闭地图".equals(result)) {
                mBusiness.setImageResource(R.drawable.navi);
            } else if ("打开天气".equals(result) || "关闭天气".equals(result)) {
                mBusiness.setImageResource(R.drawable.weather);
            } else if ("打开网络电台".equals(result) || "关闭网络电台".equals(result)) {
                mBusiness.setImageResource(R.drawable.fm);
            } else {
                mBusiness.setImageResource(R.drawable.set);
            }

        } else if ("lbs".equals(focus)) {
            mBusiness.setImageResource(R.drawable.navi);
        } else if ("map".equals(focus)) {
            mBusiness.setImageResource(R.drawable.navi);
        } else if ("telephone".equals(focus)) {
            mBusiness.setImageResource(R.drawable.telphone);
        } else if ("music".equals(focus)) {
            mBusiness.setImageResource(R.drawable.music);
        } else if ("train".equals(focus)) {
            mBusiness.setImageResource(R.drawable.train);
        } else if ("flight".equals(focus)) {
            mBusiness.setImageResource(R.drawable.flight);
        } else if ("hotel".equals(focus)) {
            mBusiness.setImageResource(R.drawable.hotel);
        } else if ("restaurant".equals(focus)) {
            mBusiness.setImageResource(R.drawable.food);
        } else if ("radio".equals(focus) || "netRadio".equals(focus)) {
            mBusiness.setImageResource(R.drawable.fm);
        } else if ("weather".equals(focus)) {
            mBusiness.setImageResource(R.drawable.weather);
        } else if ("help".equals(focus)) {
            mBusiness.setImageResource(R.drawable.set);
        } else if ("cmd".equals(focus)) {
            mBusiness.setImageResource(R.drawable.set);
        } else if ("pattern".equals(focus)) {
            mBusiness.setImageResource(R.drawable.more);
        } else if ("news".equals(focus)) {
            mBusiness.setImageResource(R.drawable.fm);
        } else if ("stock".equals(focus)) {
            mBusiness.setImageResource(R.drawable.stock);
        } else {
            mBusiness.setImageResource(R.drawable.noresult);
        }
        Log.d(TAG, "startSetResult --> Success !!!");
    }

    private void stopSetResult() {
        mRecording3.setVisibility(INVISIBLE);
        mBusiness.setVisibility(INVISIBLE);
    }

    /**
     * 关闭mic 效果
     */
    @Override
    public void destroyView() {
        mSpeechResult = null;
        mSpeechState = null;
        mRecording1 = null;
        mRecording2 = null;
        mRecording3 = null;
        mBusiness = null;
        mWaiting = null;
    }

    public void onClickEvent(View v) {
        if (currentState == ON_RECORD_START || currentState == ON_ERROR) {
            FlyHmiManager.getInstance().speakEnd();
        } else {
            FlyHmiManager.getInstance().startInteraction();
        }
    }
}
