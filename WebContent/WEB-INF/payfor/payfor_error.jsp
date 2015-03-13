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
<title>富友商户系统|批量上传付款文件</title>
<sx:head/>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script language="javascript">
window.parent.document.frames["data_eara_frame_up"].document.getElementById("submit").disabled=false;
</script>
</head>
<body>
<div id="condition">
${message }
<s:iterator value="errorMap" id="column">
	编码: <s:property value="key"/>&nbsp;&nbsp;&nbsp;&nbsp; 
	描述:<s:property escapeHtml="false" value="value"/><br>     
</s:iterator>
<s:iterator value="#request.errorList" id="trade" var="t" status="trade">
	<p><s:property value="#t" /></p>
</s:iterator>
</div>
</body>
</html>