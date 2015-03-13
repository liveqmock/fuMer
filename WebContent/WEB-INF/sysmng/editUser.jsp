<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String dateTimeStr = sdf.format(dt);
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改常用联系人</title>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.validate.js" type="text/javascript"></script>

<script language="javascript" src="<%=root%>/js/jquery.autocomplete.js"></script>
<link href="<%=root%>/styles/jquery.autocomplete.css" rel="stylesheet" type="text/css" />
<style type="text/css">
	.error{
		color: red;
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
</style>
<script type="text/javascript">
function ajaxOnload(){
    $.ajax({
        type :"post",
        url :"<%=root%>/payfor/jsonPayfor_returnProvList.action",
        dataType :"json",
        success :function (data){
        	 var sf=document.getElementById("sf").value;
             var shi = document.getElementById("user.PROV_CODE");
             document.getElementById("user.PROV_CODE").options.add(new Option("0","--请选择--"));
             shi.length = 1;
              for(var x=0;x<data.length;x++){
                 var option=new Option(data[x].PROV_NM,data[x].PROV_CD);
                 document.getElementById("user.PROV_CODE").options.add(option);
                 if(option.value==sf){
                	 option.selected=true;
                 }
              }
              provOnchange(sf);
           }
    });
}

//省Onchange 填充市
function provOnchange(proCode){
    //选择的省ID
    var provId= proCode;
    if(provId!=""){
    	document.getElementById("user.CITY_CODE").length=0;
        document.getElementById("user.BANK_CD").length=0;
        document.getElementById("user.CITY_CODE").options.add(new Option("--请选择--","0"));
        document.getElementById("user.BANK_CD").options.add(new Option("--请选择--","0"));
   	}
   	var count=0;
    $.ajax({
        type :"post",
        url :"<%=root%>/payfor/jsonPayfor_returnCityList.action",
        data :"provId=" + provId,
        dataType :"json",
        success :function (data){
        	var ds=document.getElementById("ds").value;
            count = data.length;
             document.getElementById("user.CITY_CODE").length = 0;
             document.getElementById("user.CITY_CODE").options.add(new Option("--请选择--","0"));
              for(var x=0;x<data.length;x++){
                  var option=new Option(data[x].CITY_NM,data[x].CITY_CD);
               	  document.getElementById("user.CITY_CODE").options.add(option);
               	  if(option.value==ds){
               		option.selected=true;
                  }
              }
              cityOnchange(ds);
            }
        });
     if(provId ==0 || count ==0){
          document.getElementById("user.CITY_CODE").length=0;
          document.getElementById("user.BANK_CD").length=0;
          document.getElementById("user.CITY_CODE").options.add(new Option("--请选择--","0"));
          document.getElementById("user.BANK_CD").options.add(new Option("--请选择--","0"));
          
      }
        
}
//市Onchange填充银行
function cityOnchange(cityCode) {
    var cityId = cityCode;
    if("" == cityId){
         document.getElementById("user.BANK_CD").length=0;
         document.getElementById("user.BANK_CD").options.add(new Option("--请选择--","0"));
         return ;
    }
    $.ajax({ 
        type : "post", 
        url : "<%=root%>/payfor/jsonPayfor_returnBankList.action",
        dataType : "json",
        success : function(data) {
        	 var kfh=document.getElementById("kfh").value;
             document.getElementById("user.BANK_CD").length = 0;
             document.getElementById("user.BANK_CD").options.add(new Option("--请选择--","0"));
            for ( var x = 0; x < data.length; x++) {
                var option=new Option(data[x].BANK_NM, data[x].BANK_CD);
                document.getElementById("user.BANK_CD").options.add(option);
                if(option.value==kfh){
               		option.selected=true;
                }
            }
        }
    });
}
//获取支行信息
function getPms(){
    var city =  document.getElementById("user.CITY_CODE").value;
    var bank = document.getElementById("user.BANK_CD").value;
    
    document.getElementById("bankNam_nm").value="";//清空开户行支行名称
    $("#bankNam_nm").unautocomplete();
    $.post("<%=root%>/payfor/jsonPayfor_returnSubBank.action",{'city':city,'bank':bank},function(data,textStatus){

       var tmp = data.split(',');
        $("#bankNam_nm").autocomplete(tmp, {
            matchContains: true,
            minChars: 0,
            max: 500,
            width: 430
            //scrollHeight:400
        });
    });
}
$(function(){
	$("#form1").validate({
		rules:{
			"user.USER_NAME":{
				required:true
			},
			userName:{
				required:true
			},
			"user.PROV_CODE":{
				required:true
			},
			"user.CITY_CODE":{
				required:true
			},
			"user.BANK_CD":{
				required:true
			},
			"user.PMS_BANK_NAME":{
				required:true
			},
			"user.ACCT_NO":{
				required:true,
				reg:"\\d+",
				minlength:10
			},
			custmrNoObj:{
				required:true
			},
			"user.MOBILE_NO":{
				required:false,
				reg:"([+]86){0,1}1[34589]\\d{9}"
			}
		},
		messages:{
			"user.USER_NAME":{
				required:"请输入户名"
			},
			userName:{
				required:"该联系人已存在"
			},
			"user.PROV_CODE":{
				required:"请选择省份"
			},
			"user.CITY_CODE":{
				required:"请选择市/县"
			},
			"user.BANK_CD":{
				required:"请选择开户行"
			},
			"user.PMS_BANK_NAME":{
				required:"请输入开户支行"
			},
			"user.ACCT_NO":{
				required:"请填写账号",
				reg:"账号只能为数字",
				minlength:"账号不能小于10为数字"
			},
			custmrNoObj:{
				required:""
			},
			"user.MOBILE_NO":{
				reg:"手机号码格式不正确"
			}
		}
	});
});
function setUserName(){
	document.getElementById("userNameError").innerHTML="";
	var rowid=document.getElementById("rowId").value;
	var userNameValue=document.getElementById("user.USER_NAME").value;
	var bankAcctNo=document.getElementById("user.ACCT_NO").value;
	if(userNameValue!=""&&bankAcctNo!=""){
		var strUrl="vilidateUser.action?userName="+encodeURIComponent(encodeURIComponent(userNameValue))+"&user.ROW_ID="+rowid+"&user.ACCT_NO="+bankAcctNo;
		$.ajax({
			type :"post",
	        url :"<%=root%>/"+strUrl,
	        dataType :"json",
	        success :function (data){
	        	if(data==false){
	        		document.getElementById("userNameError").innerHTML="该联系人已存在";
	        		document.getElementById("userName").value="";
	            }else if(data==true){
	            	document.getElementById("userNameError").innerHTML="";
	            	document.getElementById("userName").value="1";
	            }
	        }
		});
	}
}
function checkCradTp2(){
	var certificateTp=document.getElementById("user.CUSTMR_NO_TP");
	var certificateNo=document.getElementById("user.CUSTMR_NO");
	if(certificateTp.value==""){
		certificateNo.disabled=true;
		certificateNo.value="";
		document.getElementById('custmrNoObj').value="1";
	}else{
		certificateNo.removeAttribute("disabled");
		if(certificateNo.value==""){
			document.getElementById("custmrNoObj").value="";
		}
	}
}

function loadCustmrNoTp(){
	var custmrNoTp="${u.CUSTMR_NO_TP}";
	var custmrNoTpObj=document.getElementById("user.CUSTMR_NO_TP");
	for(var i=0;i<custmrNoTpObj.length;i++){
		if(custmrNoTp==custmrNoTpObj[i].value){
			custmrNoTpObj[i].selected=true;
			document.getElementById("user.CUSTMR_NO").removeAttribute("disabled");
			break;
		}
	}
}
function checkCustmrNo(objValue){
	var custmrNoObj=document.getElementById("custmrNoObj");
	var custmrNoError=document.getElementById("custmrNoError");
	custmrNoError.innerHTML="";
	custmrNoObj.value=objValue;
	if(custmrNoObj.value==""){
		custmrNoError.innerHTML="请填写证件号";
	}else{
		custmrNoError.innerHTML="";
	}
}
function checkCustmrNo2(){
	var custmrNoObj=document.getElementById("custmrNoObj");
	var custmrNoError=document.getElementById("custmrNoError");
	custmrNoError.innerHTML="";
	if(custmrNoObj.value==""){
		custmrNoError.innerHTML="请填写证件号";
	}else{
		custmrNoError.innerHTML="";
	}
}
</script>
</head>
<body onload="ajaxOnload();loadCustmrNoTp();"  style="background-color:#e8eef7; padding:0px; margin:0px;">
	<form id="form1" name="form1" method="post" action="userInfoMgr_confirmEdit.action">
	<s:token></s:token>
	<input type="hidden" value="${u.ROW_ID}" name="user.ROW_ID" id="rowId"/>
	<input type="hidden" value="${u.PROV_CODE}|${u.PROV_NAME}" id="sf">
	<input type="hidden" value="${u.CITY_CODE}|${u.CITY_NAME}" id="ds">
	<input type="hidden" value="${u.BANK_CD}|${u.BANK_NAME}" id="kfh">
  <table width="550">
   <tr>
   <td align="right">户名</td>
   <td><input type="text" value="${u.USER_NAME}" onkeyup="setUserName()" onblur="setUserName()" name="user.USER_NAME" id="user.USER_NAME" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" maxlength="60"/>
   <input type="hidden" name="userName" id="userName" value="1"/>
   <font color="red" id="userNameError"></font><font color="red">*</font>
   </td>
   </tr>
   <tr>
   <td align="right">省份</td>
   <td>
   <select name="user.PROV_CODE" id="user.PROV_CODE" style="width:205px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" onchange="provOnchange(this.value)">
   	<option value="">--请选择--</option>
   </select><font color="red">*</font>
   </td>
   </tr>
   <tr>
   <td align="right">市/县</td>
   <td>
   <select name="user.CITY_CODE" id="user.CITY_CODE" style="width:205px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" onchange="cityOnchange(this.value)">
   	<option value="">--请选择--</option>
   </select><font color="red">*</font>
   </td>
   </tr>
   <tr>
   <td align="right">开户行</td>
   <td>
   <select name="user.BANK_CD" value="${u.BANK_CD}" id="user.BANK_CD" onchange="getPms()" style="width:205px; height:19px; border:#7f9db9 1px solid; margin-right:5px;">
   	<option value="">--请选择--</option>
   </select><font color="red">*</font>
   </td>
   </tr>
   <tr>
   <td align="right">开户行支行名称</td>
   <td><input name="user.PMS_BANK_NAME" id="bankNam_nm" value="${u.PMS_BANK_NAME}" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" maxlength="160"/><font color="red">*</font></td>
   </tr>
   <tr>
   <tr>
   <td align="right">账号</td>
   <td><input type="text" value="${u.ACCT_NO}" onkeyup="setUserName()" onblur="setUserName()" name="user.ACCT_NO" id="user.ACCT_NO" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" maxlength="25"/><font color="red">*</font></td>
   </tr>
   <tr>
   <td align="right">证件类型</td>
   <td>
   <select name="user.CUSTMR_NO_TP" id="user.CUSTMR_NO_TP" style="width:205px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" onchange="checkCradTp2()">
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
   <td align="right">证件号</td>
   <td><input type="text" value="${u.CUSTMR_NO}" onkeyup="checkCustmrNo(this.value)" onchange="checkCustmrNo(this.value)" name="user.CUSTMR_NO" id="user.CUSTMR_NO" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" maxlength="20"/>
   <input type="hidden" name="custmrNoObj" id="custmrNoObj" value="1"/><font id="custmrNoError" style="color: red"></font>
   </td>
   </tr>
   <tr>
   <td align="right">手机号码</td>
   <td><input type="text" value="${u.MOBILE_NO}" name="user.MOBILE_NO" id="user.MOBILE_NO" style="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;"/></td>
   </tr>
   <tr>
   <td></td>
   <td>
   <input type="submit" value="提交" class="type_button" onclick="document.getElementById('userNameError').innerHTML='';checkCustmrNo2();"/>
   <input type="reset" value="重填" class="type_button">
   </td>
   </tr>
  </table>
</form>
</body>
</html>