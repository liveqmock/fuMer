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
<title>富友商户系统|单笔代收</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/verify.js" type="text/javascript"></script>
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
function checkCradTp2(){
	var certificateTp=document.getElementById("certificateTp");
	var certificateNo=document.getElementById("daishou_certificateNo");
	if(certificateTp.value==""){
		certificateNo.disabled=true;
		certificateNo.value="";
	}else{
		certificateNo.removeAttribute("disabled");
	}
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

	var provs=document.getElementById("bankOfDeposit");
	var certificateTp=document.getElementById("certificateTp");
	for(var a=0;a<provs.length;a++){
		if(provs[a].value==bank.substring(0,4)){
			provs[a].selected=true;
			document.getElementById("daishou_certificateNo").removeAttribute("disabled");
			break;
		}
	}
	for(var b=0;b<certificateTp.length;b++){
		if(certificateTp[b].value==custmrNoTp){
			certificateTp[b].selected=true;
			break;
		}
	}
	document.getElementById("daishou_userName").value=userName;
	document.getElementById("daishou_dedNum").value=acctNo;
	document.getElementById("daishou_phoneNum").value=mobileNo;
	document.getElementById("daishou_certificateNo").value=custmrNo;
}
//添加常用联系人
function createUser(){
	var userName_nm=document.getElementById("daishou_userName");
	var cardNo_nm=document.getElementById("daishou_dedNum");
	var phoneNum_nm=document.getElementById("daishou_phoneNum");
	var selectBank_nm=document.getElementById("bankOfDeposit");
	var custmrNoTp=document.getElementById("certificateTp");
	var custmrNo=document.getElementById("daishou_certificateNo");
	var s=/^\d+$/;
	
	if(""==selectBank_nm.value){
		alert("请选择开户银行");
		selectBank_nm.focus();
		return;
	}
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
		if(!verifyMobile(phoneNum_nm.value)){
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
	"&user.BANK_CD="+encodeURIComponent(encodeURIComponent(selectBank_nm.value))+"&user.CUSTMR_NO_TP="+custmrNoTp.value+"&user.CUSTMR_NO="+custmrNo.value;;
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
	getUserView(strUrl)
	
}
function displayTable(){
	document.getElementById("userViewDiv").style.display="block";
	document.getElementById("condition").style.display="none";
	document.getElementById("userViewDiv").style.display="none";
	document.getElementById("condition").style.display="block";
}

</script>
</head>
<body onload="resizeAll();checkCradTp()" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition" style="display: block;">
<s:form name="daishou" method="post" id="daishou" action="singleIcfReq.action" target = "struFrame" theme="simple" onsubmit="return checkForm();">
<table>
 <s:property value="fieldErrors.msg"/>
<tr>
	<td align="right">
		开户行:
    </td>
	<td>
		<s:select cssStyle="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" list="#session.bankList" listKey="BANK_CD" listValue="BANK_NM" id="bankOfDeposit" name="accessBean.bankCd"  />
		<s:property value="fieldErrors.bankOfDeposit"/><font color="red">*</font>
   </td>
</tr>
<tr>
	<td align="right">
		扣款人银行账号:
	</td>
	<td>
		<s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  maxlength="28" name="accessBean.bankAccount" id="daishou_dedNum" />
		<s:property value="fieldErrors.dedNum"/><font color="red">*</font>
	</td>
</tr>
<tr>
			<td align="right">户名:</td>
			<td>
				<s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="60" name="accessBean.accountName" id="daishou_userName"/>
				<s:property value="fieldErrors.userName"/><font color="red">*</font>
			</td>
		</tr>
		<tr>
            <td align="right">
            	金额(单位:元):
            </td>
            <td>
                <s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="10" name="accessBean.amount" />
                <s:property value="fieldErrors.amount"/><font color="red">*</font>
            </td>
        </tr>
		<tr>
			<td align="right">
				企业流水号:
			</td>
			<td>
                <s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" name="accessBean.enpSeriaNo" />
                <s:property value="fieldErrors.enSerialNum"/>
            </td>
		</tr>
		<tr>
			<td align="right">
				备注:
			</td>
			<td>
                <s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="60" name="accessBean.memo" />
                <s:property value="fieldErrors.remark"/>
            </td>
		</tr>
		<tr>
			<td align="right">
				手机号:
			</td>
			<td>
                <s:textfield  cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="11" name="accessBean.mobile" id="daishou_phoneNum"/>
                <s:property value="fieldErrors.phoneNum"/>
                <s:if test="#session.isForceVerifyMobile == true">
                <input type="hidden" value="isForceVerifyMobile" value="1" id="isForceVerifyMobile"/>
                <font color="red">*</font></s:if>
            </td>
		</tr>
		<tr>
			<td align="right">
				证件类型:
			</td>
			<td>
                <select name="accessBean.certificateTp" id="certificateTp" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" onchange="checkCradTp2()">
                	<option value="">--请选择证件号--</option>
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
                <s:textfield disabled="true" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="20" name="accessBean.certificateNo" id="daishou_certificateNo"/>
                <font id="fontCertificateNo"></font>
            </td>
		</tr>
		<tr>
		<td></td>
  		<td align="left">
  			<s:submit cssClass="type_button" cssStyle="width: 100px;" name="submit" value="确 认"/>
  			<s:reset cssClass="type_button" cssStyle="width: 100px;" name="reset" value="重置" />
  		</td>
  		</tr>
  		<tr>
  		<td></td>
  		<td align="left">
  			<input type="button" value="加入联系人" style="width: 100px;" class="type_button" onclick="createUser()"/>
  			<input type="button" value="选择联系人" style="width: 100px;"  class="type_button" onclick="getUserView('?pageNum=1')"/>
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
   function checkForm(){
	   var isForceVerifyMobile =  document.getElementById("isForceVerifyMobile");
	   if(isForceVerifyMobile){
		   var mobile = document.getElementById("daishou_phoneNum").value;
			if(!verifyMobile(mobile)){
				alert("手机号码格式不正确");
				return false;
			}
	   }
	   return true;
   }
</script>
</html>