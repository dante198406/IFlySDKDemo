package com.erobbing.iflysdkdemo.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.erobbing.iflysdkdemo.Constant;
import com.erobbing.iflysdkdemo.R;
import com.erobbing.iflysdkdemo.SpeechDemoApplication;
import com.erobbing.iflysdkdemo.interfaces.ControlActionListener;
import com.erobbing.iflysdkdemo.ui.view.UIControl;
import com.iflytek.clientadapter.aidl.ActionModel;
import com.iflytek.clientadapter.aidl.ContactEntity;
import com.iflytek.clientadapter.constant.FocusType;
import com.iflytek.sdk.manager.FlyPhoneManager;

/**
 * 电话选择器界面
 *
 * @param <T>
 * @author zhmao2
 */
public class TelephoneActivity extends FragmentActivity implements
        ControlActionListener {
    private static final String TAG = TelephoneActivity.class.getSimpleName();
    private List<ContactEntity> items;
    private static int itemSize = 0;// POI结果数目
    public int currentPage = 1; // 当前页
    public int totalPage = 1;// 总页数
    private static final int OP_TIMEOUT = 5000;

    private ViewPager mViewPage;
    private TextView mTip;
    private TextView mNoContact;
    private ImageView mCircle;
    private ImageView mCircleBar;
    private Timer opTimer;

    private List<TelephoneItemFragment> framents = new ArrayList<TelephoneItemFragment>();

    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d("AudioFocus", "focusChage = " + focusChange);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        setContentView(R.layout.activity_telephone);
        SpeechDemoApplication.addActivity(this);
        UIControl.getInstance().setListenner(this);
        items = getIntent().getParcelableArrayListExtra(
                "com.erobbing.iflysdkdemo.list");

        initView();
        if (items.size() == 0) {
            initEmptyView();
        } else {
            initElse();
            initData();
        }
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

    private void initEmptyView() {
        mNoContact.setVisibility(View.VISIBLE);
        createTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SpeechDemoApplication.removeALLActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCircle.clearAnimation();
        mViewPage.removeAllViews();
        mTip = null;
        mCircle = null;
        mViewPage = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "点击BACK键");

            // 取消交互,销毁界面
            FlyPhoneManager.getInstance().cancelInteractUI();
            finish();
        }
        return true;
    }

    private void initView() {
        mViewPage = (ViewPager) findViewById(R.id.viewpager);
        mCircle = (ImageView) findViewById(R.id.circle);
        mCircleBar = (ImageView) findViewById(R.id.circle_bar);
        mTip = (TextView) findViewById(R.id.circle_bar_text);
        mNoContact = (TextView) findViewById(R.id.no_contact);
    }

    private void initElse() {
        itemSize = items.size();
        totalPage = itemSize % Constant.NUM_PER_PAGE == 0 ? itemSize
                / Constant.NUM_PER_PAGE : itemSize / Constant.NUM_PER_PAGE + 1;
        totalPage = totalPage == 0 ? 1 : totalPage;
        Log.d(TAG, "联系人---->initElse():totalPage" + totalPage);

        Animation rotateAnim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_slow);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        mCircle.startAnimation(rotateAnim);
        mCircleBar.setVisibility(View.VISIBLE);
        mTip.setVisibility(View.VISIBLE);
        if (items.size() == 1) {
            mTip.setText(items.get(0).getName() + " (请说“确定”或“取消”)");
        } else {
            mTip.setText(items.get(0).getName() + " (请说“第几个”、“翻页”或“取消”)");
        }
    }

    private void initData() {
        Log.d(TAG, "---->initData()");
        // // 头部提示语
        // mMainTitle.setText(Constant.TELEPHONE_MAIN_TITLE);
        // mPageNumTxt.setText(String.format("%d/%d", currentPage, totalPage));
        // // 设置选择器底部提示语
        // if (itemSize == 1) {
        // mTipTxt.setText(Constant.TIP_SINGAL_ITEM);
        // } else if (totalPage == 1) {
        // mTipTxt.setText(Constant.TIP_MULTI_PAGER);
        // } else {
        // mTipTxt.setText(Constant.TIP_MULTI_PAGER);
        // }

        // 导航的ViewPager相关
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        mViewPage.setAdapter(adapter);
        // mPageNumTxt.setText(String.format("%d/%d", currentPage, totalPage));
        mViewPage.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                ActionModel action = new ActionModel();
                action.setFocus(FocusType.telephone);
                if (currentPage == (arg0 + 1) + 1) {
                    action.setAction(ActionModel.ACTION_PREPAGE);
                } else if (currentPage == (arg0 + 1) - 1) {
                    action.setAction(ActionModel.ACTION_NEXTPAGE);
                }
                FlyPhoneManager.getInstance().doAction(action);

                currentPage = arg0 + 1;
                // mPageNumTxt.setText(String.format("%d/%d", arg0 + 1,
                // totalPage));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
            framents.clear();
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem(int position)");
            int start;
            int end;
            start = position * Constant.NUM_PER_PAGE;
            end = start + Constant.NUM_PER_PAGE;
            end = end <= itemSize ? end : itemSize;
            List<ContactEntity> suBeans = items.subList(start, end);
            TelephoneItemFragment f = new TelephoneItemFragment(suBeans);
            framents.add(f);
            return f;
        }

        @Override
        public int getCount() {
            int tmp;
            int count;
            tmp = itemSize % Constant.NUM_PER_PAGE;
            count = itemSize / Constant.NUM_PER_PAGE;
            return tmp == 0 ? count : count + 1;
        }

    }

    @Override
    public void nextPage() {
        // TODO Auto-generated method stub
        if (currentPage == totalPage) {
            return;
        }
        mViewPage.arrowScroll(View.FOCUS_RIGHT);
    }

    @Override
    public void prePage() {
        // TODO Auto-generated method stub
        if (currentPage == 1) {
            return;
        }
        mViewPage.arrowScroll(View.FOCUS_LEFT);
    }

    @Override
    public void selectItem(int index) {
        // TODO Auto-generated method stub
        int page = currentPage - 1;
        if (page < framents.size()) {
            TelephoneItemFragment f = framents.get(page);
            f.selectItem(index);
        } else {
            Log.e(TAG, "selectItem() page >+ framents.size()");
        }

    }
}
