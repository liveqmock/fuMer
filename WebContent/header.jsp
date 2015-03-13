<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link href="styles/all.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="header">
      <div class="h_cont">
        <div class="logo"><img src="images/logo.jpg" width="313" height="90" /></div>
        <div class="compname"><img src="images/comp_name.jpg" width="252" height="48" /></div>
        <div class="time"><span>今天是: <%= new SimpleDateFormat("yyyy年M月d日").format(new Date()) %><a style="color:white" title="注销" href="logout.action" target="_parent">退出</a></span></div>
        </div>
    </div>
</body>
</html>