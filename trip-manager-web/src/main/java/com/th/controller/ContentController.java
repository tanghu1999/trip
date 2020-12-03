package com.th.controller;

import com.github.pagehelper.PageInfo;
import com.th.entity.TbContent;
import com.th.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @RequestMapping("list")
    @ResponseBody
    public Map list(@RequestParam(value="page",required = false,defaultValue = "1")String page,
                    @RequestParam(value="limit",required = false,defaultValue = "10")String limit,Long categoryId){
        Map map = new HashMap();
       PageInfo<TbContent> pageInfo = contentService.findAll(page, limit, categoryId);

        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data",pageInfo.getList());

        return map;
    }

    @RequestMapping("insert")
    @ResponseBody
    public Map insert(TbContent tbContent){
        contentService.insert(tbContent);

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","添加成功");
        map.put("data",tbContent);

        return map;
    }

    @RequestMapping("findById")
    @ResponseBody
    public TbContent findById(Long id){

        return contentService.findById(id);
    }


    @RequestMapping("update")
    @ResponseBody
    public Map update(TbContent tbContent){
        contentService.update(tbContent);

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","修改成功");

        return map;
    }

    @RequestMapping("delete")
    @ResponseBody
    public Map delete(Long id){
        contentService.delete(id);

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","删除成功");

        return map;
    }




}
