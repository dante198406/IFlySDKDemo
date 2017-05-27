package com.erobbing.iflysdkdemo.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erobbing.iflysdkdemo.Constant;
import com.erobbing.iflysdkdemo.R;
import com.iflytek.clientadapter.aidl.PoiInfo;
import com.iflytek.sdk.manager.FlyNaviManager;

public class NaviItemFragment extends Fragment {
    private static final String TAG = "NaviItemFragment";
    private LayoutInflater mInflater;
    private List<PoiInfo> poiBeans;
    private int size = 0;
    private NaviActivity naviActivity;
    private boolean hasTwice;
    // 记录当前是第一次还是第二次（theTime=2时代表此次显示的是终点位置 两点导航）
    private int theTime = 1;
    private int count = 0;

    /**
     * 是否手动点击
     */
    private boolean isMunaul = true;

    /**
     * 构造并传数据
     *
     * @param poiBeans
     * @param isLocal
     */
    public NaviItemFragment(List<PoiInfo> poiBeans, boolean hasTwice,
                            int theTime) {
        this.poiBeans = poiBeans;
        this.size = this.poiBeans.size();
        this.hasTwice = hasTwice;
        this.theTime = theTime;
        if (poiBeans != null) {
            count = poiBeans.size();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.item_fragment, container, false);
        this.mInflater = inflater;
        initView(rootView);
        return rootView;
    }

    public void initView(ViewGroup rootView) {
        for (int i = 0; i < Constant.NUM_PER_PAGE; i++) {
            RelativeLayout itemView = (RelativeLayout) mInflater.inflate(
                    R.layout.item_navi, null);
            LayoutParams param = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
            itemView.setLayoutParams(param);
            if (i >= size) {
                itemView.findViewById(R.id.navi_all).setVisibility(
                        View.INVISIBLE);
                rootView.addView(itemView);
                continue;
            }
            TextView indexTxt = (TextView) itemView
                    .findViewById(R.id.index_txt);
            TextView titleTxt = (TextView) itemView
                    .findViewById(R.id.title_txt);
            TextView subTitleTxt = (TextView) itemView
                    .findViewById(R.id.sub_title_txt);
            TextView distanceTxt = (TextView) itemView
                    .findViewById(R.id.distance_txt);
            indexTxt.setVisibility(View.VISIBLE);
            distanceTxt.setVisibility(View.VISIBLE);

            final PoiInfo bean = poiBeans.get(i);
            final String title = bean.getPoiName();
            final String subTitle = bean.getPoiAddress();
            final String distance = getDistanceKm(bean.getDistance()) + "";
            titleTxt.setText(title + "");
            subTitleTxt.setText(subTitle + "");
            // kaizhang 区分颜色显示
            // int namestart = bean.getNameStartindex();
            // int nameend = bean.getNameEndindex();
            // int addrstart = bean.getAddressStartindex();
            // int addrend = bean.getAddressEndindex();
            // if (namestart != nameend) {
            // SpannableStringBuilder namestyle = new SpannableStringBuilder(
            // title);
            // namestyle.setSpan(new ForegroundColorSpan(Color.RED),
            // namestart,nameend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // titleTxt.setText(namestyle);
            // } else {
            // titleTxt.setText(title);
            // }
            // if (addrstart != addrend) {
            // SpannableStringBuilder addressstyle = new
            // SpannableStringBuilder(subTitle);
            // addressstyle.setSpan(new
            // ForegroundColorSpan(Color.RED),addrstart, addrend,
            // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // subTitleTxt.setText(addressstyle);
            // } else {
            // if (null == subTitle || "".equals(subTitle)) {
            // subTitleTxt.setVisibility(View.GONE);
            // } else {
            // subTitleTxt.setText(subTitle);
            // }
            // }
            indexTxt.setText(String.valueOf(i + 1));
            distanceTxt.setText(String.format("%skm", distance));

            final int ind = i;
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = ind;
                    view.setBackgroundResource(R.drawable.item_select_bg);
                    Log.d(TAG, "naviActivity:" + naviActivity);
                    Log.d(TAG, "hasTwice:" + hasTwice);
                    Log.d(TAG, "theTime:" + theTime);
                    // 告诉逻辑层选择哪一个
                    if (isMunaul) {

                        if (hasTwice) {
                            if (theTime == 1) {
                                // change页面展示终点数据
                                FlyNaviManager.getInstance().selectNaviItem(
                                        bean, index);
                                ((NaviActivity) getActivity()).changeSelect();
                            } else {
                                // 关闭页面，开始导航
                                FlyNaviManager.getInstance().selectNaviItem(
                                        bean, index);
                                // ((NaviActivity) getActivity()).finish();
                            }
                        } else {
                            FlyNaviManager.getInstance().selectNaviItem(bean,
                                    index);
                            // ((NaviActivity) getActivity()).finish();
                        }
                    } else {
                        isMunaul = true;
                    }

                }
            });
            rootView.addView(itemView);
        }
    }

    /**
     * 将m装换成km
     *
     * @return
     */
    private double getDistanceKm(int distance) {
        double distance_km = (double) distance / 1000;
        return format1No(distance_km);
    }

    /**
     * 保留一位小数
     *
     * @param f
     * @return
     */
    private double format1No(double f) {
        // TODO Auto-generated method stub
        BigDecimal b = new BigDecimal(f);
        double f1 = b.setScale(1, RoundingMode.HALF_UP).doubleValue();
        return f1;
    }

    @SuppressLint("NewApi")
    public void selectItem(int index) {
        View v = getView();
        if (null != v && v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            int count = g.getChildCount();
            if (index < count) {
                View c = g.getChildAt(index);
                isMunaul = false;
                c.callOnClick();
            }
        }
    }
}
