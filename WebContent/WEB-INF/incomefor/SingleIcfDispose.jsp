<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"  %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>富友商户系统|结果页面</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<STYLE type="text/css">

.select {
	width:360px; 
	height:19px; 
	border:#7f9db9 1px solid;
	margin-right:5px;
}
</STYLE>
<%

	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
	String message = (String)request.getAttribute("msg");
%>
<script type="text/javascript">
function card(){
	var cardTp=document.getElementById("cardTp");
	var certtp=document.getElementById("certificateTp");
	if(certtp.value=="0"){
		cardTp.value="身份证";
	}else if(certtp.valu=="1"){
		cardTp.value="护照";
	}else if(certtp.valu=="2"){
		cardTp.value="军官证";
	}else if(certtp.valu=="3"){
		cardTp.value="士兵证";
	}else if(certtp.valu=="5"){
		cardTp.value="户口本";
	}else if(certtp.valu=="7"){
		cardTp.value="其他";
	}
}
</script>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
</head>
<body onload="resizeAll();card();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<div id="condition">

	<table>
	<tr>
		<td><%=message==null?"":"交易状态："+message%></td>
		<td></td>
	</tr>
	<tr>
        <td><s:text name="流水号:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{serialNumber}" disabled="true"/></td>
    </tr>
    
    <tr>
        <td><s:text name="处理状态:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{processState}" disabled="true"/></td>
    </tr>

    <tr>
        <td><s:text name="扣款人开户行名称:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{bankOfDepositName}" disabled="true"/></td>
    </tr>

    
    <tr>
        <td><s:text name="扣款人银行帐号:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.bankAccount}" disabled="true"/></td>
    </tr>
    
    <tr>
        <td><s:text name="户名:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.accountName}" disabled="true"/></td>
    </tr>
    
    <tr>
        <td><s:text name="金额(单位:元):" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.amount}" disabled="true"/></td>
    </tr>
    
    <tr>
        <td><s:text name="企业流水号:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.enpSeriaNo}" disabled="true"/></td>
    </tr>
    
    <tr>
        <td><s:text name="备注:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.memo}" disabled="true"/></td>
    </tr>
    
    <tr>
        <td><s:text name="手机号:" /></td>
        <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.mobile}" disabled="true"/></td>
    </tr>
    <tr>
		<td><s:text name="证件类型:" /></td>
		<td><s:textfield id="cardTp" name="cardTp" cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="" disabled="true"/>&nbsp;</td>
		<s:hidden name="certificateTp" id="certificateTp" value="%{accessBean.certificateTp}"></s:hidden>
	</tr>
	<tr>
		<td><s:text name="证件号:" /></td>
		<td><s:textfield id="certificate" name="certificate" cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.certificateNo}" disabled="true"/>&nbsp;</td>
	</tr>
	</table>
</div>
</body>
</html>