<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="styles/all.css" rel="stylesheet" type="text/css" />
<style type="text/css">
frameset{ border:0px;}
.cBg{background:url(images/container_bg.png) repeat-x ;}
</style>
<title>富友商户系统</title>
</head>
<frameset rows="98,*,130" cols="*" framespacing="0" frameborder="no" border="0">
  <frame src="header.jsp" name="topFrame" scrolling="no" noresize="noresize" id="topFrame" title="topFrame" />
  <frameset rows="*" cols="140,*" class="aaa" framespacing="0" frameborder="0" border="0">
    <frame src="control.jsp" id="leftFrame" name="leftFrame" style=" background:url(images/container_bg.png) repeat-x 98px 0px" allowTransparency="true" scrolling="no" noresize="noresize" title="leftFrame" />
    <frame src="main.jsp" id="mainFrame" name="mainFrame" class="cBg" style=" background:url(images/container_bg.png) repeat-x 98px 0px; " allowTransparency="true"  id="mainFrame" title="mainFrame" />
  </frameset>
  <frame src="footer.jsp" name="footerFrame" id="footerFrame" title="footerFrame"  />
</frameset>
<noframes>
<body>
</body></noframes>
</html>