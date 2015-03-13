package com.fuiou.mgr.action.sys;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
public class DoNothingAction extends ActionSupport {
	private String forwardAction;
	private String frame;
	public String execute(){
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		String[] param = forwardAction.split("\\|");
		String src=param[0].trim();
		if(param.length>1) frame=param[1].trim().split("=")[1];
		if(null==frame || "".equals(frame)){//没有指定则采用默认的frame页面
			setFrame("/WEB-INF/pages/stru/StruDefault.jsp");
		}
		if(!src.contains("action")){
			src="toJSP.action?toJSP="+src;
		}
		request.setAttribute("src", src);
		return "redirectURL";
	}
	public String incomfor(){
		return this.execute();
	}
	public String sPayforReq(){
		return this.execute();
	}
	public String sPayforReview(){
		return this.execute();
	}
	public String sPayforFileQuery(){
		return this.execute();
	}
	public String sUploadFile(){
		return this.execute();
	}
	public String getForwardAction() {
		return forwardAction;
	}
	public void setForwardAction(String forwardAction) {
		this.forwardAction = forwardAction;
	}
	public String getFrame() {
		return frame;
	}
	public void setFrame(String frame) {
		this.frame = frame;
	}
}
