package com.fuiou.mgr.util.page.taglib.page;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * description:com.chinapay.newgms.util.page.taglib.page.CalendarTag
 * 2008-10-21 9:28:04
 *
 * Chaos
 * @version 1.0.0
 * @serial
 * @since JDK 1.5.0
 */
public class CalendarTag extends TagSupport {

    private String formName;
    private String name;
    private String styleClass;
    private String value;
    private String formatType;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    @Override
	public int doStartTag() throws JspException {
        try {
            String bar = getCalendarBar(name, styleClass, value, formatType);
            pageContext.getOut().write(bar);
            return SKIP_BODY;
        }
        catch (IOException ioe) {
            throw new JspException(ioe.getMessage());
        }
    }

    @Override
	public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    private String getCalendarBar(String name, String styleClass, String value, String formatType) {
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"").append(request.getContextPath()).append("/styles/calendar.css\"/>\n");
        buffer.append("<script type=\"text/javascript\" src=\"").append(request.getContextPath()).append("/js/calendar.js\"").append("></script>\n");
        buffer.append("<script type=\"text/javascript\" src=\"").append(request.getContextPath()).append("/js/calendar-zh.js\"").append("></script>\n");
        buffer.append("<script type=\"text/javascript\" src=\"").append(request.getContextPath()).append("/js/calendar-setup.js\"").append("></script>\n");
        buffer.append("<script type=\"text/javascript\" src=\"").append(request.getContextPath()).append("/js/prototype.js\"").append("></script>\n");
        buffer.append("<input type=\"text\" name=\"").append(name).append("\" id=\"").append(name).append("\" ");
        if (styleClass != null) {
            buffer.append(" class=\"").append(styleClass).append("\" ");
        }
        if (value != null) {
            buffer.append(" value=\"").append(value).append("\" ");
        }
        String formStr = formName==null?"":",'"+formName+"'";
        buffer.append(" onclick=\"return showCalendar('").append(name).append("','").append(formatType).append("'").append(formStr).append(");\" /><input value=\"\" ");
        buffer.append(" style=\"background:url(").append(request.getContextPath()).append("/images/datepic.png) no-repeat; width:20px; height:20px; border:0px;cursor:pointer;\" ");
        buffer.append(" onclick=\"return showCalendar('").append(name).append("','").append(formatType).append("'").append(formStr).append(");\" />\n");
        return buffer.toString();
    }
}
