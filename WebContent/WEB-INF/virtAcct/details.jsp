<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="com.fuiou.mgr.util.page.PagerUtil"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat" %>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr1 = sdf1.format(dt);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=root%>/styles/style.css" rel="stylesheet" type="text/css" />
<link href="styles/leftmenu.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/all.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<title>虚拟账户详细记录</title>
</head>
<body onload="resizeAll();" style="padding: 0px; margin: 0px;height: 300px;">
	<%
	int totalRecords = (Integer)request.getAttribute("totalCnt");
	if (0 == totalRecords) {
	%>
		<div style="fond-size:12px;" align="center"><b><s:text name="txnQuery.form.nothing" /></b></div>
<%
	} else {
%>
		<s:form>
<div class="table">
     <table border="0" cellpadding="0" cellspacing="0" class="tableBorder">
		<page:pager total="<%=totalRecords %>">
			<tr>
				<td align="center" nowrap="nowrap" class='title'><s:text name="acntManagement.form.serialNum"/></td>
				<td align="center" nowrap="nowrap" class='title'><s:text name="acntManagement.form.txnDate"/></td>
				<td align="center" nowrap="nowrap" class='title'><s:text name="acntManagement.form.bookDate"/></td>
				<td align="center" nowrap="nowrap" class='title'><s:text name="acntManagement.form.credit"/></td>
				<td align="center" nowrap="nowrap" class='title'><s:text name="acntManagement.form.debit"/></td>
				<td align="center" nowrap="nowrap" class='title'><s:text name="acntManagement.form.availableBalance"/></td>
				<td align="center" nowrap="nowrap" class='title'><s:text name="acntManagement.form.remark"/></td>
	     	</tr>
	     	<%int txnIndex = 0; %>
			<s:iterator value="#request.details" id="trade" status="trade">
				<tr>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="TRACE_NO" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="FAS_SETTLE_DT" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="BOOK_DT" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="amt1" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="amt2" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="amt3" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="BOOK_DIGEST" />&nbsp;</td>
				</tr> 
			</s:iterator>
			<tr class="bg_column" height='20'>
				<td width="960" height="30" colspan="20" align="left"><page:navigator type='text' />
				</td>   
			</tr>
		</page:pager>
	</table>
	</div>
		</s:form>
<%
	}
%>
</body>
</html>