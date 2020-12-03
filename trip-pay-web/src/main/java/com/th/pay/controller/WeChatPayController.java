package com.th.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.th.entity.TbOrder;
import com.th.pay.service.WeChatService;
import com.th.utils.WxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.UUID;

@Controller
public class WeChatPayController {

    @Autowired
    private WeChatService weChatService;


    //发送支付请求生成对应的二维码
    @RequestMapping("/wxPay/{out_trade_no}/{total_fee}")
    public String pay(
            @PathVariable("out_trade_no") String out_trade_no,
            @PathVariable("total_fee") String total_fee,
            Model model) throws Exception{
        //金额，单位为分
        total_fee="1";

        Map map =  weChatService.generaterQRious(out_trade_no, total_fee);
        model.addAttribute("map",map);
        System.out.println(map);

        return "qrious";
    }

    //查询支付状态
    @RequestMapping("/payStatus")
    @ResponseBody
    public String payStatus(String out_trade_no) throws InterruptedException{
        WxResult wxResult = null;
        //设置无限循环，目的是实时获取订单状态
        while(true){
            Map map = weChatService.queryPayStatus(out_trade_no);
            System.out.println("payStatusMap:"+map);
            if(map==null){
                wxResult=new WxResult(false,"支付失败");
                break;
            }
            if("SUCCESS".equals(map.get("trade_state"))){
                wxResult = new WxResult(true,"支付成功");
                //根据订单号查询一个订单的状态并修改状态
                TbOrder tbOrder = weChatService.findByOrderId(out_trade_no);
                System.out.println(tbOrder);
                break;
            }
            //加入定时器，每隔10s查询一次
            Thread.sleep(10000);
        }
        //转换为json格式字符串
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wxResult",wxResult);
        System.out.println("jsonObject:"+jsonObject.toJSONString());

        return jsonObject.toJSONString();

    }



}
