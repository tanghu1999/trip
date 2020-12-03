package com.th.order.service.impl;

import com.th.dao.TbOrderItemMapper;
import com.th.dao.TbOrderMapper;
import com.th.dao.TbOrderShippingMapper;
import com.th.entity.TbItem;
import com.th.entity.TbOrder;
import com.th.entity.TbOrderItem;
import com.th.entity.TbOrderShipping;
import com.th.order.service.OrderService;
import com.th.utils.IDUtils;
import com.th.utils.ItripResult;
import com.th.utils.JedisClient;
import com.th.utils.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    //订单表
    @Autowired
    private TbOrderMapper tbOrderMapper;
    //订单详情表
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    //订单用户表
    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;
    @Value("${ORDERID}")
    private String ORDERID;
    @Autowired
    private JedisClient jedisClient;

    //增加一条订单信息
    @Override
    public ItripResult createOrder(Order order) {
        String orderId = jedisClient.get(ORDERID);
        if(orderId==null || orderId.trim().length()==0){
            orderId=System.currentTimeMillis()+"";
            jedisClient.set(ORDERID,orderId);
        }
        //订单编号
        Long id = jedisClient.incr(ORDERID);
        //1.向订单表增加一条记录
        TbOrder tbOrder = new TbOrder(id+"",order.getPayment(),order.getPayType(),
                0+"", 1,Long.parseLong(order.getUserId()),new Date(),new Date());
        tbOrderMapper.insertSelective(tbOrder);
        //2.将cart中的商品增加到订单明细表中
        TbOrderItem orderItem = null;
        for(int i=0;i<order.getOrderItems().size();i++){
            //购买的每一个商品
            TbItem tbItem = order.getOrderItems().get(i);
            orderItem = new TbOrderItem(IDUtils.genItemId()+"",tbItem.getId()+"",id+"",tbItem.getNum(),
                    tbItem.getTitle(),tbItem.getPrice(),tbItem.getPrice()*tbItem.getNum(),tbItem.getImage());
            tbOrderItemMapper.insertSelective(orderItem);
        }
        //3.将这个订单收货信息增加到收货表中
        TbOrderShipping tbOrderShipping = order.getOrderShipping();
        tbOrderShipping.setOrderId(id+"");
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setUpdated(new Date());
        tbOrderShippingMapper.insertSelective(tbOrderShipping);

        return ItripResult.build(200,"ok",id+"");
    }
}
