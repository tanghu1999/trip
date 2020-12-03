package com.th.utils;

import com.th.entity.TbItem;

import java.io.Serializable;

public class Item extends TbItem implements Serializable {
    public Item(TbItem tbItem){
        this.setId(tbItem.getId());
        this.setTitle(tbItem.getTitle());
        this.setSellPoint(tbItem.getSellPoint());
        this.setPrice(tbItem.getPrice());
        this.setImage(tbItem.getImage());
        this.setNum(tbItem.getNum());
        this.setBarcode(tbItem.getBarcode());
        this.setStatus(tbItem.getStatus());
        this.setCreated(tbItem.getCreated());
        this.setUpdated(tbItem.getUpdated());
        this.setCid(tbItem.getCid());

    }

    public String[] getImages(){
        return this.getImage().split(",");

    }



}
