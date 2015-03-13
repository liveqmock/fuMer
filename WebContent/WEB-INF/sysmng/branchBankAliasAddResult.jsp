<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支行别名新增|结果页面</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<STYLE type="text/css">

.select {
	width:360px; 
	height:19px; 
	border:#7f9db9 1px solid;
	margin-right:5px;
}
</STYLE>
<%

	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
</head>
<body onload="resizeAll();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
	<table>
	<tr>
        <td>支行别名新增成功</td>
        <td>
        	<s:submit cssClass="type_button" name="查询" value="查询" onclick="javascript:window.location.href='doNothing.action?forwardAction=/WEB-INF/sysmng/branchBankAliasQueryReq.jsp'"/>
        </td>
    </tr>
	</table>
</div>
</body>
</html>