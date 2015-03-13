package com.fuiou.mgr.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class CheckURLInterceptor  extends AbstractInterceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(CheckURLInterceptor.class);

	@SuppressWarnings({ "unused" })
	@Override
	public String intercept(ActionInvocation ai) throws Exception {
		// TODO Auto-generated method stub
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
		String urlString = request.getRequestURI();
		logger.info("RemoteAddr:"+request.getRemoteAddr()+" 开始执行:"+urlString);
		long bt = System.currentTimeMillis();
		String result = null;
		try {
			result = ai.invoke();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "系统异常");
			result = "error";
		}
		long et = System.currentTimeMillis();
		logger.info(urlString+"执行完毕! 耗时："+(et-bt));
		return result;
	} 
	
}
