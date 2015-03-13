<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="com.fuiou.mer.util.TDataDictConst"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>常用联系人查询</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	Date dt2 = new Date();
	dt2.setMonth(dt2.getMonth()-3);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
	String dateTimeStr2 = sdf.format(dt2);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript">
	function addUser(){
		var form1=document.getElementById("form1");
		form1.action="userInfoMgr_addInit.action";
		form1.target="data_eara_frame_top"
		form1.submit();
	}
	function query(){
		var form1=document.getElementById("form1");
		form1.action="userInfoMgr_findUsers.action";
		form1.target="data_eara_frame_down"
		form1.submit();
	}
</script>
</head>
<body  onload="resizeAll();"  style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
<form name="form1" id="form1" action="userInfoMgr_findUsers.action" method="post" target="data_eara_frame_down">
	<table>
		<tr><td colspan="7">常用联系人查询</td></tr>
        <tr>
            <td align="right">户名:</td>
            <td align="left"><input type="text" name="user.USER_NAME" id="user.USER_NAME" style="width:150px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" maxlength="60"/></td>
            <td align="right">账号:</td>
            <td align="left"><input type="text" name="user.ACCT_NO" id="user.ACCT_NO" style="width:150px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" maxlength="25"/></td>
            <td align="right">手机号:</td>
            <td align="left"><input type="text" name="user.MOBILE_NO" id="user.MOBILE_NO" style="width:150px; height:19px; border:#7f9db9 1px solid; margin-right:5px;"/></td>
            <td> <button type="button" onclick="query()" class="type_button">查询</button>
            <button type="button" name="button" onclick="addUser()" id="button" class="type_button">增加联系人</button>
            </td>
        </tr>
	</table>
</form>
</div>
</body>
</html>