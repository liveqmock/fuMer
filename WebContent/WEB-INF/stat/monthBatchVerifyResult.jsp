<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>年报表</title>
<link href="styles/all.css" rel="stylesheet" type="text/css" />
<link href="<%=root%>/styles/style.css" rel="stylesheet"
	type="text/css" />
</head>
<body>
<div class="right">
<div class="searchList">
<table border="0" cellspacing="1" cellpadding="0">
	<tr align="left">
		<td colspan="2">
		<table width="100%" border="0">
			<tr align="left">
				<td style="font-weight: bold; font-size: 16px; color: #457ec1"
					colspan="2">实名验证交易年报表</td>
			</tr>
		</table>
		</td>
	</tr>

	<tr align="left">
		<td colspan="4" nowrap="nowrap" class="tr1">
		<table class="tableBorder" width="100%" border="0">
			<tr align="left">
				<td style="background: #e8eef7" class="tr1" nowrap="nowrap">商户名称：</td>
				<td class="tr1" nowrap="nowrap">${srcMchntName }</td>
				<td style="background: #e8eef7" class="tr1" nowrap="nowrap">总笔数：</td>
				<td class="tr1" nowrap="nowrap">${cnt }笔</td>
			</tr>
			<tr align="left">
				<td style="background: #e8eef7" class="tr1" nowrap="nowrap">总验证匹配笔数：</td>
				<td class="tr1" nowrap="nowrap">${sucCnt }</td>
				<td style="background: #e8eef7" class="tr1" nowrap="nowrap">总验证不匹配笔数：</td>
				<td class="tr1" nowrap="nowrap" >${failCnt }笔</td>
			</tr>
		</table>
		<br>
		</td>
	</tr>

	<tr>
		<td colspan="4" nowrap="nowrap" class="tr1">
		<table class="tableBorder" width="100%" border="0">
			<tr>
				<td style="background: #e8eef7" nowrap="nowrap" class="tr1">交易月份</td>
				<td style="background: #e8eef7" nowrap="nowrap" class="tr1">总笔数</td>
				<td style="background: #e8eef7" nowrap="nowrap" class="tr1">验证匹配笔数</td>
				<td style="background: #e8eef7" nowrap="nowrap" class="tr1">验证不匹配笔数</td>
			</tr>
			<s:iterator value="#request.resultList" id="StatResultBean"
				status="StatResultBean">
				<tr>
					<td class="tr1" nowrap="nowrap"><s:property value="KBPS_SRC_SETTLE_DT" /></td>
					<td class="tr1" nowrap="nowrap"><s:property	value="CNT" /></td>
					<td class="tr1" nowrap="nowrap"><s:property	value="SUC_CNT" /></td>
					<td class="tr1" nowrap="nowrap"><s:property	value="FAIL_CNT" /></td>
				</tr>
			</s:iterator>
			<tr>
				<td>合计:</td>
				<td class="tr1" nowrap="nowrap">${cnt }</td>
				<td class="tr1" nowrap="nowrap">${sucCnt }</td>
				<td class="tr1" nowrap="nowrap" >${failCnt }</td>
			</tr>
		</table>
		</td>
	</tr>

</table>
</div>
</div>
</body>
</html>
