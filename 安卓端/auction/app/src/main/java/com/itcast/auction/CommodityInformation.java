package com.itcast.auction;

import java.util.Date;

public class CommodityInformation {
    //商品id
    protected String commodity_id;
    //商品名
    protected String commodity_name;
    //起拍价
    protected double start_price;
    //起拍时间
    protected Date start_time;
    //拍卖时长
    protected int length_time;
    //保证金
    protected double margin;

    public String getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(String commodity_id) {
        this.commodity_id = commodity_id;
    }

    public String getCommodity_name() {
        return commodity_name;
    }

    public void setCommodity_name(String commodity_name) {
        this.commodity_name = commodity_name;
    }

    public double getStart_price() {
        return start_price;
    }

    public void setStart_price(double start_price) {
        this.start_price = start_price;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public int getLength_time() {
        return length_time;
    }

    public void setLength_time(int length_time) {
        this.length_time = length_time;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }
}
