package com.erobbing.iflysdkdemo;

import java.io.File;

import com.iflytek.clientadapter.constant.ErrorValue;
import com.iflytek.sdk.manager.FlyHmiManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * 加载界面
 */
public class LoadActivity extends Activity {
    private final String TAG = "LoadActivity";
    //	 private final String videoPath ="mnt"+File.separator+"sdcard"+File.separator+"iflytek"+File.separator+"res"+File.separator+"video"+File.separator+"load.mp4";
    private final String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "iflytek" + File.separator + "res" + File.separator + "video" + File.separator + "load.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, SpeechActivity.class);
        setContentView(R.layout.activity_main);

        startActivity(intent);

		/*VideoView videoView = (VideoView) findViewById(R.id.videoView);
		if (ErrorValue.INIT_SUCCESS != FlyHmiManager.getInstance()
				.isInitComplete()) {
			Log.d(TAG,"videoPath= "+videoPath);
		videoView.setVideoPath(videoPath);
//		videoView.setMediaController(new MediaController(this));
		
		
		
		videoView.requestFocus();
        videoView.start();
        }else{
        	startActivity(intent);
			return;
        }
		videoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				// TODO Auto-generated method stub
				
				if (ErrorValue.INIT_SUCCESS == FlyHmiManager.getInstance()
						.isInitComplete()) {
					Log.d(TAG, "onResume() start speech");
					startActivity(intent);
					return;
				}
				else{
				mediaPlayer.seekTo(0);
				mediaPlayer.start();
				}
			}
		});*/
//		DemoApp.addActivity(this);
    }

//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//
//
//	}

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
