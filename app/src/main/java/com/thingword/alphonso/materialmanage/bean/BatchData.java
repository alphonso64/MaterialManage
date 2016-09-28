package com.thingword.alphonso.materialmanage.bean;

import java.util.List;

public class BatchData<T> {
	private String batch;
	private List<T> data;
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
}
