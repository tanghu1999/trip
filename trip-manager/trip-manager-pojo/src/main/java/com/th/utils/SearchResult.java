package com.th.utils;

import java.io.Serializable;
import java.util.List;

public class SearchResult implements Serializable{
	//从solr中得到记录
	private static final long serialVersionUID = 1L;
	private long count;		//总的信息条数
	private long totalPages;	//总的页数(是根据总的信息条数和每页显示的信息条数计算而来)
	private List<SearchItem> itemList;


	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public List<SearchItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<SearchItem> itemList) {
		this.itemList = itemList;
	}
	
	
}
