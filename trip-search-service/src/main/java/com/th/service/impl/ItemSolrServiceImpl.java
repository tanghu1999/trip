package com.th.service.impl;

import com.th.dao.TbItemMapper;
import com.th.service.ItemSolrService;
import com.th.utils.SearchItem;
import com.th.utils.SearchResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemSolrServiceImpl implements ItemSolrService {
    @Value("${pageSize}")
    private int pageSize;

    @Autowired
    private TbItemMapper tbItemMapper;
    //把SolrServer服务器对象引用进来
    @Autowired
    private SolrServer solrServer;

    @Override
    public List<SearchItem> searchAllItemToSolr() {
        //查询所有的商品
        List<SearchItem> searchItems = tbItemMapper.searchAll();
        //将商品增加到solr中
        try {
            for (SearchItem searchItem : searchItems) {
                //创建SolrInputDocument对象用于保存一条数据(一个对象里面保存一条数据)
                SolrInputDocument solrDocument = new SolrInputDocument();
                solrDocument.addField("id",searchItem.getId());
                solrDocument.addField("item_title",searchItem.getTitle());
                solrDocument.addField("item_sell_point",searchItem.getSellPoint());
                solrDocument.addField("item_price",searchItem.getPrice());
                solrDocument.addField("item_category_name",searchItem.getCategoryName());
                solrDocument.addField("item_image",searchItem.getImage());
                //把这条数据增加到solr服务器中
                solrServer.add(solrDocument);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchItems;
    }

    @Override
    public SearchResult queryItemFromSolr(String q, String page) throws Exception {
        //创建一个查询结果返回对象
        SearchResult searchResult = new SearchResult();
        //创建查询结果返回对象的集合
        List<SearchItem> items = new ArrayList<>();
        //创建查询对象
        SolrQuery solrQuery = new SolrQuery();

        solrQuery.set("df","item_keywords");
        solrQuery.setQuery(q); //q是用户输入的关键字

        solrQuery.addSort("item_price",SolrQuery.ORDER.desc);
        //启用分页
        solrQuery.setStart((Integer.valueOf(page)-1)*pageSize); //起始行号
        solrQuery.setRows(pageSize); //每页显示的记录数：5

        solrQuery.setHighlight(true); //开启高亮
        solrQuery.addHighlightField("item_title");
        solrQuery.addHighlightField("item_sell_point");
        //在关键字前面加上
        solrQuery.setHighlightSimplePre("<font color='red'>");
        //在关键字后面加上
        solrQuery.setHighlightSimplePost("</font>");
        //得到solr服务器返回结果对象
        QueryResponse response = solrServer.query(solrQuery);
        //得到所有的结果集
        SolrDocumentList results = response.getResults();
        //总行数
        Long rows = results.getNumFound();
        if(rows % pageSize==0){
            searchResult.setTotalPages(rows/pageSize);
        }else{
            searchResult.setTotalPages(rows/pageSize+1);
        }
        //遍历结果集对象
        for (SolrDocument result : results) {
            //通过字段名得到对应的值
            String id = result.get("id").toString();
            String title = result.get("item_title").toString();
            String categoryName = result.getFieldValue("item_category_name").toString();
            //从索引库中取图片，排除图片为空的情况
            String[] images=null;
            System.out.println(result.get("item_image"));
            if(result.get("item_image").toString()!=null && result.get("item_image").toString().length()>0){
                images = result.get("item_image").toString().split(",");
            }
            String image = null;
            if(images!=null && images.length>0){
                image=images[0];
            }else{
                image="http://192.168.25.133/group1/M00/00/04/wKgZhV-015yADn8YAABh5N2XzCo728.jpg";
            }
            String price = result.get("item_price").toString();
            String sellPoint = result.getFieldValue("item_sell_point").toString();
            //得到response中所有高亮的数据
            Map<String, Map<String,List<String>>> highLight = response.getHighlighting();
            //通过id得到所有的高亮数据
            Map<String, List<String>> stringListMap = highLight.get(id);
            //通过字段名得到字段名对应的结果集
            List<String> titleList = stringListMap.get("item_title");
            if(titleList!=null){
                title = titleList.get(0); //把添加高亮后的值赋给原来的字段值
            }
            List<String> sellPointList = stringListMap.get("item_sell_point");
            if(sellPointList!=null){
                sellPoint = sellPointList.get(0);
            }

            SearchItem searchItem = new SearchItem(id,title,sellPoint,Long.valueOf(price),image,categoryName);
            items.add(searchItem);
        }
        searchResult.setItemList(items);

        return searchResult;
    }
}
