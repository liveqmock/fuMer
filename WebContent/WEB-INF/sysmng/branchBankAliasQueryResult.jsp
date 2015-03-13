<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="com.fuiou.mgr.util.page.PagerUtil"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title>支行别名-查询结果</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<%
	int totalRecords = (Integer)request.getAttribute("totalCount");
	if (0 == totalRecords) {
		out.println("<center><div style=\"fond-size:12px;\"><b>没有符合条件的记录	！</b></div></center>");
		return;
	} else {
%>
<s:form name="form1" action="branchBankAliasQuery.action" method="post" theme="simple">

	<div class="table">
     <table border="0" cellpadding="0" cellspacing="0">
		<page:pager total="<%=totalRecords %>">
			<tr>
				<td align="center" nowrap="nowrap" class='title'>关键字</td>
	     		<td align="center" nowrap="nowrap" class='title'>标准名称</td>
	     		<td align="center" nowrap="nowrap" class='title'>操作</td>
	     	</tr>
			<s:iterator value="#request.aliasList" id="trade" status="trade">
				<tr>
					<td align="left" nowrap="nowrap" class='tr1'><s:property value="PMS_BANK_ALIAS" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="PMS_BANK_NM" />&nbsp;</td>
					<td align="center" nowrap="nowrap" class='tr1'>
						<a href="${pageContext.request.contextPath}/branchBankAliasDetail.action?id=<s:property value="ROW_ID" />" target="struFrame">修改</a>
						<a href="${pageContext.request.contextPath}/branchBankAliasDelete.action?id=<s:property value="ROW_ID" />" target="struFrame">删除</a>
					</td>
				</tr>
			</s:iterator>
			<tr class="bg_column" height='20'>
				<td width="960" height="30" colspan="20" align="left"><page:navigator type='text' /></td>
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
