package com.fuiou.mgr.action;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport {

	private static final long serialVersionUID = 1652679742176081614L;
	
	protected ActionContext context = ActionContext.getContext();
	protected HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
	protected HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
	protected HttpSession session = request.getSession();
	protected TInsMchntInf tInsInf = (TInsMchntInf) session.getAttribute(TDataDictConst.INS_INF);
	protected TOperatorInf tOperatorInf = (TOperatorInf) session.getAttribute(TDataDictConst.OPERATOR_INF);
	protected ResourceBundle rb = (ResourceBundle) context.getSession().get("resourceBundle");
	
}
