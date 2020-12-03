package com.th.controller;

import com.alibaba.fastjson.JSON;
import com.th.service.ContentService;
import com.th.utils.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ContentService contentService;

    @RequestMapping("index")
    public String index(Model model){
        List<Ad> listAd = contentService.findAllAd(89L);
        //Object JSON.toJSON(Object obj):将list集合变为json格式的字符串
        String adJson =  JSON.toJSON(listAd).toString();
        model.addAttribute("ad1",adJson);

        return "index";
    }




}
