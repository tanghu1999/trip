package com.th.utils;
/*
 * 返回结果工具类
 */
public class WxResult {

	private boolean success;//支付状态
	private boolean flag;
	private String message;//返回信息


	public WxResult(boolean success, String message) {
		super();
		this.success = success;
		this.flag = success;
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}


}
