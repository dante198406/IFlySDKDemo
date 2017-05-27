package com.erobbing.iflysdkdemo.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
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
import com.iflytek.clientadapter.aidl.PoiInfo;
import com.iflytek.clientadapter.constant.FocusType;
import com.iflytek.sdk.manager.FlyNaviManager;
import com.iflytek.sdk.manager.FlyPhoneManager;

public class NaviActivity extends FragmentActivity implements
        ControlActionListener {

    private static final String TAG = NaviActivity.class.getSimpleName();
    private List<PoiInfo> items;
    private List<PoiInfo> endPoi;
    private static int itemSize = 0;// POI结果数目
    public int currentPage = 1; // 当前页
    public int totalPage = 1;// 总页数
    private ViewPager mViewPage;
    private ViewPager mViewPager_end;
    private TextView mTip;
    private TextView mNoContact;
    private ImageView mCircle;
    private ImageView mCircleBar;
    private boolean hasTwice;
    // 记录当前是第一次还是第二次（theTime=2时代表此次显示的是终点位置 两点导航）
    private int theTime = 1;

    private List<NaviItemFragment> framents = new ArrayList<NaviItemFragment>();

    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d("AudioFocus", "focusChage = " + focusChange);

        }
    };

    // private
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, "NaviActivity onCreate ");

        setContentView(R.layout.activity_telephone);
        initView();
        SpeechDemoApplication.naviActivity = this;
        SpeechDemoApplication.addActivity(this);
        UIControl.getInstance().setListenner(this);
        Intent intent = getIntent();
        if (intent != null) {
            hasTwice = intent.getBooleanExtra("hasTwice", false);
            if (hasTwice) {
                items = intent.getParcelableArrayListExtra("startInfo");
                endPoi = intent.getParcelableArrayListExtra("endInfo");
            } else {
                items = intent.getParcelableArrayListExtra("endInfo");
            }
        }
        // Log.d(TAG, items.get(0).getPoiName());
        // Log.d(TAG, endPoi.get(0).getPoiName());
        if (items != null && items.size() != 0) {
            initElse();
            initData(mViewPager_end);
        } else {
            Log.d(TAG, "没有检索到结果");
            mNoContact.setText("没有搜索到结果");
            initEmptyView();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Log.d(TAG, " NaviActivity--->onNewIntent");
        // initView();
        SpeechDemoApplication.naviActivity = this;
        SpeechDemoApplication.addActivity(this);
        UIControl.getInstance().setListenner(this);
        // Intent intent = getIntent();
        if (intent != null) {
            hasTwice = intent.getBooleanExtra("hasTwice", false);
            if (hasTwice) {
                items = intent.getParcelableArrayListExtra("startInfo");
                endPoi = intent.getParcelableArrayListExtra("endInfo");
            } else {
                items = intent.getParcelableArrayListExtra("endInfo");
            }
        }
        if (items != null && items.size() != 0) {
            initElse();
            initData(mViewPager_end);
        } else {
            Log.d(TAG, "没有检索到结果");
            mNoContact.setText("没有搜索到结果");
            initEmptyView();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "NaviActivity onResume ");
        mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void changeLoadingText() {
        mNoContact.setText("正在加载中...");
    }

    public void initEmptyView() {
        mNoContact.setVisibility(View.VISIBLE);
        mCircleBar.setVisibility(View.INVISIBLE);
        mTip.setVisibility(View.INVISIBLE);
        mViewPage.setVisibility(View.INVISIBLE);
        mCircle.setVisibility(View.INVISIBLE);
        mCircle.clearAnimation();
    }

    private void initData(ViewPager mViewPager) {
        mNoContact.setVisibility(View.INVISIBLE);
        mCircleBar.setVisibility(View.VISIBLE);
        mViewPage.setVisibility(View.VISIBLE);
        mTip.setVisibility(View.VISIBLE);
        if (items.size() == 1) {
            mTip.setText("说  \"确定\" 或  \"取消\"");
        } else {
            mTip.setText("说  \"第几个\" , \"翻页\" 或 \"取消\"");
        }
        // 导航的ViewPager相关
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager(), items);
        mViewPage.setAdapter(adapter);
        // mPageNumTxt.setText(String.format("%d/%d", currentPage, totalPage));
        mViewPage.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {

                ActionModel action = new ActionModel();
                action.setFocus(FocusType.map);
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
        currentPage = 1;
    }

    private void initElse() {
        itemSize = items.size();
        totalPage = itemSize % Constant.NUM_PER_PAGE == 0 ? itemSize
                / Constant.NUM_PER_PAGE : itemSize / Constant.NUM_PER_PAGE + 1;
        totalPage = totalPage == 0 ? 1 : totalPage;
        Log.d(TAG, "---->initElse():" + items);

        Animation rotateAnim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_slow);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        mCircle.startAnimation(rotateAnim);
    }

    private void initView() {
        mViewPager_end = (ViewPager) findViewById(R.id.viewpager_end);
        mViewPage = (ViewPager) findViewById(R.id.viewpager);
        mCircle = (ImageView) findViewById(R.id.circle);
        mCircleBar = (ImageView) findViewById(R.id.circle_bar);
        mTip = (TextView) findViewById(R.id.circle_bar_text);
        mTip.setText("");
        mNoContact = (TextView) findViewById(R.id.no_contact);
        mNoContact.setText("没有搜索到结果，请重试");
    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        private List<PoiInfo> items;
        private int itemSize = 0;

        public MyAdapter(FragmentManager fm, List<PoiInfo> items) {
            super(fm);
            Log.d(TAG, "MyAdapter");
            this.items = items;
            framents.clear();
            this.itemSize = items.size();
        }

        @Override
        public Fragment getItem(int position) {
            int start;
            int end;
            start = position * Constant.NUM_PER_PAGE;
            end = start + Constant.NUM_PER_PAGE;
            end = end <= itemSize ? end : itemSize;
            List<PoiInfo> suBeans = items.subList(start, end);
            NaviItemFragment f = new NaviItemFragment(suBeans, hasTwice,
                    theTime);
            framents.add(f);
            return f;
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount()");
            int tmp;
            int count;
            tmp = itemSize % Constant.NUM_PER_PAGE;
            count = itemSize / Constant.NUM_PER_PAGE;
            return tmp == 0 ? count : count + 1;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "NaviActivity onPause ");

        // DemoApp.removeALLActivity();
    }

    @Override
    public void nextPage() {
        mViewPage.arrowScroll(View.FOCUS_RIGHT);
    }

    @Override
    public void prePage() {
        mViewPage.arrowScroll(View.FOCUS_LEFT);
    }

    /**
     * 根据传递的数据修改选择页面
     */
    public void changeSelect() {
        items = endPoi;
        theTime++;
        Log.d(TAG, "changeSelect:" + items.get(0).getPoiName());
        if (items != null && items.size() != 0) {
            // initView();
            initElse();
            mViewPage.setVisibility(View.GONE);
            initData(mViewPager_end);
        } else {
            Log.d(TAG, "没有检索到结果");
            Log.d(TAG, "没有检索到结果");
            mNoContact.setText("没有搜索到结果，请重试");
            initEmptyView();
        }
    }

    /**
     * 正在加载界面
     */
    public void changeLoading() {
        Log.d(TAG, "修改界面");
        mNoContact.setText("正在加载中...");
        initEmptyView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }

    @Override
    protected void onDestroy() {
        SpeechDemoApplication.naviActivity = null;
        super.onDestroy();
        FlyNaviManager.getInstance().cancelInteractUI();
    }

    public void selectedByMwv(int index) {
        // TODO Auto-generated method stub
        Log.d(TAG, "selectedByMwv:index->" + index);
        selectItem(index);
        if (hasTwice && theTime == 1) {
            theTime++;
            changeSelect();
        }
    }

    @Override
    public void selectItem(int index) {
        // TODO Auto-generated method stub
        int page = currentPage - 1;
        if (page < framents.size()) {
            NaviItemFragment f = framents.get(page);
            f.selectItem(index);
        } else {
            Log.e(TAG, "selectItem() page >+ framents.size()");
        }
    }
}
