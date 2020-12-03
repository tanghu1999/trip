package com.th.service;

import com.th.entity.TbItemCat;
import com.th.utils.TreePid;

import java.util.List;

public interface ItemCatService {

    List<TreePid> findItemCats();

    TbItemCat addItemCategory(Long pid, String title);

    TbItemCat updateItemCategory(Long pid,String title);

    void deleteItemCategory(Long id);


}
