<%@ page import="java.util.List" %>
<%@ page import="com.fuiou.mer.model.MenuDto" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link href="styles/style.css" rel="stylesheet" type="text/css" />
<link href="styles/leftmenu.css" rel="stylesheet" type="text/css" />
<link href="styles/all.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="styles/style.js" type="text/javascript"></script>
<script language="javascript" src="js/changeMenu.js" type="text/javascript"></script>
<script language="javascript" src="styles/leftmenu.js" type="text/javascript"></script>
</head>

<body>
<div class="left">
<div class="leftContent">
<div class="userInfor">
<div class="comperny"><s:property value="#session.insInf.INS_NAME_CN" /></div>
<div class="users">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="25%"><img src="images/user.png" width="32" height="32" /></td>
		<td width="75%">
		<h1><span style="color: #d66f00">欢迎回来！</span></h1>
		<h2>操作员：<font color=""><s:property value="#session.operatorInf.USER_NAME_CN" /></font></h2>
		</td>
	</tr>
</table>
</div>
</div>
<div class="navList">
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
                if(!"".equals(menuBean) || null!=menuBean)
                     menu = menuBean.name;
    %>
    <li><a href="#" onClick="submenu(<%= menuList.size()%>,'sub<%=i%>');"><%=menu%></a>
        <ul id="sub<%=i%>" class="none">
            <%
            List subMenuList = menuBean.getSubMenu();
                for(int j=0; j<subMenuList.size(); j++){
                    subBean = (MenuDto)subMenuList.get(j);
                    String subMenu="";
                    if(!"".equals(subBean) || null!=subBean)
                        subMenu = subBean.getName();
                %>
            <li><a href="<%=subBean.getCode()%>" target="mainFrame"><%=subMenu%></a></li>
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
<br />
<br />
<br />
<br />
</div>
</div>
<div class=""><img src="images/left_bottom_bg.png" width="173"
	height="3" /></div>
</div>
</body>
</html>