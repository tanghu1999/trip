package com.th.controller;

import com.th.entity.TbItem;
import com.th.entity.TbItemDesc;
import com.th.service.ItemDescService;
import com.th.service.ItemService;
import com.th.utils.Item;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ItemController implements MessageListener {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemDescService itemDescService;

    @Autowired
    private JmsTemplate jmsTemplate;
    //得到队列对象，向topic发消息
    @Autowired
    private Destination springTopic;

    private TextMessage textMessage = null;

    @RequestMapping("item/{itemId}")
    public String showItem(@PathVariable("itemId") final Long itemId){

        System.out.println(itemId);
        //将商品编号发到activemq中
        jmsTemplate.send(springTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                textMessage = session.createTextMessage(itemId+"");

                return textMessage;
            }
        });

        //先判断网页是否存在不存在则创建
        File html = new File("D:\\nginx1.12.2\\nginx1.12.2\\nginx1.12.2\\output\\"+itemId+".html");
        if(!html.exists()){
            this.onMessage(textMessage);
        }

        return "redirect:http://localhost:8888/"+itemId+".html";
    }


    @Override
    public void onMessage(Message message) {
        //得到商品编号
        Long itemId = null;
        TbItem tbItem = null;
        TbItemDesc tbItemDesc = null;
        Item item = null;
        try {
            itemId = Long.parseLong(textMessage.getText());
            tbItem = itemService.findById(itemId);
            item = new Item(tbItem);
            tbItemDesc = itemDescService.findById(itemId);

            //使用Freemarker实现网页静态化
            // 1.创建一个Configuration对象,构造方法的参数就是 freemarker对于的版本号
            Configuration configuration = new Configuration(Configuration.getVersion());
            //2.设置freemarker所有模板存放的目录
            configuration.setDirectoryForTemplateLoading(new File("D:\\idea2020.1.2\\project\\trip\\trip-item-web\\src\\main\\webapp\\WEB-INF\\ftl"));
            //3.设置模板文件使用的字符集
            configuration.setDefaultEncoding("utf-8");
            //4.加载一个模板，创建一个模板对象
            Template template = configuration.getTemplate("item.ftl");
            //5.创建一个模板使用的数据集，可以是pojo也可以是map，一般是map
            Map dataMap = new HashMap();
            //向数据集中添加数据
            dataMap.put("item",item);
            dataMap.put("itemDesc",tbItemDesc);
            //6.创建一个Writer对象，一般创建一个FileWriter对象，指定生成的文件名
            Writer out = new FileWriter(new File("D:\\nginx1.12.2\\nginx1.12.2\\nginx1.12.2\\output\\"+itemId+".html"));
            //7.调用模板对象的process方法输出文件
            template.process(dataMap,out);
            //8.关闭流
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
