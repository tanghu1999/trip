package com.th.service;

import com.github.pagehelper.PageInfo;
import com.th.entity.TbItem;

import java.util.List;


public interface ItemService {

    //查询出TbItem表中所有数据
    public PageInfo<TbItem> findAll(String page, String limit, Long cid);
    //增加一个商品
    public void insert(TbItem tbItem,String content1);
    //根据商品编号查询出商品
    TbItem findById(Long itemId);

    void delete(Long id);

    void update(TbItem tbItem);

    void delMany(int[] ids);



}
