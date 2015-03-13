package com.fuiou.mgr.util.page.taglib.page;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * HiddenFormDataTag.java
 * 
 * 隐藏从Session属性中取出的对象的属性值
 * 
 * 注意： Session中取出的对象的属性必需要有相对应的get方法。
 * 
 * 如果属性没有对应的get方法可将该属性放入忽略属性中
 * 
 * liu.wei
 * @version $Revision: 1.0 $ $Date: $
 * @serial
 * @since 2010-5-18 上午10:37:07
 */
public class HiddenFormDataTag extends BodyTagSupport {

	private StringBuffer sb;
	// /Session中属性的值
	private String sessionAttr;
	// ///Session中对象需要忽略隐藏的属性,多个属性之间可以用,隔开。
	private String ignoreAttrs = "";

	public String getSessionAttr() {
		return this.sessionAttr;
	}

	public void setSessionAttr(String sessionAttr) {
		this.sessionAttr = sessionAttr;
	}

	public String getIgnoreAttrs() {
		return this.ignoreAttrs;
	}

	public void setIgnoreAttrs(String ignoreAttrs) {
		this.ignoreAttrs = ignoreAttrs;
	}

	@Override
	public int doEndTag() throws JspException {
		BodyContent bodyContent = this.getBodyContent();
		Writer out = this.pageContext.getOut();
		try {
			out.write(this.sb.toString());
		} catch (IOException ex) {
			throw new JspTagException("Fatal IO Error");
		}
		return EVAL_PAGE;
	}

	@Override
	public int doAfterBody() throws JspTagException {
		BodyContent bodyContent = this.getBodyContent();
		if (bodyContent != null) {
			this.sb.append(bodyContent.getString());
			try {
				bodyContent.clear();
			} catch (IOException ex) {
				throw new JspTagException("Fatal IO Error");
			}
		}
		return SKIP_BODY;
	}

	@Override
	public int doStartTag() throws JspException {
		HttpSession session = this.pageContext.getSession();
		this.sb = new StringBuffer();
		Object obj = session.getAttribute(this.getSessionAttr());
		Field[] field = obj.getClass().getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			String fieldName = field[i].getName();
			if (this.isSikpAttr(fieldName)) {
				continue;
			}
			String firstStr = fieldName.substring(0, 1);
			String elseStr = fieldName.substring(1);
			String methodName = "get" + firstStr.toUpperCase() + elseStr;
			try {
				Method method = obj.getClass().getMethod(methodName, null);
				String value = (String) method.invoke(obj, null);
				this.sb.append("<input  type=\"hidden\" name=\"" + fieldName
						+ "\"  value=\"" + value + "\" />");
				this.sb.append("\r\n");
			} catch (SecurityException e) {
				throw new JspException("SecurityException ");
			} catch (NoSuchMethodException e) {
				throw new JspException("NoSuchMethodException class "
						+ obj.getClass() + " is no " + methodName + " method");
			} catch (IllegalArgumentException e) {
				throw new JspException("IllegalArgumentException  class="
						+ obj.getClass() + " method=" + methodName);
			} catch (IllegalAccessException e) {
				throw new JspException("IllegalAccessException  class="
						+ obj.getClass() + " method=" + methodName);
			} catch (InvocationTargetException e) {
				throw new JspException("InvocationTargetException class="
						+ obj.getClass() + " method=" + methodName);
			}
		}
		return EVAL_BODY_BUFFERED;
	}

	private boolean isSikpAttr(String attr) {
		if (this.ignoreAttrs == null) {
			this.ignoreAttrs = "";
		}
		String[] attrs = this.ignoreAttrs.split(",");
		for (int i = 0; i < attrs.length; i++) {
			if (attrs[i].equals(attr)) {
				return true;
			}
		}
		return false;
	}
}
