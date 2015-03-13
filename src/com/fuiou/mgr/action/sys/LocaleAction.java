package com.fuiou.mgr.action.sys;

import java.util.Locale;
import java.util.ResourceBundle;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LocaleAction extends ActionSupport {

	private static final long serialVersionUID = -5175928566919588288L;
	
	private String request_locale;
	public String getRequest_locale() {
		return request_locale;
	}
	public void setRequest_locale(String request_locale) {
		this.request_locale = request_locale;
	}


	@Override
	public String execute() throws Exception {
		String[] strs = request_locale.split("_");
		Locale locale = new Locale(strs[0], strs[1]);
		ResourceBundle rb = ResourceBundle.getBundle("messageResource", locale);
		ActionContext.getContext().getSession().put("WW-TRANS-I18N-LOCALE", locale);
		ActionContext.getContext().getSession().put("resourceBundle", rb);
		return SUCCESS;
	}
	
	
}
