package com.th.service.listener;

import com.th.dao.TbItemMapper;
import com.th.utils.SearchItem;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

//监控activemq，从activemq中得到新增加商品编号
//将新增加的商品增加到solr索引库中
public class ActiveMqMessageListener implements MessageListener {

    //通过dao层接口对象把id传过去得到新增商品对象
    @Autowired
    private TbItemMapper tbItemMapper;

    //得到solr对象把新增的数据增加到索引库中
    @Autowired
    private HttpSolrServer httpSolrServer;

    //监听信息
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        Long itemId = null;
        SearchItem searchItem=null;
        try {
            //得到新增的商品编号
            Thread.sleep(1000);
            //接收信息
            itemId = Long.parseLong(textMessage.getText());
            System.out.println("itemId:"+itemId);
            searchItem=tbItemMapper.searchItemById(itemId);
            //调用方法把新增的对象加到索引库
            this.addSearchItemSolr(searchItem);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //把增加的记录加到solr索引库中
    private void addSearchItemSolr(SearchItem searchItem) throws Exception{
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField("id",searchItem.getId());
        solrInputDocument.addField("item_title",searchItem.getTitle());
        solrInputDocument.addField("item_sell_point",searchItem.getSellPoint());
        solrInputDocument.addField("item_price",searchItem.getPrice());
        solrInputDocument.addField("item_category_name",searchItem.getCategoryName());
        solrInputDocument.addField("item_image",searchItem.getImage());
        httpSolrServer.add(solrInputDocument);
        //提交
        httpSolrServer.commit();

    }






}
