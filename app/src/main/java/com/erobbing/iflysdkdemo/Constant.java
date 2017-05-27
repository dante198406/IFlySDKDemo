package com.erobbing.iflysdkdemo;


public class Constant {
    //选择器中每页显示的条目数
    public final static int NUM_PER_PAGE = 3;
    public final static int NUM_TRAIN_PAGE = 2;
    // 第一页编号
    public static final int DEFAULT_FIRST_PAGE = 1;
    // 每页长度
    public static final int DEFAULT_PAGE_SIZE = 20;

    //区分Fragment的标签
    public final static int GO_NAVI_FRAGMENT = 10080;
    public final static int GO_TELPHONE_FRAGMENT = 10081;
    public final static int GO_MUSIC_FRAGMENT = 10082;
    public final static int GO_HOTEL_FRAGMENT = 10083;
    public final static int GO_RESTAURANT_FRAGMENT = 10084;
    public final static int GO_TRAIN_FRAGMENT = 10085;
    public final static int GO_FLIGHT_FRAGMENT = 10086;

    //选择器头部提示
    public static final String NAVI_MAIN_TITLE = "请选择:";
    public static final String TELEPHONE_MAIN_TITLE = "打电话给:";
    public static final String MUSIC_MAIN_TITLE = "音乐:";
    public static final String HOTEL_MAIN_TITLE = "宾馆信息:";
    public static final String RESTAURANT_MAIN_TITLE = "美食信息:";
    public static final String TRAIN_MAIN_TITLE = "火车:";
    public static final String FLIGHT_MAIN_TITLE = "航班:";

    //选择器底部提示
    public static final String TIP_SINGAL_ITEM = "请说 '确定' 或 '取消' ";
    public static final String TIP_SINGAL_PAGER = "请说 '第几个' 或 '取消' ";
    public static final String TIP_MULTI_PAGER = "请说 '第几个','翻页' 或 '取消'";

    public static final String NO_RESULT_STRING = "— —";//宾馆没有地址

    //选择情况消息发送
    public final static int MSG_PRE_PAGE = 10085;
    public final static int MSG_NEXT_PAGE = 10086;
    public final static int MSG_FIRST = 10087;
    public final static int MSG_SECEND = 10088;
    public final static int MSG_THIRD = 10089;
    public final static int MSG_ITEM_CLICKED = 10090;

    public static final String INCOMING_CALL = "com.iflytek.PHONE_INCOMING";

}
