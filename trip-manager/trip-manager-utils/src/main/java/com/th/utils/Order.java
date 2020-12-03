package com.th.utils;

import com.th.entity.TbItem;
import com.th.entity.TbOrderShipping;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String payment; //总金额
    private int payType; //支付的类型 1货到付款 2微信支付 3支付宝支付
    private String userId; //购买人的编号
    private List<TbItem> orderItems; //购物明细
    private TbOrderShipping orderShipping; //收货信息

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<TbItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TbItem> orderItems) {
        this.orderItems = orderItems;
    }

    public TbOrderShipping getOrderShipping() {
        return orderShipping;
    }

    public void setOrderShipping(TbOrderShipping orderShipping) {
        this.orderShipping = orderShipping;
    }
}
