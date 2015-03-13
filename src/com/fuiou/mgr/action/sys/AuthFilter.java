package com.fuiou.mgr.action.sys;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.util.TDataDictConst;

/**
 * Servlet Filter implementation class AuthFilter
 */
public class AuthFilter implements Filter {
	private String errorPage = "/loginForwd.jsp";// 错误页面
	@SuppressWarnings("rawtypes")
    private Set ignoreSet = new HashSet();

	/**
	 * Default constructor.
	 */
	public AuthFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		if (!this.validAuth(req)) {
			// 转发到错误页面
			resp.sendRedirect(req.getContextPath()+ errorPage);
		} else {
			// 过滤器放过
			chain.doFilter(req, resp);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException {
		String ignore = filterConfig.getInitParameter("entitleIgnorePath");
		if (!"".equals(ignore) && null!=ignore) {
			String[] paths = ignore.split("[,]", -1);
			for (int i = 0; paths != null && i < paths.length; i++) {
				this.ignoreSet.add(paths[i].trim());
			}
		}
	}

	private boolean validAuth(HttpServletRequest req) {
		String url = req.getServletPath();
		if (this.ignoreSet.contains(url)) {
			return true;
		}
		// 用户的权限
		@SuppressWarnings("rawtypes")
        List menuList = (List) req.getSession().getAttribute(TDataDictConst.MENU_LIST);
		if (menuList == null || menuList.size() < 1) {
			req.setAttribute("msg", "对不起，您没有权限访问");
			return false;
		}
		TOperatorInf tOperatorInf = (TOperatorInf)req.getSession().getAttribute(TDataDictConst.OPERATOR_INF);
		TInsMchntInf tInsInf = (TInsMchntInf)req.getSession().getAttribute(TDataDictConst.INS_INF);
		if(tOperatorInf == null || tInsInf == null){
		    req.setAttribute("msg", "对不起，会话已失效，请重新登录");
            return false;
		}
		return true;
	}
}
