package com.th.service.impl;

import com.th.dao.TbItemCatMapper;
import com.th.entity.TbItemCat;
import com.th.service.ItemCatService;
import com.th.utils.TreePid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Override
    public List<TreePid> findItemCats() {
        return tbItemCatMapper.findItemCats();
    }

    @Override
    public TbItemCat addItemCategory(Long pid, String title) {
        TbItemCat tbItemCat = new TbItemCat(pid,title,1,4,false,new Date(),new Date());
        tbItemCatMapper.insertSelective(tbItemCat);

        return tbItemCat;
    }

    @Override
    public TbItemCat updateItemCategory(Long id, String title) {
        TbItemCat tbItemCat = new TbItemCat(id,title,new Date());
        tbItemCatMapper.updateByPrimaryKeySelective(tbItemCat);

        return tbItemCat;
    }

    @Override
    public void deleteItemCategory(Long id) {
        tbItemCatMapper.deleteByPrimaryKey(id);

    }
}
