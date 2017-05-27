package com.erobbing.iflysdkdemo.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erobbing.iflysdkdemo.R;

public class HelpSystem extends FrameLayout {

    public HelpSystem(Context context) {
        this(context, null);
    }

    public HelpSystem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HelpSystem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = View.inflate(context, R.layout.layout_localview, this);
        LinearLayout linearLayout = (LinearLayout) view
                .findViewById(R.id.my_layoyt);
        String text[] = getResources().getStringArray(R.array.system);
        // TextView tv = (TextView) findViewById(R.id.tv);
        // tv.setText("我是第一个");
        for (int i = 0; i < text.length; i++) {
            String item = text[i];
            TextView textView = new TextView(context);
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            textView.setLayoutParams(params);
            params.topMargin = 14;
            // textView.setTextColor(getResources().getColor(R.color.tipcolor));
            textView.setText(item);
            linearLayout.addView(textView);

        }

    }
}
