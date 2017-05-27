package com.erobbing.iflysdkdemo.ui.view;

import com.erobbing.iflysdkdemo.interfaces.ControlActionListener;
import com.erobbing.iflysdkdemo.interfaces.SpeechViewUpdateListenner;


public class UIControl {
    private static UIControl mInstance = null;
    private SpeechViewUpdateListenner mSpeechListenner;
    private ControlActionListener mTelListener;

    private UIControl() {

    }

    public static UIControl getInstance() {
        if (mInstance == null) {
            mInstance = new UIControl();
        }
        return mInstance;
    }

    public SpeechViewUpdateListenner getUIListenner() {
        return mSpeechListenner;
    }

    public void setListenner(SpeechViewUpdateListenner mSpeechViewNew) {
        this.mSpeechListenner = mSpeechViewNew;
    }

    public ControlActionListener getTelListener() {
        return mTelListener;
    }

    public void setListenner(ControlActionListener mTelListener) {
        this.mTelListener = mTelListener;
    }

}
