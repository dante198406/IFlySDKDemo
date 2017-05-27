package com.erobbing.iflysdkdemo.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erobbing.iflysdkdemo.R;
import com.iflytek.clientadapter.aidl.ContactEntity;
import com.iflytek.sdk.manager.FlyPhoneManager;

public class TelephoneItemFragment extends Fragment {
    private LayoutInflater mInflater;
    private List<ContactEntity> tpBeans;
    private int size = 0;

    /**
     * 是否手动点击
     */
    private boolean isMunaul = true;

    /**
     * 构造并传递联系人的list
     *
     * @param suBeans
     */
    public TelephoneItemFragment(List<ContactEntity> suBeans) {
        this.tpBeans = suBeans;
        this.size = this.tpBeans.size();
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
        Typeface type1 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");
        Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface type3 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");
        // Typeface type4=
        // Typeface.createFromAsset(getActivity().getAssets(),"SC-Medium.ttf");

        for (int i = 0; i < 3; i++) {
            RelativeLayout itemView = (RelativeLayout) mInflater.inflate(
                    R.layout.item_telephone, null);
            LayoutParams param = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
            itemView.setLayoutParams(param);
            if (i >= size) {
                rootView.addView(itemView);
                continue;
            }
            RelativeLayout item = (RelativeLayout) itemView
                    .findViewById(R.id.item_telephone);
            item.setVisibility(View.VISIBLE);
            final TextView indexTxt = (TextView) itemView
                    .findViewById(R.id.index_txt);
            TextView locationTxt = (TextView) itemView
                    .findViewById(R.id.location_txt);
            TextView nameTxt = (TextView) itemView.findViewById(R.id.name_txt);
            TextView numberTxt = (TextView) itemView
                    .findViewById(R.id.number_txt);

            indexTxt.setVisibility(View.VISIBLE);

            final ContactEntity bean = tpBeans.get(i);
            final String location = bean.getCity();
            final String name = bean.getName();
            final String number = bean.getNumber();

            indexTxt.setText((i + 1) + "");
            locationTxt.setText(location);
            nameTxt.setText(name);
            numberTxt.setText(number);

            indexTxt.setTypeface(type3);
            numberTxt.setTypeface(type2);
            nameTxt.setTypeface(type1);

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    indexTxt.setBackgroundResource(R.drawable.num_select_bg);
                    view.setBackgroundResource(R.drawable.item_select_bg);
                    // 告诉逻辑层选择哪一个
                    if (isMunaul) {
                        FlyPhoneManager.getInstance().selectItem(bean);
                    } else {
                        isMunaul = true;
                    }
                }
            });
            rootView.addView(itemView);
        }

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
