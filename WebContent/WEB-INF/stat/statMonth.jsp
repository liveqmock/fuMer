<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统|月报表</title>
<sx:head/>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
	String dateTimeStr = sdf.format(dt);
	String startDate = sdf1.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
</head>
<body onload="resizeAll();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<div id="condition">
<s:form method="post" id="statMonth" action="statMonth_stat.action" target="data_eara_frame_down" theme="simple">
	<table>
		<tr>
			<s:if test="request.mchntList != null ">
				<td align="right">商户列表：</td>
				<td><s:select list="#request.mchntList" id="mchntCd" name="mchntCd"></s:select> </td>
			</s:if>
            <td align="right">业务类型：</td>
			<td><s:select list="#request.busiCdMap" id="busiCd" name="busiCd"></s:select> </td>
			<td align="right">月份：</td>
            <td><input type="text" class="Wdate"  name="startDate"  id="startDate" value="<%=startDate %>" onfocus="WdatePicker({dateFmt:'yyyyMM'})"/> </td>
             <td align="right">&nbsp;</td>
			<td align="left"><s:submit name="查询" value="查询" cssClass="type_button" /></td>
		</tr>
	</table>
</s:form>
</div>
</body>
</html>