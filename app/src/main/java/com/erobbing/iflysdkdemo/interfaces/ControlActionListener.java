package com.erobbing.iflysdkdemo.interfaces;

public interface ControlActionListener {

    public void nextPage();

    public void prePage();

    /**
     * 选中某一项
     *
     * @param index 0/1/2
     */
    public void selectItem(int index);
}
