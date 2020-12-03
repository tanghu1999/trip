package com.th.pay.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.th.dao.TbOrderMapper;
import com.th.entity.TbOrder;
import com.th.pay.service.WeChatService;
import com.th.utils.HttpClient;
import com.th.utils.PayCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private TbOrderMapper tbOrderMapper;

    //生成二维码
    public Map generaterQRious(String out_trade_no, String total_fee) {
        try {
            //1.参数封装
            Map<String,String> map = new HashMap<String, String>();
            map.put("appid", PayCode.appid); //应用id
            map.put("mch_id",PayCode.partner); //商户号
            map.put("nonce_str", WXPayUtil.generateNonceStr()); //生成随机码

            map.put("body","trip订单结算"); //商品描述
            map.put("out_trade_no",out_trade_no); //商品订单号
            map.put("total_fee",total_fee); //订单总金额
            map.put("spbill_create_ip","127.0.0.1"); //终端ip
            map.put("notify_url",PayCode.notifyurl); //异步通知地址
            map.put("trade_type","NATIVE"); //交易类型
            //2.发送请求
            //生成xml类型
            String xmlMap = WXPayUtil.generateSignedXml(map,PayCode.partnerkey);
            //请求类
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true); //是否遵循https协议
            httpClient.setXmlParam(xmlMap); //添加xml数据
            httpClient.post(); //post请求发送
            //3.获取返回结果
            String xmlResult = httpClient.getContent();
            //把获取的xml转化为map类型
            Map<String,String> xmlToMap = WXPayUtil.xmlToMap(xmlResult);
            Map<String,String> payMap = new HashMap<>();
            //生成支付二维码所需要的数据
            payMap.put("code_url",xmlToMap.get("code_url")); //二维码扫出来的链接地址
            payMap.put("total_fee",total_fee); //总金额
            payMap.put("out_trade_no",out_trade_no); //订单编号

            return payMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap() ;
    }

    //根据订单号查询订单
    public Map queryPayStatus(String out_trade_no) {
        //1.参数封装
        Map<String,String> paramMap = new HashMap<String, String>();
        paramMap.put("appid",PayCode.appid); //应用id
        paramMap.put("mch_id",PayCode.partner); //商户号
        paramMap.put("out_trade_no",out_trade_no); //商户订单号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr()); //生成随机码
        //2.发送请求
        try {
            //转化为xml类型
            String xmlParam = WXPayUtil.generateSignedXml(paramMap,PayCode.partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //3.返回结果
            String xmlResult = httpClient.getContent();
            Map<String,String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println(map);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    @Override
    public TbOrder findByOrderId(String out_trade_no) {
        TbOrder tbOrder =  tbOrderMapper.selectByPrimaryKey(out_trade_no);
        tbOrder.setStatus(2); //把未支付变为已支付
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);

        return tbOrder;
    }
}
