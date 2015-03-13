package com.fuiou.mgr.util.page.taglib.page;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.fuiou.mgr.util.page.ParamUtil;



/**
 * 分页框架标记
 * 从表单中获得全部参数,予以保存并自动生成对应的<input type=hidden>表单
 * 通过计算当前的页号,操作,以及控制每页记录数,提供起始记录号,和每页显示记录数,
 * 供框架内的程序控制显示起始的记录位置和数量,若中途转出后返回,直接调用该页面即可
 * 恢复原来转出时的情况.若页号超出范围,将自动调成最接近的合理值.
 * 使用举例:
 * <pre>
 *  <code>
 *    int count=getCount();//获得记录总数
 *    condition=request.getParameter("condition");
 *    <page:pager total=<%=count%> defaultPageSize='20'>
 *      <%
 *        showRecord(condition,index,pageSize);
 *      %>
 *      <page:navigator type='BUTTON'/>
 *    </page:pager>
 *  </code>
 * <pre>
 */
public class PagerTag extends BodyTagSupport {
    private static final long serialVersionUID = -6252620157460385197L;
    private StringBuffer output;        //保存输出
    private int pageNo;                 //当前页面
    private int pageSize;               //每页记录数
    private int index = 1;              //起始记录数,缺省为0
    private int pages;                  //总页数
    private boolean resume = false;     //是否是恢复操作
    private int total = 0;              //记录总数,即需要分页显示的记录总数,初始化默认为0
    public static int defaultPageSize = 15;   //缺省每页记录数,可在导航条中改变每页记录数的值

    public void setTotal(int newTotal) {
        total = newTotal;
    }

    public void setDefaultPageSize(int newDefaultPageSize) {
        defaultPageSize = newDefaultPageSize;
    }

    /**
     * 调用handler进行分页处理
     */
    @Override
	public int doStartTag() throws JspTagException {
        output = new StringBuffer();
        resume = false;
        handler();
        return EVAL_BODY_BUFFERED;
    }

    /**
     * 获得标记内的记录列表内容
     */
    @Override
	public int doAfterBody() throws JspTagException {
        BodyContent bodyContent = getBodyContent();
        if (bodyContent != null) {
            output.append(bodyContent.getString());
            try {
                bodyContent.clear();
            }
            catch (IOException ex) {
                throw new JspTagException("Fatal IO Error");
            }
        }
        return SKIP_BODY;
    }

    /**
     * 输出标记内的内容
     */
    @Override
	public int doEndTag() throws JspTagException {
        BodyContent bodyContent = getBodyContent();
        try {
            if (bodyContent != null) {
                //------------------------去除form定义----------------------------------------
//            	//输出结束标记
//                output.append("</form>\n");
                //输出全部内容
                bodyContent.getEnclosingWriter().write(output.toString());
            }
        }
        catch (IOException ex) {
            throw new JspTagException("Fatal IO Error");
        }

        return EVAL_PAGE;
    }

    /**
     * 设置标记内变量(每页记录数,当前页号,起始记录号,表单的查询字符串)
     */
    public void setVariable() {
        pageContext.setAttribute("pageSize", new Integer(pageSize));
        pageContext.setAttribute("pageNo", new Integer(pageNo));
        pageContext.setAttribute("total", new Integer(total));
        pageContext.setAttribute("pages", new Integer(pages));
        pageContext.setAttribute("index", new Integer(index));
    }

    /**
     * 获取分页信息和其它表单中的变量值并输出表单
     */
    private void handler() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        StringBuffer paramBuf = new StringBuffer();
        HttpSession session = pageContext.getSession();
        String choice = "";
        HashMap params = new HashMap();

        //获得当前页的URI
//        String requestURI = request.getRequestURI();

        //获取操作
        choice = ParamUtil.getParameter(request, "choice");

        //判断是否转出后返回需恢复原分页及参数信息(判断依据：GET方式访问且没有提供额外参数)
        if (choice.equals("") && request.getMethod().equals("GET") && request.getQueryString() == null) {
            //是否页面相同
            String pageURI = (String) session.getAttribute("pageURI");
            if (request.getRequestURI().equalsIgnoreCase(pageURI))
                resume = true;//true
            else
                resume = false;
        }

