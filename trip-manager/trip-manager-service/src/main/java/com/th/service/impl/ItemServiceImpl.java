package com.th.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.th.dao.TbItemDescMapper;
import com.th.dao.TbItemMapper;
import com.th.entity.TbItem;
import com.th.entity.TbItemDesc;
import com.th.entity.TbItemExample;
import com.th.service.ItemService;
import com.th.utils.IDUtils;
import com.th.utils.JedisClient;
import com.th.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Date;
import java.util.List;
@Service
public class ItemServiceImpl implements ItemService {

    //增加商品
    @Autowired
    private TbItemMapper itemMapper;
    //增加商品描述
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    //Spring 提供的 JMS 工具类，它可以进行消息发送、接收等
    @Autowired
    private JmsTemplate jmsTemplate;
    //得到队列对象，向topic发消息
    @Autowired
    private Destination springTopic;
    //查询缓存
    @Autowired
    private JedisClient jedisClient;
    //从配置文件中读取key的名称
    @Value("${ITEMID}")
    private String ITEMID;


    //查询所有商品
    @Override
    public PageInfo<TbItem> findAll(String page,String limit,Long cid) {
        PageHelper.startPage(Integer.valueOf(page),Integer.valueOf(limit));

        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        if(cid!=null){
            criteria.andCidEqualTo(cid); //参数类型只能是long
        }

        List<TbItem> tbItems = itemMapper.selectByExample(itemExample);
        PageInfo<TbItem> pageInfo = new PageInfo<>(tbItems);

        return pageInfo;

    }

    //增加一条商品信息
    @Override
    public void insert(TbItem tbItem,String content1) {
        final Long id = IDUtils.genItemId();
        tbItem.setId(id);
        tbItem.setStatus((byte) 1);
        tbItem.setCreated(new Date());
        tbItem.setUpdated(new Date());

        itemMapper.insertSelective(tbItem);

        //创建商品详情对象
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(id);
        tbItemDesc.setItemDesc(content1);
        tbItemDesc.setCreated(new Date());
        tbItemDesc.setUpdated(new Date());

        tbItemDescMapper.insert(tbItemDesc);

        //将商品编号发到activemq中
        jmsTemplate.send(springTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(id+"");

                return textMessage;
            }
        });

    }

    //从redis缓存中查询一条商品信息
    @Override
    public TbItem findById(Long itemId) {
        //1.判断redis中是否存在这个商品详情
        String jsonString = null;
        TbItem tbItem = null;
        //判断key是否在redis中存在，ITEMID:-->":"代表文件夹,相当于在ITEMID文件夹查找名称为itemId的key
        if(jedisClient.exists(ITEMID+":"+itemId)){
            jsonString = jedisClient.get(ITEMID+":"+itemId);
            //把json格式字符串转换为对象
            tbItem = JsonUtils.jsonToPojo(jsonString,TbItem.class);
        }else{
            //2.redis中不存在，查询数据库
            tbItem = itemMapper.selectByPrimaryKey(itemId);
            //把对象转换为json格式字符串
            jsonString = JsonUtils.objectToJson(tbItem);
            jedisClient.set(ITEMID+":"+itemId,jsonString);
            jedisClient.expire(ITEMID+":"+itemId,18600);

        }

        return tbItem;
    }

    //删除一条记录
    @Override
    public void delete(Long id) {
        itemMapper.deleteByPrimaryKey(id);
    }

    //修改一条记录
    @Override
    public void update(TbItem tbItem) {
        tbItem.setUpdated(new Date());
        itemMapper.updateByPrimaryKeySelective(tbItem);
    }

    @Override
    public void delMany(int[] ids) {
        itemMapper.delMany(ids);
    }
}
