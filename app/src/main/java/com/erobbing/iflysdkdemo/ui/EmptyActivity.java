package com.erobbing.iflysdkdemo.ui;

import com.erobbing.iflysdkdemo.SpeechDemoApplication;
import com.erobbing.iflysdkdemo.R;
import com.iflytek.sdk.interfaces.ITtsUiListener;
import com.iflytek.sdk.manager.FlyTtsManager;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class EmptyActivity extends Activity {
    private String tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        Intent intent = new Intent();
        intent = getIntent();
        if (intent != null) {
            tip = intent.getStringExtra("tips");
        }
        Log.d("jiyang2", "Activity中tip= " + tip);
        TextView tv = (TextView) findViewById(R.id.tv_empty);
        tv.setText(tip);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
//				try {
////					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
                FlyTtsManager.getInstance().create(
                        AudioManager.STREAM_MUSIC);
                FlyTtsManager.getInstance().speak(
                        tip, l);
            }
        }).start();

    }

    ITtsUiListener l = new ITtsUiListener() {

        @Override
        public void onProgress(int textindex, int textlen) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPlayCompleted() {
            // TODO Auto-generated method stub
            finish();
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

    protected void onPause() {
        super.onPause();
        SpeechDemoApplication.removeALLActivity();
    }

}