        if (resume) { //处理中途转出后返回的情况
            //从session中取参数
            Object obj = session.getAttribute("pagerParameters");
            if (obj != null) {
                params = (HashMap) obj;

                //获取每页记录数,若没指定,使用默认值
                pageSize = ((Integer) params.get("pageSize")).intValue();

                //获取页号
                pageNo = ((Integer) params.get("pageNo")).intValue();
            }
            else {
                //提供默认值(一般出在用GET方法查询且不需参数情况的处理,因为条件等同于转出后恢复)
                pageSize = defaultPageSize;
                pageNo = 1;
            }

            //设定操作
            choice = "current"; //操作改设为取当前页
        }
        else {
            //获取每页记录数,若没指定,使用默认值
            pageSize = ParamUtil.getIntParameter(request, "pageSize", defaultPageSize);

            //获取页号
            pageNo = ParamUtil.getIntParameter(request, "pageNo", 1);
        }

        //每页记录数越界处理
        if (pageSize <= 0)
            pageSize = 1;

        //计算总页数
        pages = (total % pageSize == 0) ? total / pageSize : total / pageSize + 1;

        //根据操作,重新确定当前页号
        if (choice.equals("next"))
            pageNo++;
        if (choice.equals("prev"))
            pageNo--;
        if (choice.equals("first"))
            pageNo = 1;
        if (choice.equals("last"))
            pageNo = pages;

        //页号越界处理
        if (pageNo > pages)
            pageNo = pages;
        if (pageNo <= 0)
            pageNo = 1;

        //起始记录号
        index = (pageNo - 1) * pageSize + 1;

        //------------------------去除form定义----------------------------------------
//        //输出表单的头
//        output.append("<form action='' method='post' name='pager'>\n");

        Enumeration enumTmp;
        String name;
        String value;
        if (resume) { //处理中途转出后返回的情况
            //恢复session中的参数
            Collection co = params.entrySet();
            if (co != null) {
                Iterator it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry e = (Map.Entry) it.next();
                    name = (String) e.getKey();
                    if (name.equals("pageNo") || name.equals("pageSize") || name.equals("choice")) { //略过
                        continue;
                    }
                    //如果是"_h"结尾的 ------- 20071106 allen ----------------
                    if(name != null ){
            			int index = name.indexOf("_h");
            			if( index !=-1 && index == name.length()-2)
            				continue;
                    }
                    value = (String) e.getValue();
                    paramBuf.append("<input type='hidden'  name='" + name + "' value='" + value +
                                    "'>\n");
                }
            }
        }
        else {
            //获取所有提交的参数,并设置表单"<input type='hidden' name='' value=''>"
            enumTmp = request.getParameterNames();

            while (enumTmp.hasMoreElements()) {
                name = (String) enumTmp.nextElement();
                value = ParamUtil.getParameter(request, name);
                //保存查询参数和值
                params.put(name, value);

                if (name.equals("pageNo") || name.equals("pageSize") || name.equals("choice")) {
                    continue;
                }
                
                //如果是"_h"结尾的 ------- 20071106 allen ---------------- 
                if(name != null ){
        			int index = name.indexOf("_h");
        			if( index !=-1 && index == name.length()-2)
        				continue;
                }
                paramBuf.append("<input type='hidden'  name='" + name + "' value='" + value +
                                 "'>\n");
            }

            //保存分页参数和值
            params.put("pageNo", new Integer(pageNo));
            params.put("pageSize", new Integer(pageSize));
            params.put("index", new Integer(index));
            
            //保存到session
            session.setAttribute("pagerParameters", params);

            //保存当前的URI
            session.setAttribute("pageURI", request.getRequestURI());
        } ////////end of if(resume)

        //输出分页参数表单
        paramBuf.append("<input type='hidden'  name='pageNo' value='" + pageNo + "'>\n");
        paramBuf.append("<input type='hidden'  name='pageSize' value='" + pageSize + "'>\n");
        paramBuf.append("<input type='hidden'  name='choice' value='" + choice + "'>\n");
        paramBuf.append("<input type='hidden'  name='totleSize' value='" + total + "'>\n");
        output.append(paramBuf.toString());
        setVariable();
        if (resume) {
            //转出恢复情况下需重新提交表单
            String resend = "<script language='javascript'>"
                            + "this.form.submit();"
                            + "</script>\n";
            output.append(resend);
        }
    }
}
