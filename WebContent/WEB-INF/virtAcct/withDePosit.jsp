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
<script type="text/javascript">
	function init(){
		$(document).ready(function(){
			var acnt = "${acntNo}";
			$.post("<%=root %>/queryBlac.action",{acnt:acnt},function(response){
				if(parseInt(response) == -1){
					alert("查询出错！");
				}else{
					var result = response.replace("\"","");
					var arrays = result.split("|",4);
					$("#balance1").text(arrays[1]+'元');
					document.getElementById("sumAmt").value=arrays[1];
					$("#div").show();					
				}
			},'json');
		});
	}

	function checkForm(){
		var amount = document.getElementById("amount");
		var enpSeriaNo = document.getElementById("enpSeriaNo");
		var sumAmt = document.getElementById("sumAmt").value;
		var mobile = document.getElementById("mobile");
		var formula1 = /^[a-zA-Z0-9]*$/;
		var formula2 = /^1[34589]\d{9}$/;
		//alert(sumAmt);
		if(amount.value == ""){
			alert("提现金额不能为空");
			amount.focus();
			return false;
		}
		var test=/^[0-9]\d*([.]\d{2}){0,1}$/;
		if(!test.test(amount.value)){
			alert("请输入正确的提现金额");
			amount.focus();
			return false;
		}
		if((amount.value)*100>sumAmt*100){
			alert("提现金额不能大于可用余额");
			amount.focus();
			return false;
		}
		if(enpSeriaNo.value != ""){
			if(!formula1.test(enpSeriaNo.value)){
				alert("请输入正确的企业流水号");
				enpSeriaNo.focus();
				return false;
			}
		}
		if(mobile.value != ""){
			if(!formula2.test(mobile.value)){
				alert("请输入正确的手机号码");
				mobile.focus();
				return false;
			}
		}
		return true;
	}
	
</script>
<title>提现</title>
</head>
<body onload="resizeAll();init();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
	<s:form  method="post" action="withdeposit_submitWith.action" theme="simple" id="form" onsubmit="return checkForm()">
		<input type="hidden" name="sumAmt" id="sumAmt" value=""/>
		<div style="display: none;" id="div"> 
			<!-- &nbsp;&nbsp;请选择账户：
			<select id="acnt" name="acnt" onchange="init()">
				<s:iterator value="acntNos">
					<option value='<s:property/>'><s:property/></option>
				</s:iterator>
			</select><br/> -->
			<input type="hidden" id="cityCode" name="icfia.cityCode" value="${tCustCwdAcnt.CITY_CD}"/>
			<input type="hidden" id="bankCd" name="icfia.bankCd" value="${tCustCwdAcnt.BANK_ROOT_CD}"/>
			<table>
			<tr>
				<td colspan="2">
					${msg}
			   </td>
			</tr>
			<tr>
				<td align="right">
					可用余额:
			    </td>
				<td>
					<label id="balance1" style="font-weight: bold;"></label>
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
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="issBankName" name="icfia.bankNam" value="${tCustCwdAcnt.ISS_BANK_NM}"/>
				</td>
			</tr>
			<tr>
				<td align="right">开户行行号:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="bankNam"  value="${tCustCwdAcnt.INTER_BANK_NO}"/>
				</td>
			</tr>
			<tr>
				<td align="right">收款人账号:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="bankAccount" name="icfia.bankAccount" value="${tCustCwdAcnt.OUT_ACNT_NO}"/>
				</td>
			</tr>
			<tr>
				<td align="right">收款人户名:</td>
				<td>
					<input readonly="readonly" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="accountName" name="icfia.accountName" value="${tCustCwdAcnt.OUT_ACNT_NM}"/>
				</td>
			</tr>
			<tr>
				<td align="right">提现金额(元):</td>
				<td>
					<input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="amount" name="icfia.amount" value=""/>
					<font style="color: red;">*</font>
				</td>
			</tr>
			<tr>
				<td align="right">企业流水号:</td>
				<td>
					<input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="enpSeriaNo" name="icfia.enpSeriaNo" maxlength="80" value=""/>
				</td>
			</tr>
			<tr>
				<td align="right">备注:</td>
				<td>
					<input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="memo" name="icfia.memo" maxlength="30" value=""/>
				</td>
			</tr>
			<tr>
				<td align="right">手机号码:</td>
				<td>
					<input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" type="text" id="mobile" name="icfia.mobile" maxlength="11" value=""/>
				</td>
			</tr>
			<tr>
				<td align="right"></td>
				<td>
					<input type="submit" value="提现" class="type_button"/>
				</td>
			</tr>
			</table>
		</div> 
	
	</s:form>   
</div>	
</body>
</html>