package com.th.pay.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.th.utils.AlipayConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AliPayController {

    @RequestMapping("/pay/notify_url")
    public String notify_url(){
        return "notify_url";
    }

    @RequestMapping("/pay/return_url")
    public String return_url(){
        return "return_url";
    }

    //创建支付
    @RequestMapping("aliPay/{trade_no}/{total_amount}")
    public void createPay(@PathVariable("trade_no") String trade_no,
                          @PathVariable("total_amount") String total_amount,
                          HttpServletRequest request, HttpServletResponse response) throws Exception{
        //获得初始化的AlipayClient
        String subject = "订单说明";
        String body = "商品说明";
        //商家支付宝信息
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,AlipayConfig.app_id,AlipayConfig.merchant_private_key,
                "json",AlipayConfig.charset,AlipayConfig.alipay_public_key,AlipayConfig.sign_type);
        //请求参数设置
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        //得到请求完成返回的url
        alipayTradePagePayRequest.setReturnUrl(AlipayConfig.return_url);
        //请求完成的通知url
        alipayTradePagePayRequest.setNotifyUrl(AlipayConfig.notify_url);
        //商品信息
        alipayTradePagePayRequest.setBizContent("{\"out_trade_no\":\""+ trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求
        String result = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(result);
        response.getWriter().flush();
        response.getWriter().close();

    }



}
