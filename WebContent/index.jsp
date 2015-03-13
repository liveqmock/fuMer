<%@ include file="common.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="java.util.List" %>
<%@ page import="com.fuiou.mer.model.MenuDto" %>
<%@ page import="com.fuiou.mer.util.TDataDictConst" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html> 
<head>
<Link Rel="ICON NAME" href="" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>富汇通——富友代收付商户操作平台</title>
<link href="styles/style.css" rel="stylesheet" type="text/css" />
<link href="styles/leftmenu.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="styles/style.js" type="text/javascript"></script>
<script language="javascript" src="js/changeMenu.js" type="text/javascript"></script>
<script language="javascript" src="styles/leftmenu.js" type="text/javascript"></script>
<script type="text/javascript">
function innerH(){
	document.getElementById("submenuDiv").innerHTML='<s:text name="index.form.sysManaget"/> -> <s:text name="index.form.sysNotice"/>';
}
</script>
</head>

<body style="padding-left:5px;" onLoad="height();innerH();">
<%
int aaa = 0;
%>
<div id="head" style="background:url(images/bannerbg_03.jpg) repeat-x;">

  <div class="title"><span style="color:#DA3A3F"></span> <img alt="logo" src="images/fuyou.gif"  height="60" align="left" /></div>
  <div class="logininfo">
  <table>
  <tr><td>&nbsp;</td></tr>
  <tr>
  	<td>
  	<s:text name="index.form.mchntId"/>:<font color=""><s:property value="#session.operatorInf.MCHNT_CD" /></font>&nbsp;|&nbsp;
  	<s:text name="index.form.operatorId"/>:<font color=""><s:property value="#session.operatorInf.LOGIN_ID" /></font> | 
  		<a title="注销" href="logout.action"><s:text name="index.form.logout"/></a>
  	</td>
  </tr>
  </table>
  		 
  </div>
  <div id='live114444' style="float:right;"> </div>
</div>

<!--leftmenu--S-->
<div id="leftmenu">
<ul id="navmenu">
	<% 
	  List menuList = (List)session.getAttribute("menuList");
   	  if(menuList == null || menuList.size()<1){
	        request.setAttribute("msg","对不起，您没有权限");
	        request.getRequestDispatcher("login.jsp").forward(request, response);
	        return ;
		}else{
			 MenuDto menuBean=null;
	            MenuDto subBean=null;
	            for(int i=0; i<menuList.size(); i++){
	                menuBean = (MenuDto)menuList.get(i);
	                String menu="";
	                if( null!=menuBean)
	                   menu = menuBean.name;
	%>
	<li><a onmouseover="this.style.backgroundColor='#3370ac'" onmouseout="this.style.backgroundColor='#6ea7df'" onClick="submenu(<%= menuList.size()%>,'sub<%=i%>');"><%=menu%></a>
		<ul id="sub<%=i%>" class="none">
			<%
				List subMenuList = menuBean.getSubMenu();
                for(int j=0; j<subMenuList.size(); j++){
                    subBean = (MenuDto)subMenuList.get(j);
                    String subMenu="";
                    if(!"".equals(subBean) || null!=subBean)
                        subMenu = subBean.getName();	
					
				%>
			<li><a onmouseover="this.style.backgroundColor='#ffffff'" onmouseout="this.style.backgroundColor='#ffffd0'" onClick="tabmenu('<%=subBean.getCode()+(subBean.getParams()==null?"":subBean.getParams())%>','<%=menu+" -> "+subMenu%>')">
				<%= subMenu%></a></li>
					<%
				}
			%>
		</ul>
	</li>
				<%
			
			}
		}
	%>
</ul>
</div>
<!--leftmenu--E-->
<div id="data">
<div id="topdot"></div>
<div id="submenuDiv">
</div>

<div id="data_eara">
<iframe id="struFrame" style="height:100%;width:100%" name="struFrame" src="proclamAction_findNotices.action" frameborder="0" scrolling="no"></iframe>
<!--data_eara-start-->
</div>
<div id="bottomdot"></div>
</div>
</body>
</html>