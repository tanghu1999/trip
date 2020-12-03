package com.th.service;

import com.th.utils.SearchItem;
import com.th.utils.SearchResult;

import java.util.List;

public interface ItemSolrService {
    //读取tbItem表中所有的数据写到solr中
    List<SearchItem> searchAllItemToSolr();

    //通过搜索框输入的值，再solr中得到对应的记录
    SearchResult queryItemFromSolr(String q,String page) throws Exception;

}
