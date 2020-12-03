package com.th.utils;

import java.io.Serializable;

public class SearchItem implements Serializable{
	//用于与solr中的字段对应，将数据存进solr中
	private static final long serialVersionUID = 1L;
	private String id;
	private String title;
	private String sellPoint;
	private long price;
	private String image;
	private String categoryName;

	public SearchItem(){}
	public SearchItem(String id, String title, String sellPoint, long price, String image, String categoryName) {
		this.id = id;
		this.title = title;
		this.sellPoint = sellPoint;
		this.price = price;
		this.image = image;
		this.categoryName = categoryName;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSellPoint() {
		return sellPoint;
	}
	public void setSellPoint(String sellPoint) {
		this.sellPoint = sellPoint;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}



}
