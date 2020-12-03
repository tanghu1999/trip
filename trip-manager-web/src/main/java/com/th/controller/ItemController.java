package com.th.controller;

import com.github.pagehelper.PageInfo;
import com.th.entity.TbItem;
import com.th.service.ItemService;
import com.th.service.ItemSolrService;
import com.th.utils.SearchItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;
    //把操作solr的对象注入进来
    @Autowired
    private ItemSolrService itemSolrService;

    @RequestMapping("list")
    @ResponseBody
    public Map list(@RequestParam(value="page",required = false,defaultValue = "1")String page,
                    @RequestParam(value="limit",required = false,defaultValue = "10")String limit,Long cid){

        PageInfo<TbItem> pageInfo = itemService.findAll(page, limit,cid);

        Map map = new HashMap();
        map.put("code",0);
        map.put("count",pageInfo.getTotal());
        map.put("msg","");
        map.put("data",pageInfo.getList());
        return map;
    }

    @RequestMapping("insert")
    @ResponseBody
    public Map insert(TbItem tbItem,String content1){
        itemService.insert(tbItem,content1);

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","增加成功");
        map.put("data",tbItem);

        return map;
    }

    @RequestMapping("addSolr")
    @ResponseBody
    public Map addSolr(){
        List<SearchItem> searchItems = itemSolrService.searchAllItemToSolr();

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","增加成功");
        map.put("data",searchItems);

        return map;
    }

    @RequestMapping("delete")
    @ResponseBody
    public Map delete(Long id){
        itemService.delete(id);

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","删除成功");

        return map;
    }

    @RequestMapping("findById")
    @ResponseBody
    public TbItem findById(Long id){
        return itemService.findById(id);
    }

    @RequestMapping("update")
    @ResponseBody
    public Map update(TbItem tbItem){
        itemService.update(tbItem);

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","修改成功");

        return map;
    }


}
