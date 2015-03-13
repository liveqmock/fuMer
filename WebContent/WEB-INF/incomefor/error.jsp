<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%@ include file="/common.jsp" %>
<html>
<head>
<title> error </title>
<script language="javascript" src="<%=root %>/styles/style.js" type="text/javascript"></script>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />

</head>
<%
	String message = (String)request.getAttribute("msg");
	//String action = (String)request.getAttribute("action");
%>
<body onload="resizeAll();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<div class="error" >
<%=message==null?"":message%>
</div>					
</body>
</html>
