<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String dateTimeStr = sdf.format(dt);
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript" src="<%=root%>/js/common.js"></script>
<script type="text/javascript" src="<%=root%>/js/jquery.js"></script>
<link href="<%=root%>/styles/style.css" rel="stylesheet" type="text/css" />
<link href="styles/leftmenu.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/stylediv.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
function tabmenu(url, desc){
	$(window.parent.document).find("iframe[@id='struFrame']").attr("src",url);
}
</script>
</head>
<body>
<form id="form1" name="form1" method="post" action="">
<div class="table2">
	<font style="font-size: 16px"><s:text name="notice.form.sysNotice"/></font>
  <table border="1" cellpadding="0" cellspacing="0" class="tableBorder">
  <tr>
    <td align="left" width="100" nowrap="nowrap" class="title"><s:text name="notice.form.date"/></td>
    <td align="left" nowrap="nowrap" class="title"><s:text name="notice.form.title"/></td>
    <td align="left" width="100" nowrap="nowrap" class="title"><s:text name="notice.form.view"/></td>
  </tr>
    <s:iterator value="notices" var="tNotice">
   	<tr style="background: #ffffff">
	   	<td align="left" nowrap="nowrap" class="tr1"><s:date name="#tNotice.NOTICE_SUBMIT_TIME" format="yyyy-MM-dd"/></td>
	   	<td align="left" nowrap="nowrap" class="tr1"><b><s:property escapeHtml="false" value="#tNotice.NOTICE_TITLE"/></b></td>
	   	<td align="left" nowrap="nowrap" class="tr1">
	   		<a href="proclamAction_findNoticeByNoticeno.action?notice.NOTICE_NO=<s:property value="#tNotice.NOTICE_NO"/>"><s:text name="notice.form.view"/></a>
   		</td>
   	</tr>
    </s:iterator>
  </table>
</div>
</form>
</body>
</html>