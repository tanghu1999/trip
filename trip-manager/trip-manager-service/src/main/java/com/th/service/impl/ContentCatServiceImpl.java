package com.th.service.impl;

import com.th.dao.TbContentCategoryMapper;
import com.th.dao.TbContentMapper;
import com.th.entity.TbContentCategory;
import com.th.service.ContentCatService;
import com.th.service.ContentService;
import com.th.utils.TreePid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentCatServiceImpl implements ContentCatService {

    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;


    @Override
    public List<TreePid> findContentCats() {
        return tbContentCategoryMapper.findContentCats();
    }

    @Override
    public TbContentCategory addContentCategory(Long pid, String title) {
        TbContentCategory tbContentCategory = new TbContentCategory(pid,title,1,1,false,new Date(), new Date());
        tbContentCategoryMapper.insertSelective(tbContentCategory);

        return tbContentCategory;
    }

    @Override
    public TbContentCategory updateContentCategory(Long id, String title) {
        TbContentCategory tbContentCategory = new TbContentCategory(id,title,new Date());
        tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);

        return tbContentCategory;
    }

    @Override
    public void deleteContentCategory(Long id) {
        tbContentCategoryMapper.deleteByPrimaryKey(id);
    }
}
