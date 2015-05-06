package com.fuiou.mgr.action.sys;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.TDataDictConst;

public class IPConfigFilter implements Filter {
	
	public static Logger logger=LoggerFactory.getLogger(IPConfigFilter.class);

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) arg0;
		HttpServletResponse resp = (HttpServletResponse) arg1;
		TOperatorInf tOperatorInf = (TOperatorInf)req.getSession().getAttribute(TDataDictConst.OPERATOR_INF);//操作员
		if(tOperatorInf!=null){
			List<String> ipList = MemcacheUtil.getIPListByMchntCd(tOperatorInf.getMCHNT_CD());
			if(isContains(getIpAddr(req), ipList)){
				arg2.doFilter(req, resp);
			}else{
				// 转发到错误页面
				resp.sendRedirect(req.getContextPath()+ "/loginForwd.jsp");
			}
		}else{
			arg2.doFilter(req, resp);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

	
	private String getIpAddr(HttpServletRequest request) {  
	    String ip = request.getHeader("x-forwarded-for");  
	    if(StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("Proxy-Client-IP");  
	    }  
	    if(StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if(StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }  
	    logger.debug("remote ip :"+ip);
	    return ip;  
	}  
	
	
	private boolean isContains(String accessIp,List<String> iplist){
		for(String ip:iplist){
			if("*".equals(ip.trim())||ip.trim().equals(accessIp)){
				return true;
			}
		}
		return false;
	}
}
