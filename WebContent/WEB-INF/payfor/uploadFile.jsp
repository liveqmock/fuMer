<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.fuiou.mer.util.TDataDictConst"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统|上传批量付款文件</title>
<sx:head/>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
	
	String busiCd = TDataDictConst.BUSI_CD_PAYFOR;
	request.setAttribute("busiCd",busiCd);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
</head>
<body onload="resizeAll();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<div id="condition">
<form action="${pageContext.request.contextPath}/payfor/payFor_executePayForUpload.action" enctype="multipart/form-data" target="data_eara_frame_down"  method="post" onsubmit="submit.disabled=true">
上传批量付款文件:&nbsp;<input type="file" name="upload" />
    	<input type="submit" id="submit" name="submit" value="上传" class="type_button" />
    	<s:hidden name="busiCd" id="busiCd" value="%{#request.busiCd}"></s:hidden>
   </form>
</div>
</body>
</html>