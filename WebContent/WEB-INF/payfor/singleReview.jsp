<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>单笔付款复核</title>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js" type="text/javascript"></script>
<script language="javascript" src="<%=root %>/js/jquery.js" type="text/javascript"></script>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
</head>

<body style="padding: 0px; margin: 0px;">
<%
	int totalRecords = (Integer)request.getAttribute("fileCount");
	String message = (String)request.getAttribute("message");
	//out.println("totalRecords=" + totalRecords);
	if(0 == totalRecords) {
		out.println("<center><div style=\"fond-size:12px;\"><b>" + message + "</b></div></center>");
		return ;
	}else {
%>
<s:form name="form1" action="/payfor/PayforReviewAction_execute.action" method="post" theme="simple">
	<div class="table">
     <table  class="tableBorder" border="0" cellpadding="0" cellspacing="0" align="center">
    	 <tr>
    	  <td align="center" nowrap="nowrap" class='title'>户名</td>
		  <td align="center" nowrap="nowrap" class='title'>流水号</td>
    	  <td align="center" nowrap="nowrap" class='title'>交易类型</td>
    	  <td align="center" nowrap="nowrap" class='title'>收款帐号</td>
    	  <td align="center" nowrap="nowrap" class='title'>开户行</td>
    	  <td align="center" nowrap="nowrap" class='title'>交易金额</td>
    	  <td align="center" nowrap="nowrap" class='title'>手续费</td>
		  <td align="center" nowrap="nowrap" class='title'>交易提交时间</td>
		  <td align="center" nowrap="nowrap" class='title'>操作</td>
		</tr>
		<page:pager total="<%=totalRecords %>">
		<%int txnIndex = 0; %>
			<s:iterator value="#request.fileList" id="trade" status="trade">
				<s:if test="#trade.odd == true">
			    	<tr class="tr1" id="tr<%=txnIndex %>">
			    </s:if>
			    <s:else>
			    	<tr class="tr2" id="tr<%=txnIndex %>">
			    </s:else>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="ID_NO" /></td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="KBPS_TRACE_NO" /></td>
					<td align="center" nowrap="nowrap" class='tr1'>单笔</td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="DEBIT_ACNT_NO" /></td> 
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="FIRST_SRC_MODULE_CD" /></td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="FIRST_KBPS_SRC_SETTLE_DT"/></td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="FIRST_KBPS_TRACE_NO"/></td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="TXN_RCV_TS" /></td>
					<td align="center" nowrap="nowrap" class='tr1'>					
					<a href='${pageContext.request.contextPath}/payfor/PayforReviewAction_singleReview.action?KBPS_TRACE_NO=<s:property value="KBPS_TRACE_NO" />&KBPS_SRC_SETTLE_DT=<s:property value="KBPS_SRC_SETTLE_DT" />&fileBusiTp=<s:property value="#request.fileBusiTp"/>'>复核&nbsp;|&nbsp;</a>
					
					<a href='${pageContext.request.contextPath}/payfor/PayforReviewAction_singleReviewNoPass.action?KBPS_TRACE_NO=<s:property value="KBPS_TRACE_NO" />&KBPS_SRC_SETTLE_DT=<s:property value="KBPS_SRC_SETTLE_DT" />&fileBusiTp=<s:property value="#request.fileBusiTp"/>'>复核不通过&nbsp;|&nbsp;</a>	
					</td>
				</tr>
				<%txnIndex++; %>
			</s:iterator>
			<tr class="bg_column" height='20'>
				<td width="960" height="30" colspan="20" align="left"><page:navigator
					type='text' />
					<input type="hidden" name="allDisplayCount" id="allDisplayCount" value="<%=txnIndex %>"/></td>
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