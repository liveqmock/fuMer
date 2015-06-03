<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>单笔客户协议录入</title>
<%
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.validate.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/js/idValidate.js"></script>
<style type="text/css">
	em {
		color: red;
	}
	input{
		margin: 1px 1px 1px 1px;
	}
	.item{
		text-align: right;
	}
	.input_off{
		padding:2px 8px 0pt 3px;
		height:18px;
		border:1px solid #CCC;
		background-color:#FFF;
	}
	.input_on{
		padding:2px 8px 0pt 3px;
		height:18px;
		border:1px solid #999;
		background-color:#FFFFCC;
	}
	.input_move{
	padding:2px 8px 0pt 3px;
	height:18px;
	border:1px solid #999;
	background-color:#FFFFCC;
}
	.input_out{
	/*height:16px;默认高度*/
	padding:2px 8px 0pt 3px;
	height:18px;
	border:1px solid #CCC;
	background-color:#FFF;
}
select {
	width: 140px;
	font-size: 12px;
}
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
body, html {
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
$(function(){
	$("#form1").validate({
		rules:{
			"custmrBusi.BUSI_CD":{
				required:true
			},
			"custmrBusi.USER_NM":{
				required:true
			},
			"custmrBusi.MOBILE_NO":{
				required:true,
				reg:"1\\d{10}"
			},
			"custmrBusi.CREDT_TP":{
				required:true
			},
			"custmrBusi.CREDT_NO":{
				required:true,
				reg:"[a-zA-z0-9]+"
			},
			"custmrBusi.ACNT_NO":{
				required:true,
				reg:"\\d+",
				minlength:10,
				maxlength:30
			},
			"custmrBusi.ACNT_TP":{
				required:true
			}
		},
		messages:{
			"custmrBusi.BUSI_CD":{
				required:"请选择业务代码"
			},
			"custmrBusi.USER_NM":{
				required:"请填写姓名"
			},
			"custmrBusi.MOBILE_NO":{
				required:"请填写正确的手机号码",
				reg:"请填写正确的手机号码"
			},
			"custmrBusi.CREDT_TP":{
				required:"请选择证件类型"
			},
			"custmrBusi.CREDT_NO":{
				required:"请填写证件号",
				reg:"格式不正确"
			},
			"custmrBusi.ACNT_NO":{
				required:"请填写账号",
				reg:"格式不正确",
				minlength:"账号不能小于10位且不能大于30位"
			},
			"custmrBusi.ACNT_TP":{
				required:"请选择账户类型"
			}
		}
	});
	
	$("#ivrCall").click(function(){
		if($(this).attr("checked")){
			var mobile = $("#MOBILE_NO").val();
			var p = /^1\d{10}$/;
			if(!p.test(mobile)){
				alert("手机号码格式不正确");
				$("#button").attr("disabled",true);
			}else{
				$("#button").attr("disabled",false);
			}
		}else{
			$("#button").attr("disabled",false);
		}
	})
	
	$("#MOBILE_NO").blur(function(){
		var mobile = $(this).val();
		var p = /^1\d{10}$/;
		if(!p.test(mobile)){
			alert("手机号码格式不正确");
			$("#button").attr("disabled",true);
		}else{
			$("#button").attr("disabled",false);
		}
	})
	
	$("#CREDT_NO").blur(function(){
		var idTp = $("#CREDT_TP").val();
		if(idTp=='0'){
			var idNo = $(this).val();
			var flag = IdCardValidate(idNo);
			if(!flag){
				alert("身份证号码格式不正确");
				$("#button").attr("disabled",true);
			}else{
				$("#button").attr("disabled",false);
			}
		} 
	})
	
		$("#CREDT_TP").change(function(){
		var idTp = $(this).val();
		if(idTp=='0'){
			var idNo = $("#CREDT_NO").val();
			var flag = IdCardValidate(idNo);
			if(idNo!='' && !flag){
				alert("身份证号码格式不正确");
				$("#button").attr("disabled",true);
			}else{
				$("#button").attr("disabled",false);
			}
		} 
	})
	
});


</script>
</head>
<body  onload="resizeAll();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<form method="post" action="addCustmrBusiContract_addCustmrBusi.action" target="struFrame"  id="form1" name="form1">
  <s:token></s:token>
  <table width="88%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 0px;margin-left: 10px;">
    <tr>
      <td width="20%" class="item">业务类型：</td>
      <td width="30%">
        <select name="custmrBusi.BUSI_CD" id="BUSI_CD" >
          <option value="AC01">代收</option>
          <option value="AP01">付款</option>
   	  </select><em>*</em></td>
   	  <td class="item">账号行别：</td>
      <td><select name="custmrBusi.BANK_CD" id="BANK_CD">
        <s:iterator value="bankMap">
        	<option value='<s:property value="value.BANK_CD"/>'><s:property value="value.BANK_NM"/></option>
        </s:iterator>
      </select><em>*</em></td>
    </tr>
    <tr>
      <td class="item">户名：</td>
      <td><input style="width: 125px;" type="text" name="custmrBusi.USER_NM" id="USER_NM" maxlength="30" class="input_out" onfocus="this.className='input_on';this.onmouseout=''" onblur="this.className='input_off';this.onmouseout=function(){this.className='input_out'};" onmousemove="this.className='input_move'" onmouseout="this.className='input_out'"/><em>*</em></td>
      <td class="item">账户属性：</td>
      <td><select name="custmrBusi.ACNT_TP" id="ACNT_TP">
      	  <option value="">--请选择--</option>
          <option value="*">无需验证</option>
      	  <option value="01">借记卡</option>
          <option value="02">贷记卡</option>
          <option value="03">准贷记卡</option>
      </select><em>*</em></td>
    </tr>
    <tr>
    <td class="item">证件类型：</td>
      <td>
      <select name="custmrBusi.CREDT_TP" id="CREDT_TP">
      	<option value="">--请选择--</option>
        <option VALUE="0">身份证</option>
		<option VALUE="1">护照</option>
		<option VALUE="2">军官证</option>
		<option VALUE="3">士兵证</option>
		<option VALUE="4">回乡证</option>
		<option VALUE="5">户口本</option>
		<option VALUE="6">外国护照</option>
		<option VALUE="7">其他</option>
      </select><em>*</em></td>
      <td class="item">账号：</td>
      <td><input style="width: 125px;" type="text"  maxlength="30" name="custmrBusi.ACNT_NO" id="ACNT_NO" class="input_out" onfocus="this.className='input_on';this.onmouseout=''" onblur="this.className='input_off';this.onmouseout=function(){this.className='input_out'};" onmousemove="this.className='input_move'" onmouseout="this.className='input_out'"/>
      <em>*</em></td>
    </tr>
    <tr>
      <td class="item">证件号码：</td>
      <td><input style="width: 125px;" type="text" maxlength="30" name="custmrBusi.CREDT_NO" id="CREDT_NO" class="input_out" onfocus="this.className='input_on';this.onmouseout=''" onblur="this.className='input_off';this.onmouseout=function(){this.className='input_out'};" onmousemove="this.className='input_move'" onmouseout="this.className='input_out'"/><em>*</em></td>
      <td class="item">手机号码：</td>
      <td><input style="width: 125px;" type="text" name="custmrBusi.MOBILE_NO" id="MOBILE_NO" class="input_out" onfocus="this.className='input_on';this.onmouseout=''" onblur="this.className='input_off';this.onmouseout=function(){this.className='input_out'};" onmousemove="this.className='input_move'" onmouseout="this.className='input_out'"/><em>*</em></td>
    </tr>
    <tr>
      <td class="item">是否需要语音回拨签约：</td>
      <td><input  type="radio" name="custmrBusi.IS_CALLBACK" value="1" checked="checked"/>不需要<input type="radio" value="0" name="custmrBusi.IS_CALLBACK" id="ivrCall"/>需要 </td>
      <td class="item">备注：</td>
      <td colspan="3"><input style="width: 125px;" type="text" maxlength="60" name="custmrBusi.RESERVED1" id="RESERVED1" class="input_out" onfocus="this.className='input_on';this.onmouseout=''" onblur="this.className='input_off';this.onmouseout=function(){this.className='input_out'};" onmousemove="this.className='input_move'" onmouseout="this.className='input_out'"/></td>
    </tr>
    <tr>
      <td colspan="4" class="item" style="padding-right:370px; "><input type="submit" name="button" id="button" value="提交" class="type_button"/>
      <input type="reset" name="button2" id="button2" value="重置" class="type_button"/></td>
    </tr>
  </table>
</form>
</body>
</html>
