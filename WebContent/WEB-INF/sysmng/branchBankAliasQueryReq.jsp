<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支行别名-查询</title>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
</head>
<body  onload="resizeAll();"  style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
<s:form name="form1" action="branchBankAliasQuery.action" method="post" target="data_eara_frame_down" theme="simple">
	<table>
        <s:actionerror/>
        <tr>
            <td align="right">关键字：</td>
            <td><s:textfield name="branchBankAlias" id="branchBankAlias" maxlength="80"/></td>
            <td><s:property value="fieldErrors.branchBankAlias"/> </td>
            <td align="right">标准名称：</td>
            <td><s:textfield name="branchBankName" id="branchBankName" maxlength="80"/></td>
            <td><s:property value="fieldErrors.branchBankName"/> </td>
            <td><s:submit cssClass="type_button" name="确认" value="确认" /></td>
            <td><s:submit cssClass="type_button" name="新增" value="新增" onclick="javascript:window.open('doNothing.action?forwardAction=/WEB-INF/sysmng/branchBankAliasAddReq.jsp')"/></td>
        </tr>
	</table>
</s:form>
</div>
</body>
</html>