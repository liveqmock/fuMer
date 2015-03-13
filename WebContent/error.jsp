<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file="/common.jsp" %>
<html>
<head>
<title> error </title>
<script language="javascript" src="/styles/style.js" type="text/javascript"></script>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />

</head>
<%
	String message = (String)request.getAttribute("msg");
	//String action = (String)request.getAttribute("action");
%>
<body style="text-align:center;">
<div class="error" >
<center><div style="fond-size:12px;"><b><%=message==null?"":message%></b></div></center>
</div>					
</body>
</html>