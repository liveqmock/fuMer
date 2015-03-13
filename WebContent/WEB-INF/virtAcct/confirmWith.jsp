<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.fuiou.mer.util.TDataDictConst"%>
<%
	response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<link href="<%=root%>/styles/stylediv.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>

<title>提现确认界面</title>
<script type="text/javascript">
	function disbButton(){
		document.getElementById("submit1").disabled=true;
		document.getElementById("button1").disabled=true;
		return true;
	}
</script>
</head>
<body onload="resizeAll();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
	<s:form  method="post" action="withdeposit_validater.action" theme="simple" id="form" onsubmit="return disbButton()">
		<s:token></s:token>
		<input type="hidden" name="sumAmt" id="sumAmt" value=""/>
		<div style="" id="div"> 
			<input type="hidden" id="cityCode" name="icfia.cityCode" value="${icfia.cityCode}"/>
			<input type="hidden" id="bankCd" name="icfia.bankCd" value="${icfia.bankCd}"/>
			<table>
			<tr>
				<td align="right">
					可用余额:
			    </td>
				<td>
					<label id="balance1" style="font-weight: bold;">${sumAmt}</label>
			   </td>
			</tr>
			<tr>
				<td align="right">开户银行:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="bankName" name="bankName" value="${BANK_NM}"/>
				</td>
			</tr>
			<tr>
				<td align="right">开户行名称:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="issBankName" name="icfia.bankNam" value="${icfia.bankNam}"/>
				</td>
			</tr>
			<tr>
				<td align="right">开户行行号:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="bankNam"  value="${icfia.bankNam}"/>
				</td>
			</tr>
			<tr>
				<td align="right">收款人账号:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="bankAccount" name="icfia.bankAccount" value="${icfia.bankAccount}"/>
				</td>
			</tr>
			<tr>
				<td align="right">收款人户名:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="accountName" name="icfia.accountName" value="${icfia.accountName}"/>
				</td>
			</tr>
			<tr>
				<td align="right">提现金额(元):</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="amount" name="icfia.amount" value="${icfia.amount}"/>
				</td>
			</tr>
			<tr>
				<td align="right">企业流水号:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="enpSeriaNo" name="icfia.enpSeriaNo" maxlength="80" value="${icfia.enpSeriaNo}"/>
				</td>
			</tr>
			<tr>
				<td align="right">备注:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="memo" name="icfia.memo" maxlength="30" value="${icfia.memo}"/>
				</td>
			</tr>
			<tr>
				<td align="right">手机号码:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="mobile" name="icfia.mobile" maxlength="11" value="${icfia.mobile}"/>
				</td>
			</tr>
			<tr>
				<td align="right"></td>
				<td>
					<input id="submit1" type="submit" value="确认" class="type_button"/>
					<input id="button1" type="button" value="取消" class="type_button"/>
				</td>
			</tr>
			</table>
		</div> 
	
	</s:form>   
</div>	
</body>
</html>