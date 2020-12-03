package com.th.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.th.dao.TbContentMapper;
import com.th.entity.TbContent;
import com.th.entity.TbContentExample;
import com.th.service.ContentService;
import com.th.utils.Ad;
import com.th.utils.JedisClient;
import com.th.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper tbContentMapper;

    @Value("${ad1}")
    private String ad1;

    @Autowired
    private JedisClient jedisClient; //接口

    @Override
    public PageInfo<TbContent> findAll(String page, String limit, Long categoryId) {
        PageHelper.startPage(Integer.valueOf(page),Integer.valueOf(limit));

        TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        if(categoryId!=null){
            criteria.andCategoryIdEqualTo(categoryId);
        }

        List<TbContent> tbContents = tbContentMapper.selectByExample(tbContentExample);
        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContents);

        return pageInfo;

    }

    @Override
    public void insert(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        tbContentMapper.insert(tbContent);
    }

    //查询出所有的大广告
//    @Override
//    public List<Ad> findAllAd(Long categoryId) {
//        TbContentExample tbContentExample = new TbContentExample();
//        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
//        criteria.andCategoryIdEqualTo(categoryId);
//        List<TbContent> list = tbContentMapper.selectByExample(tbContentExample);
//
//        List<Ad> listAd = new ArrayList<>();
//        for(TbContent content:list){
//            Ad ad = new Ad();
//            ad.setHref(content.getUrl());  //点击图片跳转的链接
//            ad.setSrc(content.getPic());
//            ad.setSrcB(content.getPic2());
//            ad.setAlt(content.getSubTitle());
//            listAd.add(ad);
//        }
//
//        return listAd;
//    }

    //使用redis缓存查询出所有的大广告
    @Override
    public List<Ad> findAllAd(Long categoryId) {
        String jsonString="";
        List<TbContent> list=null;
        //判断key=ad1在redis中是否存在
        if(jedisClient.exists(ad1)) {
            //从redis缓存中得到json对象
            jsonString = jedisClient.get(ad1);
            //把json格式的字符串变为list集合
            list = JsonUtils.jsonToList(jsonString, TbContent.class);
            System.out.println(ad1);
        }else{
            System.out.println("开始访问数据库.....");
            TbContentExample tbContentExample = new TbContentExample();
            TbContentExample.Criteria criteria = tbContentExample.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            list = tbContentMapper.selectByExample(tbContentExample);
            System.out.println("访问数据库结束.....");
            //将List集合转换为json格式字符串
            jsonString=JsonUtils.objectToJson(list);
            //把数据加到redis缓存中 key-value
            jedisClient.set("ad1",jsonString);
            //设置redis缓存的生命周期
            jedisClient.expire("ad1",60*60*24);
        }

        List<Ad> listAd = new ArrayList<>();
        for(TbContent content:list){
            Ad ad = new Ad();
            ad.setHref(content.getUrl());  //点击图片跳转的链接
            ad.setSrc(content.getPic());
            ad.setSrcB(content.getPic2());
            ad.setAlt(content.getSubTitle());
            listAd.add(ad);
        }


        return listAd;
    }

    @Override
    public void delete(Long id) {
        tbContentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public TbContent findById(Long id) {
        return tbContentMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbContent tbContent) {
        tbContent.setUpdated(new Date());
        tbContentMapper.updateByPrimaryKeySelective(tbContent);
    }

    @Override
    public void delMany(int[] ids) {
        tbContentMapper.delMany(ids);
    }
}
