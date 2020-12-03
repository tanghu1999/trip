package com.th.sso.service.impl;

import com.th.dao.TbUserMapper;
import com.th.entity.TbUser;
import com.th.entity.TbUserExample;
import com.th.sso.service.UserService;
import com.th.utils.ItripResult;
import com.th.utils.JedisClient;
import com.th.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private JedisClient jedisClient;

    @Value("${USERTOKEN}")
    private String USERTOKEN;

    @Value("${USERTOKENTIMEOUT}")
    private int USERTOKENTIMEOUT;

    //导入JMS工具类，它可以进行消息发送、接收等
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination springTopic;



    //对输入的数据通过数据库进行检查
    @Override
    public ItripResult checkData(String param, Integer type) {
        TbUserExample tbUserExample = new TbUserExample();
        List<TbUser> list = null;
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        if(type==1){ //检查userName是否有效
            criteria.andUsernameEqualTo(param);
            list=tbUserMapper.selectByExample(tbUserExample);
            if(list!=null && list.size()>0){
                return ItripResult.build(400, "用户已经存在");
            }else{
                ItripResult.build(200,"ok",true);
            }
        } else if(type==2){ //检验手机号码
            criteria.andPhoneEqualTo(param);
            list=tbUserMapper.selectByExample(tbUserExample);
            if(list!=null && list.size()>0){
                return ItripResult.build(400, "手机号码已经存在");
            }else{
                ItripResult.build(200,"ok",true);
            }
        }else if(type==3) { //检验邮箱
            criteria.andEmailEqualTo(param);
            list = tbUserMapper.selectByExample(tbUserExample);
            if (list != null && list.size() > 0) {
                return ItripResult.build(400, "邮箱已经存在");
            } else {
                ItripResult.build(200,"ok",true);
            }
        }
        return ItripResult.build(200,"数据有效",true);

    }

    //用户注册
    @Override
    public ItripResult register(TbUser user) {
        if(user.getUsername()==null || user.getUsername().trim().length()==0){
            return ItripResult.build(400,"用户名不能为空");
        }else{
            ItripResult result = checkData(user.getUsername(),1);
            if(result.getStatus()==400){
                return result;
            }
        }

        if(user.getPassword()==null || user.getPassword().trim().length()==0){
            return ItripResult.build(400,"密码不能为空");
        }

        if(user.getPhone()==null || user.getPhone().trim().length()==0){
            return ItripResult.build(400,"电话号码不能为空");
        }else{
            ItripResult result = checkData(user.getPhone(),2);
            if(result.getStatus()==400){
                return result;
            }
        }

        if(user.getEmail()==null || user.getEmail().trim().length()==0){
           return ItripResult.build(400,"邮箱不能为空");
        }else{
            ItripResult result = checkData(user.getEmail(),3);
            if(result.getStatus()==400){
                return result;
            }
        }
        //表示用户的信息都是合法可以注册
        final String code = UUID.randomUUID().toString();
        user.setCode(code);
        user.setState(0); //未激活
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //使用md5对密码进行加密
        String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(pwd);
        tbUserMapper.insertSelective(user);

        //向activemq发一个消息
        jmsTemplate.send(springTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //消息的内容为code(激活码)
                TextMessage textMessage = session.createTextMessage(code);
                return textMessage;
            }
        });

        return ItripResult.ok();
    }

    //用户登录
    @Override
    public ItripResult userLogin(String userName, String password) {
        TbUserExample tbUserExample = new TbUserExample();
        List<TbUser> list = null;
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        //按用户名查询
        criteria.andUsernameEqualTo(userName);
        list = tbUserMapper.selectByExample(tbUserExample);
        TbUser tbUser = null;
        if(list==null || list.size()==0){
            return ItripResult.build(400,"用户名不存在！");
        }else{
            tbUser=list.get(0); //用户名唯一
            String pwd = tbUser.getPassword(); //从数据库中取出密码(加过密)
            //取数据库中的密码与输入的密码加密后进行比较
            if(!pwd.equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
                return ItripResult.build(400,"密码错误！");
            }
            //用户名与密码是正确的
            //将登录成功的用户信息存储到redis中 30分钟 key-value
            String token = UUID.randomUUID().toString(); //key
            String jsonString = JsonUtils.objectToJson(tbUser); //vaule
            //写入redis设有效期
            jedisClient.set(USERTOKEN+token,jsonString);
            jedisClient.expire(USERTOKEN+token,USERTOKENTIMEOUT);
            //将token返回到前台sessionId
            return ItripResult.build(200,"ok",token);
        }

    }


    //通过token得到用户信息
    @Override
    public ItripResult getUserByToken(String token) {
        String jsonString =null;
        if(jedisClient.exists(USERTOKEN+token)){
            jsonString=jedisClient.get(USERTOKEN+token);
        }
        if(jsonString==null || jsonString.trim().length()==0){
            return ItripResult.build(400,"当前用户已过期！");
        }
        //设置有效时间
        jedisClient.expire(USERTOKEN+token,USERTOKENTIMEOUT);
        TbUser tbUser = JsonUtils.jsonToPojo(jsonString,TbUser.class);
        return ItripResult.build(200,"ok",tbUser);

    }

    //通过code得到用户信息
    @Override
    public TbUser getUserByCode(String code) {
        TbUserExample tbUserExample = new TbUserExample();
        List<TbUser> list = null;
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        criteria.andCodeEqualTo(code);
        list = tbUserMapper.selectByExample(tbUserExample);
        TbUser tbUser = list.get(0);

        return tbUser;
    }

    //完成用户激活
    @Override
    public ItripResult jihuo(String code) {
        TbUser tbUser = getUserByCode(code);
        tbUser.setState(1); //将状态修改为激活
        tbUserMapper.updateByPrimaryKeySelective(tbUser);

        return ItripResult.ok();
    }


}
