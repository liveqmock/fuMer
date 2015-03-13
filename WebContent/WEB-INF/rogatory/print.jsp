<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=root %>/js/jquery.js"></script>
<script type="text/javascript" src="<%=root %>/js/printArea.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#printBtn").click(function(){
			$("#printArea").printArea();			
		})
	})
</script>
<title>回执打印</title>
</head>
<body>
	<center>
	<div id="printArea" style="width: 50%" class="table">
				<table border="0">
					<tr>
						<td width="40%">
							<img alt="商户富友支付服务有限公司" src="<%=root %>/images/1.png" style="width: 180px;float: left;margin-bottom: -50px;">
						</td>
						<td>
							<h2>富友代收付平台</h2>
						</td>
					</tr>
				</table>					
				<table width="100%" cellpadding="0" cellspacing="0" border="1">
				<tr>
					<td colspan="2"  align="center">付款业务</td>
				</tr>
				<tr>
					<td colspan="2"  align="center">批量业务发起时间：<s:property value="%{txnLog.ACQ_AUTH_RSP_CD}"/></td>
				</tr>
				<tr>
					<td colspan="2">批量业务发起方:<s:property value="%{txnLog.SRC_MCHNT_CD}"/></td>
				</tr>
				<tr>
					<td width="50%">业务类型：批量付款</td>
					<td>批次号：<s:property value="%{txnLog.KBPS_DEST_SETTLE_DT}"/></td>
				</tr>
				<tr>
					<td>付款人账号：<s:property value="%{txnLog.ACQ_RSP_CD}"/></td>
					<td>收款人账号：<s:property value="%{txnLog.DEST_SSN}"/></td>
				</tr>
				<tr>
					<td>付款人名称：<s:property value="%{txnLog.ADDN_RSP_DAT}"/></td>
					<td>收款人名称：<s:property value="%{txnLog.ID_NO}"/></td>
				</tr>
				<tr>
					<td>付款人开户银行：<s:property value="%{txnLog.ADDN_PRIV_DATA}"/></td>
					<td>收款人开户银行：<s:property value="%{txnLog.ADDN_POS_INF}"/></td>
				</tr>
				<tr>
					<td>大写金额：<s:property value="%{txnLog.ADV_PROC_ST}"/></td>
					<td>小写金额：<s:property value="%{txnLog.ACQ_INS_CD}"/>元</td>
				</tr>
				<tr>
					<td>业务处理状态：回执成功</td>
					<td>付款方式：转账</td>
				</tr>
				<tr>
					<td>用途：<s:property value="%{txnLog.ACQ_INS_RES}"/></td>
					<td>备注：</td>
				</tr>
				<tr>
					<td>企业预留信息：<s:property value="%{txnLog.ACQ_INS_RES}"/></td>
					<td>打印日期：<s:property value="%{txnLog.ADJ_CAPITAL_MD}"/></td>
				</tr>
		</table>
	</div>
	<div style="width: 50%" align="right">
		<input type="button" value="打印" id="printBtn">
	</div>
	</center>
</body>
</html>