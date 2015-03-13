<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="com.fuiou.mgr.util.page.PagerUtil"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title>交易查询</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/all.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="<%=root %>/js/jquery.js" type="text/javascript"></script>
</head>
<body>
<%
	int totalRecords = (Integer)request.getAttribute("totalCount");
	if (0 == totalRecords) {
%>
	<div style="fond-size:12px;" align="center"><b><s:text name="txnQuery.form.nothing" /></b></div>
<%
	} else {
%>
<s:form name="form1" action="queryCurr_query.action" method="post" theme="simple">
	<div class="table">
     <table border="0" cellpadding="0" cellspacing="0" class="tableBorder">
		<page:pager total="<%=totalRecords %>">
			<tr>
				<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.acntNm" /></td>
				<s:if test="#request.txnlog.BUSI_CD != 'TP01'">
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.txnDate" /></td>
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.serialNum" /></td>
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.txnRcvTs" /></td>
	     		</s:if>
	     		<s:if test="#request.txnlog.BUSI_CD == 'TP01'">
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.refundTxnDate" /></td>
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.refundSerialNum" /></td>
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.origSerialNum" /></td>
	     		</s:if>
	     		<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.busiCd" /></td>
	     		<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.filename" /></td>
	     		<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.amtTitle" /></td>
	     		<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.bankNm" /></td>
	     		<s:if test="#request.txnlog.BUSI_CD != 'TP01'">
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.feeAmt" /></td>
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.status" /></td>
	     		</s:if>
	     		<s:if test="#request.txnlog.BUSI_CD == 'AP01'">
	     			<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.refund" /></td>
	     		</s:if>
	     		<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.remark" /></td>
	     		<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.operatorId" /></td>
	     		<td align="center" nowrap="nowrap" class='title'><s:text name="txnQuery.form.operation" /></td>
	     	</tr>
	     	<%int txnIndex = 0; %>
			<s:iterator value="#request.dataset" id="trade" var="t" status="trade">
				<tr id="tr<%=txnIndex %>">
					<td align="left"  nowrap="nowrap" class='tr1'><s:property value="#t.ID_NO" />&nbsp;</td>
					<s:if test="#request.txnlog.BUSI_CD != 'TP01'">
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.KBPS_SRC_SETTLE_DT" />&nbsp;</td>
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.KBPS_TRACE_NO" />&nbsp;</td>
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.TRACK_3_DATA" />&nbsp;</td>
					</s:if>
					<s:if test="#request.txnlog.BUSI_CD == 'TP01'">
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.TRACK_3_DATA" />&nbsp;</td>
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.KBPS_TRACE_NO" />&nbsp;</td>
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.RLAT_KBPS_TRACE_NO" />&nbsp;</td>
					</s:if>
					<td align="center"  nowrap="nowrap" class='tr1'>&nbsp;<s:property value="#t.MSG_TP" /></td>
					<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.SRC_ORDER_NO" />&nbsp;</td>
					<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.minAmt" />&nbsp;</td>
					<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.DEST_INS_CD" />&nbsp;</td>
					<s:if test="#request.txnlog.BUSI_CD != 'TP01'">
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.TRACK_2_DATA" />&nbsp;</td>
						<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.DEST_TXN_ST" />&nbsp;</td>
					</s:if>
					<s:if test="#request.txnlog.BUSI_CD == 'AP01'">
		     			<td align="center" nowrap="nowrap" class='tr1'>
		     				<s:if test="#t.CANCEL_IND == 1">
		     					<s:text name="txnQuery.form.refundYes" />
		     				</s:if>
		     				<s:if test="#t.CANCEL_IND != 1">
		     					<s:text name="txnQuery.form.refundNo" />
		     				</s:if>
		     			</td>
		     		</s:if>
					<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.ADDN_PRIV_DATA" />&nbsp;</td>
					<td align="center"  nowrap="nowrap" class='tr1'><s:property value="#t.SEC_CTRL_INF" />&nbsp;</td>
					<td align="center"  nowrap="nowrap" class='tr1'>
						<a href="${pageContext.request.contextPath}/returnTicket_queryCurrDetail.action?txnLog.KBPS_SRC_SETTLE_DT=<s:property value="#t.KBPS_SRC_SETTLE_DT" />&txnLog.SRC_MODULE_CD=<s:property value="#t.SRC_MODULE_CD" />&txnLog.KBPS_TRACE_NO=<s:property value="#t.KBPS_TRACE_NO" />&txnLog.SUB_TXN_SEQ=<s:property value="#t.SUB_TXN_SEQ" />&txnLog.BUSI_CD=<s:property value="#t.BUSI_CD" />" target="_blank"><s:text name="txnQuery.form.detail" /></a>
						<s:if test="#t.ACQ_AUTH_RSP_CD == 1">
							&nbsp;<a href="${pageContext.request.contextPath}/returnTicket_initPrint.action?txnLog.KBPS_SRC_SETTLE_DT=<s:property value="#t.KBPS_SRC_SETTLE_DT" />&txnLog.SRC_MODULE_CD=<s:property value="#t.SRC_MODULE_CD" />&txnLog.KBPS_TRACE_NO=<s:property value="#t.KBPS_TRACE_NO" />&txnLog.SUB_TXN_SEQ=<s:property value="#t.SUB_TXN_SEQ" />&txnLog.BUSI_CD=<s:property value="#t.BUSI_CD" />" target="_blank"><s:text name="txnQuery.form.print" /></a>							
						</s:if>
					</td>
				</tr>
			</s:iterator>
			<tr class="bg_column" height='20'>
				<td width="960" height="30" colspan="20" align="left"><page:navigator type='text' />
					<input type="hidden" name="allDisplayCount" id="allDisplayCount" value="<%=txnIndex %>"/>
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
