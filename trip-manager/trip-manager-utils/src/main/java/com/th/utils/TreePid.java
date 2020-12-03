package com.th.utils;

import java.io.Serializable;

public class TreePid implements Serializable {
    private Integer id;
    private String title;
    private Integer pid;

    public TreePid(){}
    public TreePid(Integer id, String title, Integer pid) {
        this.id = id;
        this.title = title;
        this.pid = pid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }
}
