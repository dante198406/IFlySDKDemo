package com.erobbing.iflysdkdemo.entity;


/**
 * Poi信息：
 * 包含如下参数：longitude：经度；
 * latitude：纬度；
 * poiName：poi名称；
 * poiAddress：poi地址；
 * poiCity：poi所在城市；
 * distance=-1：搜索范围；
 * category=0：分类，0普通   1家   2公司。
 *
 * @author huhai2
 */
public class PoiInfo {

    // modify by haihu2

    /**
     * 经度
     */
    public double longitude;//经度

    /**
     * 维度
     */
    public double latitude;//纬度

    /**
     * poi名称
     */
    public String poiName;//poi名称

    /**
     * poi所在地址
     */
    public String poiAddress;//poi所在地址
    /**
     * poi所在城市
     * 如果是当前城市，则传入CURRENT_CITY
     */
    public String poiCity;//poi所在城市
    /**
     * 搜索距离
     */
    public int distance = -1;//搜索距离
    /**
     * 分类  ：
     * 0普通   1家   2公司
     */
    public int category = 0;//分类


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getPoiAddress() {
        return poiAddress;
    }

    public void setPoiAddress(String poiAddress) {
        this.poiAddress = poiAddress;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiCity() {
        return poiCity;
    }

    public void setPoiCity(String poiCity) {
        this.poiCity = poiCity;
    }

    @Override
    public String toString() {
        return "PoiInfo [longitude=" + longitude + ", latitude=" + latitude
                + ", poiName=" + poiName + ", poiAddress=" + poiAddress
                + ", poiCity=" + poiCity + ", distance=" + distance
                + ", category=" + category + "]";
    }


}
