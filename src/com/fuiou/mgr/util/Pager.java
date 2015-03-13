package com.fuiou.mgr.util;

import java.util.Map;

/**
 * 分页的类
 * dev_db
 *
 */
public class Pager {
	/**
	 * 
	 * @param uir访问的地址
	 * @param pageNum当前页数
	 * @param totalCount信息总条数
	 * @param pageSize每页的条数
	 * @param map连接的参数集合
	 * @return
	 */
	public static String getPagerStr(String url,int pageNum,int totalCount,int pageSize,Map map){
		StringBuffer str=new StringBuffer();
		int totalPage=(totalCount+pageSize-1)/pageSize;
		String strp="";
		//循环获取连接的参数
		for(Object s:map.keySet()){
			strp+="&"+s+"="+map.get(s);
		}
		str.append("每页<input type=\"text\"  size='2' value='"+pageSize+"' maxLength='2' onchange=\"checkpageSize(this,'"+url+"?a=a"+strp+"&pageSize='),shux('?pageSize'=+this.value);\"/>条 |\n");
		str.append("总页数"+totalPage+"/总记录数:"+totalCount+" |\n");
		str.append("<a href=\""+url+"?a=a"+strp+"&pageNum=1&pageSize="+pageSize+"\"> 第一页</a>\n");
		str.append(pageNum<=1?"上一页":"<a href=\""+url+"?a=a"+strp+"&pageNum="+(pageNum-1)+"&pageSize="+pageSize+"\">上一页</a>\n");
		str.append(pageNum>=totalPage?"下一页":"<a href=\""+url+"?a=a"+strp+"&pageNum="+(pageNum+1)+"&pageSize="+pageSize+"\">下一页</a>\n");
		str.append("<a href=\""+url+"?a=a"+strp+"&pageNum="+totalPage+"&pageSize="+pageSize+"\"> 最后一页</a>\n");
		str.append("| 到<input type='text' size='2' value='"+pageNum+"' onChange=\"checkPageCount(this,'"+url+"?a=a"+strp+"&pageSize="+pageSize+"&pageNum='),shux('?pageNum'=+this.value+'&pageSize="+pageSize+"');\"> <input type=\"button\" value='GO' class='type_button' onClick=\"checkGo()\">\n");
		str.append("<input type=\"hidden\" id=\"hidurl\" value=\"\"/>\n");
		str.append("<script type=\"text/javascript\">\n");
		str.append("function shux(urlValue){\n");
		str.append("\tdocument.getElementById(\"hidurl\").value='"+url+"?'+urlValue+'"+strp+"';\n");
	    str.append("}\n");
		str.append("function checkGo(){");
		str.append("\n\tvar str=document.getElementById('hidurl').value;\n");
		str.append("\tif(str!=''){\n\tlocation.href=str;\n\t}\n}\n</script>\n");
		return str.toString();
	}
	public static String getParameter(Map map){
		String strp="";
		//循环获取连接的参数
		for(Object s:map.keySet()){
			strp+="&"+s+"="+map.get(s);
		}
		return strp;
	}
}
