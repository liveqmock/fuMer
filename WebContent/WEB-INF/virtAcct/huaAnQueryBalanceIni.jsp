<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="fuiou" uri="/WEB-INF/fuiou.tld"%>
<%@ include file="/common.jsp"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.fuiou.mer.util.TDataDictConst"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/fht/styles/stylediv.css" rel="stylesheet" type="text/css" />
<style type="text/css">
	.type_text {   
	width: 120px;
	height: 19px;
	border: #7f9db9 1px solid;
	margin-right: 5px;
}
body, html {
	width: 100%;
	border: 0px;
	padding: 0px;
	margin: 0px;
	line-height: 20px;
	font-family: "锟斤拷锟斤拷";
	font-size: 12px;
	color: #333333;
	background-color: #FFFFFF;
}.type_button {
	background: url( <%=root %>/images/buttonbg.gif ) repeat-x;
	text-align: center;
	color: #FFFFFF;
	border: #7fb5ea 1px solid;
	padding: 0px 12px 2px 12px !important;
	padding: 1px 8px 1px 8px;
	height: 22px !important;
	height: 23px;
	cursor: pointer;
}
</style>
<link href="/fht/styles/jquery.autocomplete.css" rel="stylesheet" type="text/css" />
<link href="<%=root%>/styles/style.css" rel="stylesheet" type="text/css" />
<link href="styles/leftmenu.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/all.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=root %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<script language="javascript" src="<%=root %>/styles/iframe.js" type="text/javascript"></script>
<link href="<%=root %>/styles/suggestComboBox.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=root %>/js/suggest.js"></script>
<script type="text/javascript">
function changeSelectTp(){
	var div1 = document.getElementById("div1");
	var div2 = document.getElementById("div2");
	var divDisplay1 = div1.style.display;
	var divDisplay2 = div2.style.display;
	div1.style.display = divDisplay2;
	div2.style.display = divDisplay1;
}
function initSelectTpValue(){
	var div1 = document.getElementById("div1");
	var div2 = document.getElementById("div2");
	var selectTpValue = "${selectTp}";
	if(selectTpValue == 1){
		div1.style.display = "none";
		div2.style.display = "block";
	}else if(selectTpValue == 2){
		div1.style.display = "block";
		div2.style.display = "none";
	}
}
function checkMchntInf(){
	var mchntCd_tag = document.getElementById("mchntCd_tag").value;
	if(mchntCd_tag == ""){
		document.getElementById("mchntCd_tag").focus();
		alert("分公司商户名称不能为空！");
		return;
	}
	$.post("<%=root %>/virtacct_checkMchntInf.action",{mchntCd_tag:mchntCd_tag},function(response){
		if(parseInt(response) == -1){
			document.getElementById("mchntCd_tag").focus();
			alert("没有找到该分公司！");
			return;
		}
	},'json');
	document.getElementById("iframeId").src="virtacct_getMchntRlatBlac.action?selectTp=2&mchntCd_tag="+encodeURIComponent(encodeURIComponent(mchntCd_tag));
	document.getElementById("iframeDiv").style.display = "block";
}
function submitForm(){
	var mchntCd = document.getElementById("mchntCd").value;
	document.getElementById("iframeId").src="virtacct_getMchntRlatBlac.action?selectTp=1&mchntCd="+mchntCd;
	document.getElementById("iframeDiv").style.display = "block";
}
</script>
<title>余额查询</title>
</head>
<body onload="resizeAll();initSelectTpValue();" style="padding: 0px; margin: 0px;">
	<s:form method="post" action="virtacct_getMchntRlatBlac.action" theme="simple" id="form">
		<div style="background-color: #e8eef7;">
		<div style="background-color: #e8eef7;display: none;" id="div1">
		&nbsp;&nbsp;分公司商户名称：
		<fuiou:suggestCombBox name="mchntCd_tag" id = "mchntCd_tag"  busiType="mchnt"  value="${mchntCd_tag}" wildcard="15" size="45"></fuiou:suggestCombBox>
		<input type="button" value="查询" class="type_button" onclick="checkMchntInf();"/>
		<input type="button" value="切换查询方式" class="type_button" onclick="changeSelectTp();"/>
		</div>
		<div style="background-color: #e8eef7;display: block;" id="div2">
		&nbsp;&nbsp;分公司商户名称：
		<select id="mchntCd" name="mchntCd">
			<option value="">--请选择分公司商户名称--</option>
			<s:iterator var="list" value="#insInfCacheBeans">
			<option value="<s:property value="#list.MCHNT_CD"/>"><s:property value="#list.INS_NAME_CN"/></option>
			</s:iterator>
		</select>
		<input type="button" value="查询" class="type_button"/ onclick="submitForm();">
		<input type="button" value="切换查询方式" class="type_button" onclick="changeSelectTp();"/>
		</div>
		</div>
	</s:form>   
	<div style="display: none; text-align: center; padding-left: 4px; padding-top:4px;height:100%; width: 100%;overflow: auto;" id="iframeDiv">
		<iframe id="iframeId" src="" style="height: 400px;width: 100%;" frameborder="0" scrolling="auto"></iframe>
	</div>
</body>
</html>