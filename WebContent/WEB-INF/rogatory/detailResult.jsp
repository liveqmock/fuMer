<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="com.fuiou.mgr.util.page.PagerUtil"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title>查询明细</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<s:if test="tApsTxnLogMap == ''">
	<center><div style="fond-size:12px;"><b>没有符合条件的记录！</b></div></center>
</s:if>
<s:else>
<table class="tableBorder" border="0">
	<tr>
		<td width="90px;" align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1">交易批号</td>
		<td class="tr1" nowrap="nowrap" align="left" width="160px;">${tApsTxnLogMap.KBPS_SRC_SETTLE_DT}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >交易流水号</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.KBPS_TRACE_NO}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >文件明细号 </td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.SRC_SSN}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >交易提交时间  </td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.TXN_RCV_TS}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >交易类型 </td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.bargaining}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >文件名 </td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.SRC_ORDER_NO}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >交易金额 </td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.SRC_TXN_AMT}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >户名</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.ID_NO}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >扣款账号</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.DEBIT_ACNT_NO}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >收款账号</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.CREDIT_ACNT_NO}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >交易状态</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.OP_ST}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >注释</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.ADDN_PRIV_DATA}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >备注</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.remark_nm}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >企业流水号</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.enSerialNum_nm}&nbsp;</td>
	</tr>
	<tr>
		<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >手机号码</td>
		<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.phoneNum_nm}&nbsp;</td>
	</tr>
	<s:if test="#tApsTxnLogMap.TP_TXN_RCV_TS != null">
	
	     <tr>
			<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >退票附言</td>
			<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.ADDN_PRIV_DATA}&nbsp;</td>
		</tr>
		<tr>
			<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >原交易时间</td>
			<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.TP_TXN_RCV_TS}&nbsp;</td>
		</tr>
		<tr>
			<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >原交易流水</td>
			<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.TP_KBPS_TRACE_NO}&nbsp;</td>
		</tr>
		<tr>
			<td align="left" nowrap="nowrap" bgcolor="#e8eef7" class="tr1" >原交易金额</td>
			<td class="tr1" nowrap="nowrap" align="left" >${tApsTxnLogMap.TP_DEST_TXN_AMT}&nbsp;</td>
		</tr>
	
	</s:if>
</table>
</s:else>
</body>
</html>