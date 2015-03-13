<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>月报表</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="right">
<div class="searchList">
<table border="0" cellspacing="1" cellpadding="0" >
	<tr align="left">
			<td colspan="3">
					<table width="100%" border="0">
								<tr align="left">
		<td style="font-weight: bold; font-size: 16px; color: #457ec1" colspan="3">代收交易月报表</td>
		
	</tr>
					</table>
			</td>
	</tr>
	<tr align="left">
			<td colspan="6" nowrap="nowrap" class="tr1" >
					<table class="tableBorder" width="100%" border="0">
								<tr align="left">
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >商户名称</td>
		<td class="tr1" nowrap="nowrap" >${srcMchntName }</td>
		<td  style="background: #e8eef7"class="tr1" nowrap="nowrap" >总成功笔数:</td>
		<td class="tr1" nowrap="nowrap" >${sucCnt }笔</td>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >总成功金额:</td>
		<td class="tr1" nowrap="nowrap" >${sucAmt }元</td>
	</tr>
	<tr align="left">
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >交易类型</td>
		<td class="tr1" nowrap="nowrap" >${busiCd }</td>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >总失败笔数:</td>
		<td class="tr1" nowrap="nowrap" >${failCnt }笔</td>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >总失败金额:</td>
		<td class="tr1" nowrap="nowrap" >${failAmt }元</td>
	</tr>
					</table>
					<br />
			</td>
			</tr>

	<tr>
			<td colspan="6" nowrap="nowrap" class="tr1" >
				<table class="tableBorder" width="100%" border="0">
								<tr>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >交易日</td>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >成功交易笔数</td>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >成功交易金额</td>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" >失败交易笔数</td>
		<td style="background: #e8eef7" class="tr1" nowrap="nowrap" colspan="2">失败交易金额</td>
	</tr>
	<s:iterator value="#request.resultList" id="StatResultBean" status="StatResultBean">
	<tr>
		<td class="tr1" nowrap="nowrap" ><s:property value="KBPS_SRC_SETTLE_DT" /></td>
		<td class="tr1" nowrap="nowrap" ><s:property value="SUC_CNT" /></td>
		<td class="tr1" nowrap="nowrap" ><s:property value="SUC_DEST_TXN_AMT" /></td>
		<td class="tr1" nowrap="nowrap" ><s:property value="FAIL_CNT" /></td>
		<td class="tr1" nowrap="nowrap" colspan="2"><s:property value="FAIL_DEST_TXN_AMT" /></td>
	</tr>
	</s:iterator>
	<tr>
		<td>合计:</td>
		<td class="tr1" nowrap="nowrap" >${sucCnt }</td>
		<td class="tr1" nowrap="nowrap" >${sucAmt }</td>
		<td class="tr1" nowrap="nowrap" >${failCnt }</td>
		<td class="tr1" nowrap="nowrap" colspan="2">${failAmt }</td>
	</tr>
					</table>
			</td>
			</tr>

</table>
</div>
</div>
</body>
</html>
