package com.th.order.controller;

import com.th.entity.TbItem;
import com.th.order.service.OrderService;
import com.th.utils.CookieUtils;
import com.th.utils.ItripResult;
import com.th.utils.JsonUtils;
import com.th.utils.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    //从cookie中得到一个cart
    private List<TbItem> getCarList(HttpServletRequest request){
        List<TbItem> list = null;
        //1.从cookie中得到一个购物车
        String cartCookie = CookieUtils.getCookieValue(request,"CART",true);
        //2.判断cookie中是否有购物车
        if(cartCookie!=null && cartCookie.trim().length()>0){
            list = JsonUtils.jsonToList(cartCookie,TbItem.class);
        }else{
            list = new ArrayList<>();
        }

        return list;
    }


    //得到购物车并跳转到订单页面
    @RequestMapping("/order/order-cart")
    public String createOrder(HttpServletRequest request, Model model){
        List<TbItem> list = getCarList(request);
        model.addAttribute("cartList",list);

        return "order-cart";
    }

    //增加一个订单信息
    @RequestMapping("/order/create")
    public String createOrder(Order order){
        ItripResult result = orderService.createOrder(order);
        //得到订单编号
        String orderId = result.getData().toString();
        //得到总金额
        String payment = order.getPayment();
        //微信支付
        if(order.getPayType()==2){
            return "redirect:http://localhost:8087/wxPay/"+orderId+"/"+payment;
        }else if(order.getPayType()==3){
            //支付宝支付
            return "redirect:http://localhost:8087/aliPay/"+orderId+"/"+payment;
        }else{
            //货到付款
            return "success";
        }

    }



}
