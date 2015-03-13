<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统|信用卡单笔收款</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.validate.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<link href="<%=root%>/styles/stylediv.css" rel="stylesheet" type="text/css" />

<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript">

$(function(){
	$("#daishou").validate({
		rules:{
			userName:{
				required:true
			},
			dedNum:{
				required:true,
				reg:"\\d+",
				minlength:10
			},
			amount:{
				required:true,
				reg:"[0-9]\\d*([.]\\d{2}){0,1}",
				min:1,
				max:10000.00
			},
			certificateTp:{
				required:true
			},
			certificateNo:{
				required:true
			},
			phoneNum:{
				required:false,
				reg:"1[34589]\\d{9}"
			},
			validityDt:{
				required:false,
				reg:"\\d+"
			},
			cvv2:{
				required:false,
				reg:"\\d+"
			}
		},
		messages:{
			userName:{
				required:"[持卡人户名不能为空]"
			},
			dedNum:{
				required:"[持卡人卡号不能为空]",
				reg:"[持卡人卡号只能为数字]",
				minlength:"[持卡人卡号长度不能小于10位]"
			},
			amount:{
				required:"[金额不能为空]",
				reg:"[金额格式不正确]",
				min:"[金额不能小于1元]",
				max:"[金额不能超过10000元]"
			},
			certificateTp:{
				required:"[请选择证件类型]"
			},
			certificateNo:{
				required:"[请选择证件号]"
			},
			phoneNum:{
				reg:"[手机格式不正确]"
			},
			validityDt:{
				reg:"[有效日期只能为数字]"
			},
			cvv2:{
				reg:"[cvv2只能为数字]"
			}
		}
	});
});

function checkCradTp(){
	var certificateTp=document.getElementById("certificateTp");
	for(var i=0;i<certificateTp.length;i++){
		if(certificateTp[i].value==""){
			certificateTp[i].selected=true;
			break;
		}
	}
	displayTable();
}

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

	
	var certificateTp=document.getElementById("certificateTp");
	for(var b=0;b<certificateTp.length;b++){
		if(certificateTp[b].value==custmrNoTp){
			certificateTp[b].selected=true;
			break;
		}
	}
	document.getElementById("userName").value=userName;
	document.getElementById("dedNum").value=acctNo;
	document.getElementById("phoneNum").value=mobileNo;
	document.getElementById("certificateNo").value=custmrNo;
}
//添加常用联系人
function createUser(){
	var userName_nm=document.getElementById("userName");
	var cardNo_nm=document.getElementById("dedNum");
	var phoneNum_nm=document.getElementById("phoneNum");
	var custmrNoTp=document.getElementById("certificateTp");
	var custmrNo=document.getElementById("certificateNo");
	var s=/^\d+$/;
	var s2=/^1[34589]\d{9}$/;
	
	if(""==userName_nm.value){
		alert("请填写户名");
		userName_nm.focus();
		return;
	}
	if(""==cardNo_nm.value){
		alert("请填写收款人银行账号");
		cardNo_nm.focus();
		return;
	}
	if(!s.test(cardNo_nm.value)){
		alert("收款人银行账号输入有误");
		cardNo_nm.focus();
		return;
	}
	var le=cardNo_nm.value.length;
	if(le<10||le>30){
		alert("收款人银行账号必须在10-30位之间");
		cardNo_nm.focus();
		return;
	}
	if(phoneNum_nm.value!=""){
		if(!s2.test(phoneNum_nm.value)){
			alert("您输入的手机号码有误");
			phoneNum_nm.focus();
			return;
		}
	}
	if(custmrNoTp.value!=""){
		if(custmrNo.value==""){
			alert("请输入证件号");
			custmrNo.focus();
			return;
		}
	}
	var strUrl="?user.USER_NAME="+encodeURIComponent(encodeURIComponent(userName_nm.value))+"&user.ACCT_NO="+cardNo_nm.value+"&user.MOBILE_NO="+phoneNum_nm.value+
	"&user.CUSTMR_NO_TP="+custmrNoTp.value+"&user.CUSTMR_NO="+custmrNo.value;;
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
	getUserView(strUrl);
	
}
function displayTable(){
	document.getElementById("userViewDiv").style.display="block";
	document.getElementById("condition").style.display="none";
	document.getElementById("userViewDiv").style.display="none";
	document.getElementById("condition").style.display="block";
}
function checkCard(cardNo){
	if(cardNo==''||10>cardNo.length){
		document.getElementById("checkMs").innerHTML="";
		return;
	}
//	var issId='${param.issId}';
	$.ajax({
		type :"post",
        url :"<%=root%>/credCard_checkCard.action?cardNo="+cardNo,
        dataType :"text",
        success :function (data){
        	var obj = data.split("|");
        	$("#checkMs").text(obj[1]);
        	if(obj[0]=='0'){
        		$("#submit").attr("disabled",true); 
        	}else{
        		$("#submit").attr("disabled",false); 
        	}
        }
	});
}
</script>
</head>
<body onload="resizeAll();checkCradTp()" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition" style="display: block;">
<s:form name="daishou" method="post" id="daishou" action="credCard_execute.action" target = "struFrame" theme="simple">
<table>
<tr>
	<td align="right">
		持卡人账号:
	</td>
	<td>
		<s:textfield cssClass="type_text" onblur="checkCard(this.value)" onkeyup="if(this.value==''||10>this.value.length){document.getElementById('checkMs').innerHTML='';}" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  maxlength="19" name="dedNum" id="dedNum"/>
		<span id="checkMs"></span>
		<font color="red">*</font>
	</td>
