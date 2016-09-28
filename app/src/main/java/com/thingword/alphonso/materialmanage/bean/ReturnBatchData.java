package com.thingword.alphonso.materialmanage.bean;

import java.util.List;

public class ReturnBatchData<T> {
	private String return_msg;
	private String return_code;
	private List<BatchData<T>> data;
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public List<BatchData<T>> getData() {
		return data;
	}
	public void setData(List<BatchData<T>> data) {
		this.data = data;
	}


}
