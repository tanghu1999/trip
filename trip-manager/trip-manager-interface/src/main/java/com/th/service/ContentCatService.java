package com.th.service;

import com.th.entity.TbContentCategory;
import com.th.utils.TreePid;

import java.util.List;

public interface ContentCatService {

    List<TreePid> findContentCats();

    TbContentCategory addContentCategory(Long pid,String title);

    TbContentCategory updateContentCategory(Long pid,String title);

    void deleteContentCategory(Long id);

}
