package com.fuiou.mgr.action.sys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LogoutAction extends ActionSupport {

	public String execute() throws Exception {
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		HttpSession session = request.getSession();
		session.removeAttribute(TDataDictConst.MENU_LIST);
		session.removeAttribute(TDataDictConst.OPERATOR_INF);
		session.removeAttribute(TDataDictConst.INS_INF);
		session.invalidate();
		return Action.SUCCESS;
	}
}
