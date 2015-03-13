package com.fuiou.mgr.util.page.taglib.page;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.fuiou.mgr.util.page.StringUtil;

/**
 * 显示导航条,可利用此改变每页的记录数,上下翻页和跳页
 * 使用方法:<page:navigator type='BUTTON'/>
 * Starboy
 * @version 2.0
 */
public class NavigatorTag extends TagSupport {
	private static final long serialVersionUID = 4650550290421073438L;
	/**导航条的类型(BUTTON/TEXT)(按钮型/文字链接型)*/
    private String type = "TEXT"; //选择导航条类型默认"BUTTON"(BUTTON/TEXT)

    public void setType(String newType) {
        type = newType;
    }

    @Override
	public int doStartTag() throws JspException {
        try {
            String bar = getNavigatorBar(type);
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

    /**
     * 根据指定类型获得导航条预先设计的导航条
     * @param type 导航条类型(BUTTON/TEXT)
     * @return 返回导航条的HTML代码,若指定类型不存在,返回""
     */
    private String getNavigatorBar(String type) {
    	HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
    	HttpSession session = request.getSession();
    	ResourceBundle rb = (ResourceBundle) session.getAttribute("resourceBundle");
        String bar = "";
        String pageNo   = ((Integer) pageContext.getAttribute("pageNo")).toString();
        String pages    = ((Integer) pageContext.getAttribute("pages")).toString();
        String total    = ((Integer) pageContext.getAttribute("total")).toString();
        String pageSize = ((Integer) pageContext.getAttribute("pageSize")).toString();
        String nextDisabled = "";
        String prevDisabled = "";
        if (Integer.parseInt(pageNo) >= Integer.parseInt(pages))
            nextDisabled = "disabled";
        if (Integer.parseInt(pageNo) <= 1)
            prevDisabled = "disabled";

        //---------------------按钮型的导航条-----------------------//
     /*   if (type.equalsIgnoreCase("BUTTON")) {
            String pageSizeInput = "<input type='text' size='2' value='" + pageSize + "' "
		            + "onChange=\"javascript:this.form.choice.value='current';"
		            + "this.form.pageSize.value=this.value;return true;\">";
            
			String firstButton = "<input type='button' class='type_button' value='"
					+ rb.getString("global.pageQuery.first")
					+"' " + prevDisabled + " "
					+ "onClick=\"javascript:this.form.choice.value='first';this.form.submit();\">";
			String prevButton = "<input type='button' class='type_button' value='"
					+ rb.getString("global.pageQuery.prev")
	                + "' " + prevDisabled + " "
					+ "onClick=\"javascript:this.form.choice.value='prev';this.form.submit();\">";
			String nextButton = "<input type='button' class='type_button' value='"
                    + rb.getString("global.pageQuery.next")
                    + "' " + nextDisabled + " "
					+ "onClick=\"javascript:this.form.choice.value='next';this.form.submit();\">";
			String lastButton = "<input type='button' class='type_button' value='"
                    + rb.getString("global.pageQuery.last")
                    + "' " + nextDisabled + " "
					+ "onClick=\"javascript:this.form.choice.value='last';this.form.submit();\">";
			String pageNoInput = "<input type='text' size='3' value='" + pageNo + "' "
					+ "onChange=\"javascript:this.form.pageNo.value=this.value\">";
			
            bar =   rb.getString("eachPage")+" pageSize "+rb.getString("size")+" | \n"
		            + rb.getString("totalPage") +":pages/"+rb.getString("totalRow")+":total"+" | \n"
		            + "first \n prev \n next \n last \n | "+rb.getString("go")+" pageNo"+"\n"
		            +
		              " <input type='submit' value='GO' class='type_button' onClick=\"javascript:this.form.choice.value='current';\">\n";
			
			bar = StringUtil.replace(bar, "pageSize", pageSizeInput);
			bar = StringUtil.replace(bar, "pages", pages);
			bar = StringUtil.replace(bar, "total", total);
			bar = StringUtil.replace(bar, "first", firstButton);
			bar = StringUtil.replace(bar, "prev", prevButton);
			bar = StringUtil.replace(bar, "next", nextButton);
			bar = StringUtil.replace(bar, "last", lastButton);
			bar = StringUtil.replace(bar, "pageNo", pageNoInput);
        }  */

        //-------------------------文字型----------------------------//
        if (type.equalsIgnoreCase("TEXT")) {
        	String formPath = "this.parentNode.childNodes[1].form";
        	
            StringBuffer tPageSizeInputStrBuff = new StringBuffer();
            tPageSizeInputStrBuff.append("<input type='text' size='2' value='"                                                      );
            tPageSizeInputStrBuff.append(pageSize                                                                                   );
            tPageSizeInputStrBuff.append("' maxLength='2' "                                                                         );
            tPageSizeInputStrBuff.append("onChange=\""+formPath+".choice.value='current';"                                        );
            tPageSizeInputStrBuff.append(""+formPath+".pageSize.value=this.value;"                                                     );
            tPageSizeInputStrBuff.append("if(this.value==null || this.value=='') "+formPath+".pageSize.value = 20 ;"                   );
            tPageSizeInputStrBuff.append("else{"                                                                                    );
            tPageSizeInputStrBuff.append(" for(var i=0; i<this.value.length; i++){"                                                 );
            tPageSizeInputStrBuff.append("  var ch = this.value.charAt(i);"                                                         );
            tPageSizeInputStrBuff.append("   if(ch == '0' && i==0){ "                                                               );
            tPageSizeInputStrBuff.append("       alert('"+"The Transaction Records That Each Page Displays Should be Positive Integer"+"');"  );
            tPageSizeInputStrBuff.append("       return false;"                                                                     );
            tPageSizeInputStrBuff.append("   }"                                                                                     );
            tPageSizeInputStrBuff.append("  if(ch < '0' || ch > '9'){ "                                                             );
            tPageSizeInputStrBuff.append("      alert('"+"The Transaction Records That Each Page Displays Should be Positive Integer"+"');"   );
            tPageSizeInputStrBuff.append("      return false;"                                                                      );
            tPageSizeInputStrBuff.append("  }"                                                                                      );
            tPageSizeInputStrBuff.append("}"                                                                                        );
            tPageSizeInputStrBuff.append("if(this.value > 20) {alert('"
            		+"The Transaction Records that Each Page Displays are NOT More Than 20"
            		+"');return false;}else{"+formPath+".pageSize.value=this.value;}}\">");            
            String pageSizeInput = new String(tPageSizeInputStrBuff);
                                   
            String firstText = rb.getString("global.pageQuery.first");
            String prevText = rb.getString("global.pageQuery.prev");
            String nextText = rb.getString("global.pageQuery.next");
            String lastText = rb.getString("global.pageQuery.last");
            if (prevDisabled.equalsIgnoreCase("")) {
                firstText = "<a href='first' "
                            +
                        "onClick=\"javascript:"+formPath+".choice.value='first';"+formPath+".submit();return false;\">"
                            + rb.getString("global.pageQuery.first")
                            + "</a>";
                prevText = "<a href='prev' "
                           +
                        "onClick=\"javascript:"+formPath+".choice.value='prev';"+formPath+".submit();return false;\">"
                           + rb.getString("global.pageQuery.prev")
                           + "</a>";
            }
            if (nextDisabled.equalsIgnoreCase("")) {
                nextText = "<a href='next' "
                           +
                        "onClick=\"javascript:"+formPath+".choice.value='next';"+formPath+".submit();return false;\">"
                           + rb.getString("global.pageQuery.next")
                           + "</a>";
                lastText = "<a href='last' "
                           +
                        "onClick=\"javascript:"+formPath+".choice.value='last';"+formPath+".submit();return false;\">"
                           + rb.getString("global.pageQuery.last")
                           + "</a>";
            }

            StringBuffer tPageNoInputStrBuff = new StringBuffer();
            tPageNoInputStrBuff.append("<input type='text' size='3' size='2' value='"                            );
            tPageNoInputStrBuff.append(pageNo                                                                    );
            tPageNoInputStrBuff.append("' "                                                                      );
            tPageNoInputStrBuff.append("onChange=\"javascript:"+formPath+".pageNo.value=this.value;"                );
            tPageNoInputStrBuff.append("if(this.value == null || this.value == '') "+formPath+".pageNo.value='1';"  );
            tPageNoInputStrBuff.append("else{"                                                                   );
            tPageNoInputStrBuff.append("   for(var i=0; i<this.value.length;i++){"                               );
            tPageNoInputStrBuff.append("       var ch = this.value.charAt(i);"                                   );
            tPageNoInputStrBuff.append("       if( ch =='0' && i==0){"                                           );
            tPageNoInputStrBuff.append("           alert('"+"countMustBePositive"+"');");
            tPageNoInputStrBuff.append("           return false;"                                                );
            tPageNoInputStrBuff.append("       }"                                                                );
            tPageNoInputStrBuff.append("       if( ch <'0' || ch > '9'){"                                        );
            tPageNoInputStrBuff.append("           alert('"+"countMustBePositive"+"');");
            tPageNoInputStrBuff.append("           return false;"                                                );
            tPageNoInputStrBuff.append("       }"                                                                );
            tPageNoInputStrBuff.append("   }"                                                                    );
            tPageNoInputStrBuff.append("}\">"                                                                    );
            String pageNoInput = new String(tPageNoInputStrBuff);

            bar = rb.getString("global.pageQuery.eachPage")+" _pageSize_ "+rb.getString("global.pageQuery.size")+" | \n"
                  +rb.getString("global.pageQuery.totalPage") +":_pages_/"+rb.getString("global.pageQuery.totalRow")+":_total_"+" | \n"
                  + "_first_ \n _prev_ \n _next_ \n _last_ \n | "+rb.getString("global.pageQuery.go")+" _pageNo_"+"\n"
                  +
                    " <input type='submit' value='GO' class='type_button' onClick=\"javascript:"+formPath+".choice.value='current';\">\n";

            bar = StringUtil.replace(bar, "_pageSize_", pageSizeInput);
            bar = StringUtil.replace(bar, "_pages_", pages);
            bar = StringUtil.replace(bar, "_total_", total);
            bar = StringUtil.replace(bar, "_first_", firstText);
            bar = StringUtil.replace(bar, "_prev_", prevText);
            bar = StringUtil.replace(bar, "_next_", nextText);
            bar = StringUtil.replace(bar, "_last_", lastText);
            bar = StringUtil.replace(bar, "_pageNo_", pageNoInput);
        }

        //---------------------按钮型的导航条-----------------------//
    /*    if (type.equalsIgnoreCase("SIMPLEBUTTON")) {
            String pageSizeInput = "<input class='navbar' type='text' size='1' value='" + pageSize +
                                   "' "
                                   + "onChange=\"javascript:this.form.choice.value='current';"
                                   + "this.form.pageSize.value=this.value;return true;\">";
            String prevButton = "<input class='navbar' type='button' value='上一页' " + prevDisabled +
                                " "
                                +
                    "onClick=\"javascript:this.form.choice.value='prev';this.form.submit();\">";
            String nextButton = "<input class='navbar' type='button' value='下一页' " + nextDisabled +
                                " "
                                +
                    "onClick=\"javascript:this.form.choice.value='next';this.form.submit();\">";
            String pageNoInput = "<input class='navbar' type='text' size='1' value='" + pageNo +
                                 "' "
                                 + "onChange=\"javascript:this.form.pageNo.value=this.value\">";

            bar = "每页pageSize条记录 | \n"
                  + "共pages页/total条记录 | \n"
                  + "\n prev \n next \n | 第pageNo页\n"
                  + " <input class='navbar' type='submit' value='GO' class='type_button' onClick=\"javascript:this.form.choice.value='current';\">\n";

            bar = StringUtil.replace(bar, "pageSize", pageSizeInput);
            bar = StringUtil.replace(bar, "pages", pages);
            bar = StringUtil.replace(bar, "total", total);
            bar = StringUtil.replace(bar, "prev", prevButton);
            bar = StringUtil.replace(bar, "next", nextButton);
            bar = StringUtil.replace(bar, "pageNo", pageNoInput);
        } /////end of if(button)

        //-------------------------文字型----------------------------//
        if (type.equalsIgnoreCase("SIMPLETEXT")) {
            String pageSizeInput = "<input type='text' size='1' value='" + pageSize + "' "
                                   + "onChange=\"javascript:document.forms[0].choice.value='current';"
                                   + "this.form.pageSize.value=this.value;return true;\">";
            String firstText = rb.getString("global.pageQuery.first");
            String prevText = rb.getString("global.pageQuery.prev");
            String nextText = rb.getString("global.pageQuery.next");
            String lastText = rb.getString("global.pageQuery.last");
            if (prevDisabled.equalsIgnoreCase("")) {
                firstText = "<a href='first' "
                            +
                        "onClick=\"javascript:document.forms[0].choice.value='first';document.forms[0].submit();return false;\">"
                            + rb.getString("global.pageQuery.first")
                            + "</a>";
                prevText = "<a href='prev' "
                           +
                        "onClick=\"javascript:document.forms[0].choice.value='prev';document.forms[0].submit();return false;\">"
                           + rb.getString("global.pageQuery.prev")
                           + "</a>";
            }
            if (nextDisabled.equalsIgnoreCase("")) {
                nextText = "<a href='next' "
                           +
                        "onClick=\"javascript:document.forms[0].choice.value='next';document.forms[0].submit();return false;\">"
                           + rb.getString("global.pageQuery.next")
                           + "</a>";
                lastText = "<a href='last' "
                           +
                        "onClick=\"javascript:document.forms[0].choice.value='last';document.forms[0].submit();return false;\">"
                           + rb.getString("global.pageQuery.last")
                           + "</a>";
            }
            String pageNoInput = "<input type='text' size='1' value='" + pageNo + "' "
                                 + "onChange=\"javascript:this.form.pageNo.value=this.value\">";

            bar = rb.getString("eachPage")+"pageSize "+rb.getString("size")+" | \n"
                  + "共pages页/total条记录 | \n"
                  + "prev \n next \n | 第pageNo页\n"
                  +
                    " <input type='submit' value='GO' class='type_button' onClick=\"javascript:this.form.choice.value='current';\">\n";

            bar = StringUtil.replace(bar, "pageSize", pageSizeInput);
            bar = StringUtil.replace(bar, "pages", pages);
            bar = StringUtil.replace(bar, "total", total);
            bar = StringUtil.replace(bar, "prev", prevText);
            bar = StringUtil.replace(bar, "next", nextText);
            bar = StringUtil.replace(bar, "pageNo", pageNoInput);
        } /////end of if(text)  */
        
        return "<div>"+bar+"</div>";
    }}