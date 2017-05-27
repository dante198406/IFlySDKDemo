package com.erobbing.iflysdkdemo.interfaces;


public interface SpeechViewUpdateListenner {


    public void updateUIInRecodingState(); // 录音开始状态

    public void updateUIInWaitingResultState(); // 等待结果状态

    public void updateUIInCancelState(); // 取消状态

    public void updateUIInErrorState(); // 错误状态

    public void updateUIAfterResult(String focus, String result); // 获得了结果后状态

    public void updateUIShowTip(String result);// 显示提示状态

    public void updateUIRecodingVolume(int vlume); // 音量更新状态

    public void destroyView(); // 销毁视图

}