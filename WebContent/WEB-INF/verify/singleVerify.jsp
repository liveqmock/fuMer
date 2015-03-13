<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统|单笔代收</title>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.validate.js" type="text/javascript"></script>
<link href="<%=root%>/styles/stylediv.css" rel="stylesheet" type="text/css" />
<STYLE type="text/css">

.select {
	width:360px; 
	height:19px; 
	border:#7f9db9 1px solid;
	margin-right:5px;
}
.type_button {
	background: url(<%=root%>/images/buttonbg.gif ) repeat-x;
	text-align: center;
	color: #FFFFFF;
	border: #7fb5ea 1px solid;
	padding: 0px 12px 2px 12px !important;
	padding: 1px 8px 1px 8px;
	height: 22px !important;
	height: 23px;
	cursor: pointer;
}
body, html,select {
	width: 100%;
	border: 0px;
	padding: 0px;
	margin: 0px;
	line-height: 20px;
	font-size: 12px;
	color: #333333;
	background-color: #FFFFFF;
}
.type_text {
	width: 120px;
	height: 19px;
	border: #7f9db9 1px solid;
	margin-right: 5px;
}
</STYLE>
<script type="text/javascript">

//添加常用联系人
function createUser(){
	var userName=document.getElementById("userName");
	var dedNum=document.getElementById("dedNum");
	var bank=document.getElementById("bank");
	var custmrNoTp=document.getElementById("certtp");
	var custmrNo=document.getElementById("certno");
	var s=/^\d+$/;
	var s2=/^\d+$/;
	if(""==bank.value){
		alert("请选择开户银行");
		bank.focus();
		return;
	}
	if(""==userName.value){
		alert("请填写户名");
		userName.focus();
		return;
	}
	if(""==dedNum.value){
		alert("请填写收款人银行账号");
		dedNum.focus();
		return;
	}
	if(!s.test(dedNum.value)){
		alert("收款人银行账号输入有误");
		dedNum.focus();
		return;
	}
	var le=dedNum.value.length;
	if(le<10||le>30){
		alert("收款人银行账号必须在10-30位之间");
		dedNum.focus();
		return;
	}
	if(custmrNoTp.value!=""){
		if(custmrNo.value==""){
			alert("请输入证件号");
			custmrNo.focus();
			return;
		}
	}
	var strUrl="?user.USER_NAME="+encodeURI(encodeURI(userName.value))+"&user.ACCT_NO="+dedNum.value+"&user.BANK_CD="+encodeURI(encodeURI(bank.value))+
	"&user.CUSTMR_NO_TP="+custmrNoTp.value+"&user.CUSTMR_NO="+custmrNo.value;
	$.ajax({
		type :"post",
        url :"<%=root%>/createUser.action"+strUrl,
        dataType :"json",
        success :function (data){
        	if(data=="0"){
				alert("添加成功");
            }else if(data=="1"){
            	alert("添加失败");
            }else if(data=="2"){
            	alert("该联系人已存在");
            }
           }
	});
}

$(function(){
	$("#verifyreq").validate({
		rules:{
			bank:{
				required:true
			},
			dedNum:{
				required:true,
				reg:"\\d{10}([0-9]{1,18})?"
			},
			userName:{
				required:true
			}	
		},
		messages:{
			bank:{
				required:"[请选择开户行]"
			},
			dedNum:{
				required:"[请输入银行账号]",
				reg:"[银行账号不正确]"
			},
			userName:{
				required:"[请输入户名]"
			}
		}
	});
});