</tr>
<tr>
		<td align="right">持卡人户名:</td>
		<td>
			<s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" id="userName" name="userName" />
			<font color="red">*</font>
		</td>
</tr>
		<tr>
            <td align="right">
            	金额(单位:元):
            </td>
            <td>
                <s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="10" name="amount" id="amount"/>
                <font color="red">*</font>
            </td>
        </tr>
		<tr>
			<td align="right">
				证件类型:
			</td>
			<td>
                <select name="certificateTp" id="certificateTp" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;">
				    <option value="">--请选择证件号--</option>
				    <option value="0">身份证</option>
				    <option value="1">护照</option>
				    <option value="2">军官证</option>
                	<option value="3">士兵证</option>
				    <option value="4">回乡证</option>
				    <option value="6">户口本</option>
				    <option value="7">其他</option>
                </select>
                <font color="red">*</font>
            </td>
		</tr>
		<tr>
			<td align="right">
				证件号:
			</td>
			<td>
                <s:textfield cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="20" name="certificateNo" id="certificateNo"/>
                <font color="red">*</font>
            </td>
		</tr>
		<tr>
			<td align="right">
				手机号:
			</td>
			<td>
                <s:textfield  cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="11" id="phoneNum" name="phoneNum" />
            </td>
		</tr>
		<tr>
			<td align="right">
				有效期:
			</td>
			<td>
                <s:textfield cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="4" id="validityDt" name="validityDt"/>
           </td>
		</tr>
		<tr>
			<td align="right">
				CVV2:
			</td>
			<td>
                <s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="3" id="cvv2" name="cvv2" />
            </td>
		</tr>
		<tr>
			<td align="right">
				备注1:
			</td>
			<td>
                <s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" name="remark1" id="remark1"/>
            </td>
		</tr>
		<tr>
			<td align="right">
				备注2:
			</td>
			<td>
                <s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" name="remark2" id="remark2"/>
            </td>
		</tr>
		<tr>
		<td></td>
  		<td align="left">
  			<s:submit cssClass="type_button" cssStyle="width: 100px;" id="submit" name="submit" value="确 认"/>
  			<s:reset cssClass="type_button" cssStyle="width: 100px;" name="reset" value="重置" />
  		</td>
  		</tr>
  		<tr>
  		<td></td>
  		<td align="left">
  			<input type="button" value="加入联系人" style="width: 100px;" class="type_button" onclick="createUser()"/>
  			<input type="button" value="选择联系人" style="width: 100px;"  class="type_button" onclick="getUserView('?pageNum=1')"/>
  			<input class="type_button" style="width: 100px;" type="button" onclick="history.go(-1)" value="返回"/>
  		</td>
		</tr>
</table>
</s:form>
</div>
<div style="height: 110px;width: 800">
<div id="userViewDiv" class="table"></div>
</div>
</body>
<script type="text/javascript">

</script>
</html>