<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.fuiou.mer.util.TDataDictConst"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>当日交易查询</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
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
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>


<script type="text/javascript">
	function query(){
		document.form1.action="queryCurr_execute.action";
		document.form1.submit();
	}
	function download(){
		document.form1.action="downloadHis.action";
		document.form1.submit();
	}
	function busiChange(o){
		if(o.value=="AP01"){
			document.getElementById("destTxnSt").disabled="true";
		}else{
			document.getElementById("destTxnSt").removeAttribute("disabled");
		}
	}
</script>
</head>
<body onload="resizeAll();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<div id="condition">
<tr>当日交易查询</tr>
<s:form name="form1" action="queryCurr_execute.action" method="post" target="data_eara_frame_down" theme="simple">
	<table>
		<s:actionerror />
		<tr>
			<td align="right" nowrap="nowrap"><s:text name="业务类型：" /></td>
	        <td><s:select list="#request.busiCdMap" onchange="" id="busiCd" name="busiCd" cssStyle="width:100px;"></s:select></td>
			<td><s:property value="fieldErrors.dedNum" /></td>
			<td align="right" nowrap="nowrap"><s:text name="文件名：" /></td>
			<td><s:textfield name="srcOrderNo" id="srcOrderNo" /></td>
			<td><s:property value="fieldErrors.srcOrderNo" /></td>
			<td align="right" nowrap="nowrap"><s:text name="交易状态：" /></td>
			<td><s:select list="#request.destTxnStMap" id="destTxnSt" name="destTxnSt"></s:select></td>
			<td><s:property value="fieldErrors.destTxnSt" /></td>
			<td>是否退票：
            <select name="cancelInd">
            	<option value="">全部</option>
            	<option value="1">是</option>
            	<option value="0">否</option>
            </select>
            </td>
		</tr>
		<tr>
			<td align="right" nowrap="nowrap"><s:text name="户名：" /></td>
			<td><s:textfield name="idNo" id="idNo" cssStyle="width:95px;"/></td>
			<td><s:property value="fieldErrors.idNo" /></td>
			<td align="right" nowrap="nowrap"><s:text name="扣款账号：" /></td>
			<td><s:textfield name="dOrcAccountNo" id="dOrcAccountNo" /></td>
			<td><s:property value="fieldErrors.dOrcAccountNo" /></td>
			<td align="right" nowrap="nowrap"><s:text name="金额范围：" /></td>
			<td><s:textfield name="minAmt" id="minAmt" onblur="checkAmt(this.value)" cssStyle="width:80px;"/>-</td>
			<td><s:textfield name="maxAmt" id="maxAmt" onblur="checkAmt(this.value)" cssStyle="width:80px;"/>元</td>
			<td> <button onclick="query();" class="type_button" id="queryBtn">查询</button>
            	&nbsp;&nbsp;&nbsp;<button onclick="download();" class="type_button" id="downloadBtn">下载</button>
            </td>
		</tr>
	</table>
</s:form>
</div>
</body>
<script type="text/javascript">
	function check(){
		var busicd = $("#busiCd").val();
		if(busicd == 'TP01'){
			document.all[ "destTxnSt"].disabled=true; 
		}else{
			document.all[ "destTxnSt"].disabled=false;
		}
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
</script>
</html>