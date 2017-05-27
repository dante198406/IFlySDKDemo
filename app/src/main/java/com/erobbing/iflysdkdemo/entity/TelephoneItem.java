package com.erobbing.iflysdkdemo.entity;

/**
 * 电话实体类,用于解析查询后传过来的json
 *
 * @author zhmao2
 */
public class TelephoneItem {
    private String name;
    private String city;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
