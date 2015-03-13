<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>日对帐单</title>
<link href="<%=root %>/styles/all.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<style type="text/css">
<!--
.STYLE1 {color: #F0F0F0}
-->
</style>
</head>
<body>
<div class="table">
<table width="383">
	<tr>
		<td width="187">商户名称:${companyName}</td>
		<td width="110">日期:${fromDate}</td>
	</tr>
</table>
<table align="center" class="tableBorder">
	<tr>
		<th class="title" nowrap="nowrap">交易类型</th>
		<th class="title" nowrap="nowrap">交易状态</th>
		<th class="title" nowrap="nowrap">笔数</th>
		<th class="title" nowrap="nowrap">应收金额</th>
		<th class="title" nowrap="nowrap">实收金额</th>
		<th class="title" nowrap="nowrap">应付金额</th>
		<th class="title" nowrap="nowrap">实付金额</th>
		<s:if test="#MCHNT_TP=='D601'">
		<th class="title" nowrap="nowrap">手续费合计</th>
		</s:if>
	</tr>
	<tr>
		<td rowspan="2" align="center" nowrap="nowrap" class="tr1">提出代收</td>
		<td align="center" nowrap="nowrap" class="tr1">处理成功:</td>
		<td class="tr1" nowrap="nowrap" align="right">${incomeforSuccessCount }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${incomefor_src_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${incomefor_dest_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">0.00&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">0.00&nbsp;</td>
		<s:if test="#MCHNT_TP=='D601'">
		<td class="tr1" nowrap="nowrap" align="right">${incomeforSuccessFeeAtm/100}&nbsp;</td>
		</s:if>
	</tr>
	<tr>
	
		<td class="tr1" nowrap="nowrap" align="center">处理失败:</td>
		<td class="tr1" nowrap="nowrap" align="right">${incomeforfailuerCount}&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${incomefor_src_FailureAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${incomefor_dest_FailureAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">0.00&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">0.00&nbsp;</td>
		<s:if test="#MCHNT_TP=='D601'">
		<td class="tr1" nowrap="nowrap" align="right">${incomeforFailureFeeAtm/100}&nbsp;</td>
		</s:if>
	</tr>
	<tr>
		<td class="tr1" rowspan="2" nowrap="nowrap" align="center">提出付款</td>
		<td class="tr1" nowrap="nowrap" align="center">处理成功:</td>
		<td class="tr1" nowrap="nowrap" align="right">${payforSuccessCount }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_inSrc_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_inDest_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_src_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_dest_SuccessAmt }&nbsp;</td>
		<s:if test="#MCHNT_TP=='D601'">
		<td class="tr1" nowrap="nowrap" align="right">${payforFeeAtm/100}&nbsp;</td>
		</s:if>
	</tr>
	<tr>
	
		<td class="tr1" nowrap="nowrap" align="center">处理失败:</td>
		<td class="tr1" nowrap="nowrap" align="right">${payforFailureCount }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_inSrc_FailureAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_inDest_FailureAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_src_FailureAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${payfor_dest_FailureAmt }&nbsp;</td>
		<s:if test="#MCHNT_TP=='D601'">
		<td class="tr1" nowrap="nowrap" align="right">${payforFeeFailureAtm/100}&nbsp;</td>
		</s:if>
	</tr>
	
	<tr>
		<td class="tr1" nowrap="nowrap" align="center">提回退票</td>
		<td class="tr1" nowrap="nowrap" align="center">处理成功:</td>
		<td class="tr1" nowrap="nowrap" align="right">${refTkSuccessCount }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${refTk_src_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${refTk_dest_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">0.00&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">0.00&nbsp;</td>
		<s:if test="#MCHNT_TP=='D601'">
		<td class="tr1" nowrap="nowrap" align="right">${refundFeeAtm/100}&nbsp;</td>
		</s:if>
	</tr>
	<tr>
		<td colspan="2" class="tr1" nowrap="nowrap" align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;合计</td>
		<td class="tr1" nowrap="nowrap" align="right">${allCount }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${all_inSrc_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${all_inDest_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${all_src_SuccessAmt }&nbsp;</td>
		<td class="tr1" nowrap="nowrap" align="right">${all_dest_SuccessAmt }&nbsp;</td>
		<s:if test="#MCHNT_TP=='D601'">
		<td class="tr1" nowrap="nowrap" align="right">${all_fee_ant}&nbsp;</td>
		</s:if>
	</tr>
</table>
</div>
</body>
</html>
