package com.erobbing.iflysdkdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.erobbing.iflysdkdemo.ui.NaviActivity;
import com.erobbing.iflysdkdemo.ui.view.UIControl;
import com.iflytek.clientadapter.aidl.ActionModel;
import com.iflytek.clientadapter.aidl.ContactEntity;
import com.iflytek.clientadapter.aidl.PoiInfo;
import com.iflytek.clientadapter.tts.CTtsSession;
import com.iflytek.sdk.interfaces.IInitListener;
import com.iflytek.sdk.interfaces.INaviListener;
import com.iflytek.sdk.interfaces.IPhoneListener;
import com.iflytek.sdk.interfaces.IWakupListener;
import com.iflytek.sdk.manager.FlyHmiManager;
import com.iflytek.sdk.manager.FlyNaviManager;
import com.iflytek.sdk.manager.FlyPhoneManager;
import com.iflytek.sdk.manager.FlySDKManager;
import com.iflytek.utils.log.FLog;

public class SpeechDemoApplication extends Application {

    private static String TAG = "DemoApp";
    private static Context context;
    private static List<Activity> mList;// 用于存放所有启动的Activity的集合

    private static SpeechDemoApplication mSelf;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    // ///////////////////////////////////////////////////////////////////////////////////////
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

    // 选中消息框
    public final static int MSG_SELECT_ITEM = 10096;

    public static NaviActivity naviActivity;

    // ///////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        context = getApplicationContext();
        mList = new ArrayList<Activity>();
        creatShortCut();
        CTtsSession.getInstance(context);
        IntentFilter filter = new IntentFilter(Constant.INCOMING_CALL);
        registerReceiver(mBroadcastReceiver, filter);

        FlySDKManager.getInstance().init(context, new IInitListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess() :初始化成功 !!!");
                FlyPhoneManager.getInstance().setBookList(getBookList());
                removeALLActivity();
                startSpeechUI();
            }