function getUserView(strUrl){
	var divView=document.getElementById("userViewDiv");
	divView.innerHTML="";
   //var left = ((document.documentElement.clientWidth - 402) / 2)+"px";
   //var top = ((document.documentElement.clientHeight - 200) / 2 + document.documentElement.scrollTop)-98 + "px";
   //divView.style.left = left;
   //divView.style.top = top;
	//alert("left:"+left+"   top:"+top);
	$.ajax({
		type :"post",
        url :"<%=root%>/userView.action"+strUrl,
        dataType :"json",
        success :function (data){
        	divView.innerHTML=data;
        	divView.style.display="block";
        	document.getElementById("condition").style.display="none";
           }
	});
}
function query(){
	var userName=document.getElementById("user.USER_NAME").value;
	var acctNo=document.getElementById("user.ACCT_NO").value;
	var mobileNo=document.getElementById("user.MOBILE_NO").value;
	var url="?user.USER_NAME="+encodeURIComponent(encodeURIComponent(userName))+"&user.ACCT_NO="+encodeURIComponent(encodeURIComponent(acctNo))
	+"&user.MOBILE_NO="+encodeURIComponent(encodeURIComponent(mobileNo));
	getUserView(url);
}
function onfcusColor(o,i){
	var indexNum=document.getElementById("indexNum");
	var listSize=document.getElementById("listSize").value;
	if("#3370ac"==o.style.background){
		o.style.background="#ffffcc";
		indexNum.value="";
	}else{
		indexNum.value=i;
		o.style.background="#3370ac";
		for(var j=0;j<listSize;j++){
			if(i!=j){
				document.getElementById("utr"+j).style.background="#ffffcc";
			}
		}
	}
}
function seletedUser(){
	var index=document.getElementById("indexNum").value;
	if(""==index){
		alert("请双击要选中的行");
		return;
	}
	var userName=document.getElementById("user"+index).innerHTML;
	var bank=document.getElementById("bank"+index).value;
	var acctNo=document.getElementById("acctNo"+index).innerHTML;
	var mobileNo=document.getElementById("mobileNo"+index).innerHTML;
	var custmrNoTp=document.getElementById("custmrNoTp"+index).value;
	var custmrNo=document.getElementById("custmrNo"+index).innerHTML;

	var banks=document.getElementById("bank");
	var certificateTp=document.getElementById("certtp");
	for(var a=0;a<banks.length;a++){
		if(banks[a].value==bank.substring(0,4)){
			banks[a].selected=true;
			break;
		}
	}
	for(var b=0;b<certificateTp.length;b++){
		if(certificateTp[b].value==custmrNoTp){
			certificateTp[b].selected=true;
			break;
		}
	}
	document.getElementById("userName").value=userName;
	document.getElementById("dedNum").value=acctNo;
	document.getElementById("certno").value=custmrNo;
}
function updateUser(){
	var index=document.getElementById("indexNum").value;
	if(""==index){
		alert("请双击要编辑的行");
		return;
	}
	var rowId=document.getElementById("indexId"+index).value;
	open("<%=root%>/userInfoMgr_eidtUser.action?user.ROW_ID="+rowId);
}
function deleteUser(url){
	var index=document.getElementById("indexNum").value;
	if(""==index){
		alert("请双击要删除的行");
		return;
	}
	var rowId=document.getElementById("indexId"+index).value;
	if(confirm("确定要删除吗?")){
		$.ajax({
			type :"post",
	        url :"<%=root%>/userInfoMgr_deleteUser.action?user.ROW_ID="+rowId+"&ajaxDelete=deleteUser",
	        dataType :"json",
	        success :function (data){
	        	if(data=="0"){
					alert("删除成功");
	            }else if(data=="1"){
	            	alert("删除失败");
	            }
	        }
		});
		getUserView(url);
	}
}
function getPageNum(url){
	var pagecount=document.getElementById("taozhuang")
	if(pagecount.value==""){
        alert("请输入要跳转的页面的数量!");
        return;
   }
   if(isNaN(pagecount.value)){
        alert("对不起,要跳转的数量只能为数字!");
        return ;
   }
   if(pagecount<1){
   	   alert("对不起,要跳转的数量不能小于1!");
       return ;
   }
	var pageNum=document.getElementById("taozhuang").value;
	var strUrl="?pageNum="+pageNum+url;
	getUserView(strUrl)
	
}
function displayTable(){
	document.getElementById("userViewDiv").style.display="block";
	document.getElementById("condition").style.display="none";
	document.getElementById("userViewDiv").style.display="none";
	document.getElementById("condition").style.display="block";
}
//function checkCertno(){
//	if(document.getElementById("certtp").value!=""){
//		if(document.getElementById("certno").value==""){
//			document.getElementById("cerFont").innerHTML="[请填写证件号]";
//			document.getElementById("submit1").disabled=true;
//		}else{
//			document.getElementById("cerFont").innerHTML="";
//			document.getElementById("submit1").disabled=false;
//		}
//	}else{
//		document.getElementById("cerFont").innerHTML="";
//		document.getElementById("submit1").disabled=false;
//	}
//}
</script>
</head>
<body onload="resizeAll();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition" style="display: block;">
<form name="verifyreq" id="verifyreq" method="post" id="verifyreq" action="verify_simpleCommit.action" target = "struFrame">
<table>
<tr>
	<td align="right">
		开户行:
    </td>
	<td>
		<select id="bank" name="bank" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;">
		<option value="">--请选择--</option>
		<s:iterator var="banks" value="#tRootBankInfs">
		<option value="<s:property value="#banks.BANK_CD"/>"><s:property value="#banks.BANK_NM"/></option>
		</s:iterator>
		</select><font color="red">*</font>
   </td>
</tr>
<tr>
	<td align="right">
		银行账号:
	</td>
	<td>
		<input type="text" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  maxlength="28" id="dedNum" name="dedNum" />
		<font color="red">*</font>
	</td>
</tr>
<tr>
			<td align="right">账户名称:</td>
			<td>
				<input type="text" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="60" id="userName" name="userName" />
				<font color="red">*</font>
			</td>
		</tr>
		<tr>
            <td align="right">
            	证件类型:
            </td>
            <td>
            <select name="certtp" id="certtp" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;">
            <option value="">请选择证件类型</option>
			<option value="0">身份证</option>
			<option value="1">护照</option>
			<option value="2">军官证</option>
			<option value="3">士兵证</option>
			<option value="5">户口本</option>
			<option value="7">其他</option>
			</select>
            </td>
        </tr>
		<tr>
			<td align="right">
				证件号:
			</td>
			<td>
                <input type="text" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" id="certno" name="certno" />
                <font id="cerFont"></font>
            </td>
		</tr>
		<tr>
		<td></td>
  		<td align="left">
  			<input type="submit" id="submit1" style="width: 100px;" class="type_button" name="submit" value="确认" />
  			<input type="reset" style="width: 100px;" class="type_button" name="reset" value="重置" />
  		</td>
  		</tr>
  		<tr>
  		<td></td>
  		<td align="left">
  			<input type="button" style="width: 100px;" value="加入联系人" class="type_button" onclick="createUser()"/>
  			<input type="button" style="width: 100px;" value="选择联系人" class="type_button" onclick="getUserView('?pageNum=1')"/>
  		</td>
		</tr>
</table>
</form>
</div>
<div style="height: 250px;width: 800">
<div id="userViewDiv" class="table"></div>
</div>
</body>
<script type="text/javascript">

</script>
</html>