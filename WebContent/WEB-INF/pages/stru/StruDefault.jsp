<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" " http://www.w3.org/TR/html4/strict.dtd"> 
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../../common.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>富汇通——富友代收付商户操作平台</title>
<script language="javascript" src="<%=root %>/styles/iframe.js"></script>
</head>
<body onload="resizeAll();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<%
	String data  = (String)request.getAttribute("src");
	String down  = (String)request.getAttribute("down");

%>
<iframe  id="data_eara_frame_up" name="data_eara_frame_up"  style="width:100%;"  frameborder="0" scrolling="auto" src="<%=data %>" ></iframe>
<iframe id="data_eara_frame_down" name="data_eara_frame_down" style="width:100%; "  frameborder="0" scrolling="auto" src=""></iframe>
</body>
</html>