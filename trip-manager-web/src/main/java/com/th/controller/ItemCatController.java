package com.th.controller;

import com.th.service.ItemCatService;
import com.th.utils.TreePid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("itemCat")
public class ItemCatController {

    @Autowired
    private ItemCatService itemCatService;

    @RequestMapping("list")
    @ResponseBody
    public List<TreePid> list(){
        return itemCatService.findItemCats();
    }

    @RequestMapping("insert")
    @ResponseBody
    public Map insert(Long pid, String title){
        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","增加成功");
        map.put("data",itemCatService.addItemCategory(pid,title));

        return map;
    }

    @RequestMapping("update")
    @ResponseBody
    public Map update(Long id, String title){
        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","修改成功");
        map.put("data",itemCatService.updateItemCategory(id, title));

        return map;
    }


    @RequestMapping("delete")
    @ResponseBody
    public Map delete(Long id){
        itemCatService.deleteItemCategory(id);

        Map map = new HashMap();
        map.put("status",200);
        map.put("msg","删除成功");

        return map;
    }




}
