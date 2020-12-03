package com.th.service.impl;

import com.th.dao.TbItemDescMapper;
import com.th.entity.TbItemDesc;
import com.th.service.ItemDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemDescServiceImpl implements ItemDescService {

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Override
    public TbItemDesc findById(Long itemId) {
        return tbItemDescMapper.selectByPrimaryKey(itemId);
    }
}
