package com.th.order.service;


import com.th.utils.ItripResult;
import com.th.utils.Order;

public interface OrderService {

    //创建一个订单
    ItripResult createOrder(Order order);



}
