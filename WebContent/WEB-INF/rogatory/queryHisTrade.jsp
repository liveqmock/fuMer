<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>交易查询</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=root %>/js/jquery.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	Date dt2 = new Date();
	dt2.setMonth(dt2.getMonth()-1);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
	String dateTimeStr2 = sdf.format(dt2);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript">
	function query(){
		document.form1.action="queryCurr_query.action";
		document.form1.submit();
	}
	function download(){
		document.form1.action="queryCurr_download.action";
		document.form1.submit();
	}
	
	function checkAmt(){
		var minAmt = document.getElementById("minAmt").value;
		var maxAmt = document.getElementById("maxAmt").value;
		minAmt = minAmt.replace(/(^\s*)|(\s*$)/g, "");  
		maxAmt = maxAmt.replace(/(^\s*)|(\s*$)/g, "");  
		if(minAmt == '' && maxAmt == ''){
			document.getElementById("queryBtn").disabled=false;
			document.getElementById("downloadBtn").disabled=false;			
		}else{
			var pattern =/^-?\d+\.{0,}\d{0,}$/;
			if(minAmt != '' && !pattern.test(minAmt) ){
				alert("请输入正确的金额");
				document.getElementById("queryBtn").disabled=true;
				document.getElementById("downloadBtn").disabled=true;
				return ;
			}
			if(maxAmt != '' && !pattern.test(maxAmt) ){
				alert("请输入正确的金额");
				document.getElementById("queryBtn").disabled=true;
				document.getElementById("downloadBtn").disabled=true;
				return ;
			}
			document.getElementById("queryBtn").disabled=false;
			document.getElementById("downloadBtn").disabled=false;		
		}
	}
	function busiChange(o){
		var busiCd = o.value;
		if(busiCd=="AP01"){
			document.getElementById("isRefund").style.display="";
		}else{
			document.getElementById("isRefund").style.display="none";
		}
		if(busiCd == 'TP01'){
			document.all["destTxnSt"].disabled=true; 
		}else{
			document.all["destTxnSt"].disabled=false;
		}
	}
	
	$(document).ready(function(){
		$("#busiCd").change(function(){
			var busiCd = $(this).val();
			switch (busiCd) {
			case 'AC01':
				$("#destTxnSt option:gt(0)").remove();
				$("<option value='0'><s:text name="destTxnSt.0" /></option>").appendTo($("#destTxnSt"));
				$("<option value='3'><s:text name="destTxnSt.3" /></option>").appendTo($("#destTxnSt"));
				$("<option value='1'><s:text name="destTxnSt.1" /></option>").appendTo($("#destTxnSt"));
				$("<option value='2'><s:text name="destTxnSt.2" /></option>").appendTo($("#destTxnSt"));
				$("#isRefund").hide();
				break;
			case 'AP01':
				$("#destTxnSt option:gt(0)").remove();
				$("<option value='0'><s:text name="destTxnSt.0" /></option>").appendTo($("#destTxnSt"));
				$("<option value='3'><s:text name="destTxnSt.3" /></option>").appendTo($("#destTxnSt"));
				$("<option value='a'><s:text name="destTxnSt.1" /></option>").appendTo($("#destTxnSt"));
				$("#isRefund").show();
				break;
			case 'YZ01':
				$("#destTxnSt option:gt(0)").remove();
				$("<option value='0'><s:text name="destTxnSt.0" /></option>").appendTo($("#destTxnSt"));
				$("<option value='3'><s:text name="destTxnSt.3" /></option>").appendTo($("#destTxnSt"));
				$("<option value='1'><s:text name="destTxnSt.1" /></option>").appendTo($("#destTxnSt"));
				$("<option value='2'><s:text name="destTxnSt.2" /></option>").appendTo($("#destTxnSt"));
				$("#isRefund").hide();
				break;
			case 'TP01':
				$("#destTxnSt option:gt(0)").remove();
				$("#isRefund").hide();
				break;
			default:
				break;
			}
		})
	})
</script>
</head>
<body  onload="resizeAll();"  style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
<s:form name="form1" action="" method="post" target="data_eara_frame_down" theme="simple">
	<table>
        <s:actionerror/>
        <tr>
          	<td align="right"><s:text name="txnQuery.form.from" />:</td>
            <td><input type="text" class="Wdate" name="txnlog.startDt" id="fromDate" value="<%=dateTimeStr2 %>" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})"/> </td>
         	<td align="right">&nbsp;<s:text name="txnQuery.form.to" />:</td>
         	<td><input type="text" class="Wdate" name="txnlog.endDt" id="toDate" value="<%=dateTimeStr %>" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})"/> </td>
        	<td align="right"><s:text name="txnQuery.form.filename" />:</td>
            <td><s:textfield name="txnlog.SRC_ORDER_NO" id="srcOrderNo" /></td>
         </tr>
         <tr>
         	<td align="right"><s:text name="txnQuery.form.account" />:</td>
            <td><s:textfield name="txnlog.DEBIT_ACNT_NO" id="dOrcAccountNo" /></td>
            <td align="right"><s:text name="txnQuery.form.acntNm" />:</td>
            <td><s:textfield name="txnlog.ID_NO" id="idNo" /></td>
            <td align="right"><s:text name="txnQuery.form.amtRange" />:</td>
            <td  colspan="2"><s:textfield name="txnlog.minAmt" id="minAmt" cssStyle="width:80px;" onblur="checkAmt(this.value)" />-<s:textfield name="txnlog.maxAmt" id="maxAmt" cssStyle="width:80px;" onblur="checkAmt(this.value)"/></td>
         </tr>
        <tr>
            <td align="right"><s:text name="txnQuery.form.busiCd" />:</td>
            <td>
            	<select id="busiCd" name="txnlog.BUSI_CD"  STYLE="width: 150px">
            		<option value="AC01"><s:text name="busiCd.AC01" /></option>
            		<option value="AP01"><s:text name="busiCd.AP01" /></option>
            		<option value="TP01"><s:text name="busiCd.TP01" /></option>
            		<option value="YZ01"><s:text name="busiCd.YZ01" /></option>		
            	</select>
            </td>
            <td align="right"><s:text name="txnQuery.form.status" />:</td>
            <td>
            	<select id="destTxnSt" name="txnlog.DEST_TXN_ST" STYLE="width: 150px">
            		<option value=""><s:text name="txnQuery.form.emptySelect" /></option>
            		<option value="0"><s:text name="destTxnSt.0" /></option>
            		<option value="3"><s:text name="destTxnSt.3" /></option>
            		<option value="1"><s:text name="destTxnSt.1" /></option>
            		<option value="2"><s:text name="destTxnSt.2" /></option>
            	</select>
            </td>
            <td colspan="2" style="display: none;" id="isRefund">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="txnQuery.form.refund" />:
	            <select name="txnlog.CANCEL_IND">
	            	<option value=""><s:text name="txnQuery.form.emptySelect" /></option>
	            	<option value="1"><s:text name="txnQuery.form.refundYes" /></option>
	            	<option value="0"><s:text name="txnQuery.form.refundNo" /></option>
	            </select>
            </td>
        </tr>
        <tr>
        	<td colspan="6" align="right">
	            <input type="button" onclick="query();" class="type_button" id="queryBtn" value="<s:text name="txnQuery.form.queryBtn" />"/> 
	            <input type="button" onclick="download();" class="type_button" id="downloadBtn" value="<s:text name="txnQuery.form.DownloadBtn" />"/>
	            <font color="red">注：平台只供查询半年内交易，请及时下载！</font>
            </td>
        </tr>
	</table>
</s:form>
</div>
</body>
</html>