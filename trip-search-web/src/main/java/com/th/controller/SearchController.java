package com.th.controller;

import com.th.service.ItemSolrService;
import com.th.utils.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private ItemSolrService itemSolrService;

    @RequestMapping("search")
    public String search(String q,
                       @RequestParam(value="page",required = false,defaultValue = "1") String page,
                       Model model) throws Exception{

        //public String(byte[] bytes,Charset charset)构造一个新的String由指定用指定的字节的数组解码charset
        String queryParam= new String(q.getBytes("iso-8859-1"),"UTF-8");

        //根据q的值来查询solr
        SearchResult searchResult = itemSolrService.queryItemFromSolr(queryParam, page);

        model.addAttribute("query",queryParam);
        model.addAttribute("page",page);
        model.addAttribute("totalPages", searchResult.getTotalPages());
        model.addAttribute("itemList",searchResult.getItemList());

        return "search";

    }

}
