package com.th.pay.service;

import com.th.entity.TbOrder;

import java.util.Map;

public interface WeChatService {
    //生成二维码，参数1表示订单号，参数2表示总金额
    public Map generaterQRious(String out_trade_no, String total_fee);

    //根据订单号查询订单
    public Map queryPayStatus(String out_trade_no);

    //查找订单后修改状态
    TbOrder findByOrderId(String out_trade_no);


}
