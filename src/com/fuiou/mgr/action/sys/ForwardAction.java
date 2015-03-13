package com.fuiou.mgr.action.sys;

import com.opensymphony.xwork2.ActionSupport;


public class ForwardAction extends ActionSupport {
	private String toJSP;
	public String execute(){
		return "forwardJSP";
	}
	public String getToJSP() {
		return toJSP;
	}
	public void setToJSP(String toJSP) {
		this.toJSP = toJSP;
	}

}