            @Override
            public void onError(int errorid, String errortips) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onError() errorid = " + errorid + " | errortips = "
                        + errortips);
            }

        }, new IWakupListener() {

            @Override
            public void onOneshotWakeup(String result, int score) {
                // TODO Auto-generated method stub
                Intent intent = new Intent("com.iflytek.autofly.POP_SPEECH");
                intent.setPackage("com.erobbing.iflysdkdemo");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("com.iflytek.autofly.speechservice.operation",
                        "INTERACTION");
                getContext().startService(intent);
            }

            @Override
            public void onMainUIWakeup(String result, int score) {
                // TODO Auto-generated method stub
                if (mList != null) {
                    removeALLActivity();
                }
                startSpeechUI();
                Log.d(TAG, "onHmiWakeup result = " + result + " | score = "
                        + score);
            }

            @Override
            public void onGlobalWakeup(String result, int score) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onGlobleWakeup() result = " + result
                        + " | score = " + score);
            }

        }, null);

        FlyHmiManager.getInstance();

        FlyPhoneManager.getInstance().setListener(new IPhoneListener() {

            @Override
            public void onShowList(List<ContactEntity> list) {
                FLog.d(TAG, "onShowList(List<ContactEntity> list)" + list);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("com.erobbing.iflysdkdemo.list",
                        (ArrayList<? extends Parcelable>) list);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName("com.erobbing.iflysdkdemo",
                        "com.erobbing.iflysdkdemo.ui.TelephoneActivity"));
                startActivity(intent);
            }

            @Override
            public void onSelectPage(int page) {
                FLog.d(TAG, "onSelectPage(int page)" + page);

            }

            @Override
            public void toCallPhone(String number) {
                FLog.d(TAG, "toCallPhone(String number)" + number);
                Message msg = new Message();
                msg.what = MSG_MAKE_CALL;
                msg.obj = number;
                mHandler.sendMessage(msg);
                SpeechDemoApplication.removeALLActivity();
            }

            @Override
            public void toAcceptCall(String number) {
                FLog.d(TAG, "toAcceptCall(String number)" + number);
                Message msg = new Message();
                msg.what = MSG_LISTEN_CALL;
                msg.obj = number;
                mHandler.sendMessage(msg);
                SpeechDemoApplication.removeALLActivity();

            }

            @Override
            public void toRejectCall(String number) {
                FLog.d(TAG, "toRejectCall(String number)" + number);
                Message msg = new Message();
                msg.what = MSG_HANG_CALL;
                msg.obj = number;
                mHandler.sendMessage(msg);
                SpeechDemoApplication.removeALLActivity();
            }

            @Override
            public boolean onDoActioin(ActionModel action) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onDoActioin : " + action.getAction());
                if (ActionModel.ACTION_NEXTPAGE == action.getAction()) {
                    mHandler.sendEmptyMessage(MSG_NEXT_PAGE);
                } else if (ActionModel.ACTION_PREPAGE == action.getAction()) {
                    mHandler.sendEmptyMessage(MSG_PRE_PAGE);
                } else if (ActionModel.ACTION_CANCLE == action.getAction()) {
                    mHandler.sendEmptyMessage(MSG_CANCLE);
                }
                return false;
            }

            @Override
            public void onSelectItem(int index) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onSelectItem : " + index);
                Message msg = new Message();
                msg.what = MSG_SELECT_ITEM;
                msg.arg1 = index;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onBookResult(int result) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onBookResult() result = " + result);
            }
        });

        FlyNaviManager.getInstance().setListener(new INaviListener() {

            @Override
            public void onShowPoi(List<PoiInfo> endInfo) {
                Log.d(TAG, "endInfo::" + endInfo);
                Intent intent = new Intent();
                intent.setClass(getContext(), NaviActivity.class);
                intent.putExtra("hasTwice", false);
                intent.putParcelableArrayListExtra("endInfo",
                        (ArrayList<? extends Parcelable>) endInfo);
                intent.setPackage("com.erobbing.iflysdkdemo");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onPoiChoice(int Index) {
                Log.d(TAG, "onPoiChoice：" + Index);
                Message msg = Message.obtain();
                msg.what = MSG_NAVI_SELECTED;
                msg.arg1 = Index;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onClosePage(boolean isClose) {
                // TODO Auto-generated method stub
                Message msg = Message.obtain();
                msg.what = MSG_NAVI_CLOSEPAGE;
                msg.obj = isClose;
                mHandler.sendMessage(msg);
            }

            @Override
            public boolean onDoAction(ActionModel action) {
                int actionInt = action.getAction();
                String focus = action.getFocus();
                if ("map".equals(focus)) {
                    Message msg = new Message();
                    switch (actionInt) {
                        case ActionModel.ACTION_CANCLE:
                            // 取消
                            mHandler.sendEmptyMessage(MSG_CANCLE);
                            break;
                        case ActionModel.ACTION_NEXTPAGE:
                            // 下一页
                            mHandler.sendEmptyMessage(MSG_NEXT_PAGE);
                            break;
                        case ActionModel.ACTION_PREPAGE:
                            // 上一页
                            mHandler.sendEmptyMessage(MSG_PRE_PAGE);
                            break;
                    }
                }
                return false;
            }

            @Override
            public void onShowPoiTwice(List<PoiInfo> startInfo,
                                       List<PoiInfo> endInfo) {
                Log.d(TAG, "startInfo:" + startInfo.get(0).getPoiName());
                Log.d(TAG, "endInfo:" + endInfo.get(0).getPoiName());
                Intent intent = new Intent();
                intent.setClass(getContext(), NaviActivity.class);
                intent.putExtra("hasTwice", true);
                intent.setPackage("com.erobbing.iflysdkdemo");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putParcelableArrayListExtra("endInfo",
                        (ArrayList<? extends Parcelable>) endInfo);
                intent.putParcelableArrayListExtra("startInfo",
                        (ArrayList<? extends Parcelable>) startInfo);
                startActivity(intent);
            }

        });
        mSelf = this;
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }

    Handler mHandler = new Handler() { // 去除HandlerLeak警告

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Log.d(TAG, "msg.what=" + msg.what);
            switch (msg.what) {
                case MSG_SELECT_ITEM:
                    UIControl.getInstance().getTelListener().selectItem(msg.arg1);
                    break;
                case MSG_NAVI_LOADING:
                    if (naviActivity != null) {
                        naviActivity.changeLoading();
                    }
                    break;
                case MSG_NAVI_CHANGEDATA:
                    if (naviActivity != null) {
                        boolean hasTwice = (msg.arg1 == 1);
                        // naviActivity.changeSelect((String) msg.obj, hasTwice);
                    }
                    break;
                case MSG_RESTART_INTERACTION:
                    FlyHmiManager.getInstance().startInteraction();
                    break;
                case MSG_NAVI_SELECTED:
                    // 此时回调到NaviActivity由NaviActivity判断该做山么事
                    if (naviActivity != null) {
                        naviActivity.selectedByMwv(msg.arg1);
                    }
                    break;
                case MSG_NAVI_CLOSEPAGE:
                    // 此时回调到NaviActivity由NaviActivity判断该做山么事
                    if (naviActivity != null) {
                        naviActivity.finish();
                    }
                    break;
                case MSG_NEXT_PAGE:
                    UIControl.getInstance().getTelListener().nextPage();
                    break;
                case MSG_PRE_PAGE:
                    UIControl.getInstance().getTelListener().prePage();
                    break;
                case MSG_CANCLE:
                    // if (naviActivity != null) {
                    // naviActivity.finish();
                    // }
                    SpeechDemoApplication.removeALLActivity();
                    Log.d(TAG, "执行取消指令");
                    break;
                case MSG_MAKE_CALL:
                    Toast.makeText(getContext(), "打电话给: " + msg.obj,
                            Toast.LENGTH_LONG).show();
                    break;
                case MSG_HANG_CALL:
                    Toast.makeText(getContext(), "已挂断电话: " + msg.obj,
                            Toast.LENGTH_LONG).show();
                    break;
                case MSG_LISTEN_CALL:
                    Toast.makeText(getContext(), "已接听电话: " + msg.obj,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent.getAction()
                    && intent.getAction().equals(Constant.INCOMING_CALL)) {
                FlyPhoneManager.getInstance().inCommingCall("1234567");
                FlyPhoneManager.getInstance().setCallState(0);
            }
        }
    };

    private void creatShortCut() {
        Log.d(TAG, "创建快捷方式");
        Intent intent = new Intent("com.erobbing.iflysdkdemo.action.SHORTCUT");
        Intent shortcutintent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "麦克风");
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
                R.mipmap.ic_launcher);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        sendBroadcast(shortcutintent);
    }

    private void startSpeechUI() {
        // 关闭弹出框
        Intent intent = new Intent("com.iflytek.autofly.POP_SPEECH");
        intent.setPackage("com.erobbing.iflysdkdemo");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.iflytek.autofly.speechservice.operation", "EXIT");
        startService(intent);

        Intent openIntent = new Intent();
        openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openIntent.setComponent(new ComponentName("com.erobbing.iflysdkdemo",
                "com.erobbing.iflysdkdemo.SpeechActivity"));
        startActivity(openIntent);
    }

    /**
     * 添加Activity
     */
    public static void addActivity(Activity activity) {
        // 判断当前集合中不存在该Activity
        if (!mList.contains(activity)) {
            mList.add(activity);// 把当前Activity添加到集合中
        }
    }

    public static void removeALLActivity() {
        if (mList != null) {
            for (Activity activity : mList) {
                activity.finish();
            }
        }
    }

    public static Context getContext() {
        return context;
    }

    public static SpeechDemoApplication getApp() {
        return mSelf;
    }

    public WindowManager.LayoutParams getWmParams() {
        return wmParams;
    }

    private List<ContactEntity> getBookList() {

        List<ContactEntity> list = new ArrayList<ContactEntity>();
        ContactEntity c1 = new ContactEntity();
        ContactEntity c2 = new ContactEntity();
        ContactEntity c3 = new ContactEntity();
        ContactEntity c4 = new ContactEntity();
        ContactEntity c5 = new ContactEntity();
        ContactEntity c6 = new ContactEntity();

        ContactEntity c7 = new ContactEntity();
        ContactEntity c8 = new ContactEntity();
        ContactEntity c9 = new ContactEntity();
        ContactEntity c10 = new ContactEntity();
        ContactEntity c11 = new ContactEntity();
        ContactEntity c12 = new ContactEntity();

        ContactEntity c13 = new ContactEntity();
        ContactEntity c14 = new ContactEntity();
        ContactEntity c15 = new ContactEntity();
        ContactEntity c16 = new ContactEntity();
        ContactEntity c17 = new ContactEntity();
        ContactEntity c18 = new ContactEntity();

        ContactEntity c19 = new ContactEntity();
        ContactEntity c20 = new ContactEntity();
        ContactEntity c21 = new ContactEntity();
        ContactEntity c22 = new ContactEntity();
        ContactEntity c23 = new ContactEntity();
        ContactEntity c24 = new ContactEntity();
        ContactEntity c25 = new ContactEntity();
        ContactEntity c26 = new ContactEntity();
        ContactEntity c27 = new ContactEntity();
        ContactEntity c28 = new ContactEntity();
        ContactEntity c29 = new ContactEntity();
        ContactEntity c30 = new ContactEntity();
        ContactEntity c31 = new ContactEntity();
        ContactEntity c32 = new ContactEntity();
        ContactEntity c33 = new ContactEntity();
        ContactEntity c34 = new ContactEntity();
        ContactEntity c35 = new ContactEntity();
        ContactEntity c36 = new ContactEntity();

        c1.setName("张凯");
        c2.setName("张凯");
        c3.setName("张凯1");
        c4.setName("张凯2");
        c5.setName("张凯3");
        c6.setName("张凯4");

        c7.setName("毛志豪");
        c8.setName("杨骥");
        c9.setName("周浩");
        c10.setName("刘艳芳");
        c11.setName("耿明哲");
        c12.setName("耿明者");

        c13.setName("毛志豪");
        c14.setName("杨骥1");
        c15.setName("唐小伟");
        c16.setName("华为科技");
        c17.setName("计算器");
        c18.setName("季兴安");
        c19.setName("耿明哲");
        c20.setName("耿明哲");
        c21.setName("毛志豪2");
        c22.setName("毛志豪3");
        c23.setName("毛志豪4");
        c24.setName("毛志豪1");
        c25.setName("毛志豪5");
        c26.setName("毛志豪6");
        c27.setName("毛志豪7");
        c28.setName("毛志豪8");
        c29.setName("毛志豪9");
        c30.setName("孙杨");
        c31.setName("孙阳");
        c32.setName("孙扬");
        c33.setName("孙漾");
        c34.setName("倾城");
        c35.setName("倾城");
        c36.setName("中国移动黄工");

        c1.setNumber("18733387609");
        c2.setNumber("18298787609");
        c3.setNumber("18385687609");
        c4.setNumber("18364812609");
        c5.setNumber("18730926209");
        c6.setNumber("18731093709");

        c1.setNumber("15700387609");
        c2.setNumber("18255198303");
        c3.setNumber("15536876093");
        c4.setNumber("18581260339");
        c5.setNumber("17730926209");
        c6.setNumber("13831090078");

        c7.setNumber("13403876094");
        c8.setNumber("18155198336");
        c9.setNumber("15535756093");
        c10.setNumber("18368870339");
        c11.setNumber("17334445559");
        c12.setNumber("13831022348");

        c13.setNumber("18605510670");
        c14.setNumber("18155198336");
        c15.setNumber("15535756093");
        c16.setNumber("18368870339");
        c17.setNumber("17334445559");
        c18.setNumber("13831022348");
        c19.setNumber("17334445559");
        c20.setNumber("13831022348");
        c21.setNumber("17436445559");
        c22.setNumber("17213445559");
        c23.setNumber("12321321321");
        c24.setNumber("5768733423");
        c25.setNumber("987421236");
        c26.setNumber("789634534324");
        c27.setNumber("177658722329");
        c28.setNumber("177658722329");
        c29.setNumber("177658722329");
        c30.setNumber("177658722329");
        c31.setNumber("177658722330");
        c32.setNumber("177658722331");
        c33.setNumber("177658722332");
        c34.setNumber("15056770643");
        c35.setNumber("15855153592");
        c36.setNumber("15855153593");

        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        list.add(c5);
        list.add(c6);

        list.add(c7);
        list.add(c8);
        list.add(c9);
        list.add(c10);
        list.add(c11);
        list.add(c12);

        list.add(c13);
        list.add(c14);
        list.add(c15);
        list.add(c16);
        list.add(c17);
        list.add(c18);
        list.add(c19);
        list.add(c20);
        list.add(c21);
        list.add(c22);
        list.add(c23);
        list.add(c24);
        list.add(c25);
        list.add(c26);
        list.add(c27);
        list.add(c28);
        list.add(c29);
        list.add(c30);
        list.add(c31);
        list.add(c32);
        list.add(c33);
        list.add(c34);
        list.add(c35);
        list.add(c36);

        Log.d(TAG, "电话本上传完成");
        return list;

    }
}
