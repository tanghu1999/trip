package com.th.service;

import com.github.pagehelper.PageInfo;
import com.th.entity.TbContent;
import com.th.utils.Ad;

import java.util.List;

public interface ContentService {

    public PageInfo<TbContent> findAll(String page, String limit, Long categoryId);

    public void insert(TbContent tbContent);

    //查询出所有的大广告
    public List<Ad> findAllAd(Long categoryId);

    void delete(Long id);

    TbContent findById(Long id);

    void update(TbContent tbContent);

    void delMany(int[] ids);


}
