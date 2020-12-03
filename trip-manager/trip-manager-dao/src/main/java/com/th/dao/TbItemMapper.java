package com.th.dao;

import com.th.entity.TbItem;
import com.th.entity.TbItemExample;
import com.th.utils.SearchItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbItemMapper {
    long countByExample(TbItemExample example);

    int deleteByExample(TbItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbItem record);

    int insertSelective(TbItem record);

    List<TbItem> selectByExample(TbItemExample example);

    TbItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbItem record, @Param("example") TbItemExample example);

    int updateByExample(@Param("record") TbItem record, @Param("example") TbItemExample example);

    int updateByPrimaryKeySelective(TbItem record);

    int updateByPrimaryKey(TbItem record);

    //查询所有商品信息，增加到solr索引库
    List<SearchItem> searchAll();

    //通过id查询的到对应的SearchItem对象(封装TbItem类的部分字段)
    SearchItem searchItemById(Long itemId);

    void delMany(int[] ids);
}