package com.th.sso.service.listener;

import com.th.entity.TbUser;
import com.th.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

//监控mq ,从mq中得到新增加商品编号  itemId
//将新增加的商品增加solr库中
public class ActiveMqMessageListener implements MessageListener {

@Autowired
private UserService userService;

@Autowired
private MailSender  mailSender;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        String code=null;
        try {
            Thread.sleep(1000);
            //接收activemq发送的信息
           code=textMessage.getText();
           //发一个邮件
            //1.根据code得到user
            TbUser user=userService.getUserByCode(code);
            //2.发邮件：链接http://localhost:8084/user/code
            //第一个参数为：用户的邮箱地址，第二个参数为邮件的内容
            sendEmail(user.getEmail(),code);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String email,String code){
        //1.创建一封邮件对象
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        //2.发件人
        simpleMailMessage.setFrom("1621037507@qq.com");
        //3.收件人
        simpleMailMessage.setTo(email); //mail是邮箱地址
        //simpleMailMessage.setCc("77376447@qq.com");
        //4.邮件的主题
        simpleMailMessage.setSubject("trip激活用户");
        //5.邮件内容
        String text="请激活,点击链接完成激活：http://localhost:8084/user/jihuo/"+code;
        simpleMailMessage.setText(text);
        //6.发送邮件
        mailSender.send(simpleMailMessage);
    }


    private void sendSms(){}

}
