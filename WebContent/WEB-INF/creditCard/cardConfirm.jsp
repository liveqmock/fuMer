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
	function loadCardTp(){
		var cardTpValue="${certificateTp}";
		var cardTp = document.getElementById("cardTp");
		if(cardTpValue=="0"){
			cardTp.value="身份证";
		}else if(cardTpValue=="1"){
			cardTp.value="护照";
		}else if(cardTpValue=="2"){
			cardTp.value="军官证";
		}else if(cardTpValue=="3"){
			cardTp.value="士兵证";
		}else if(cardTpValue=="4"){
			cardTp.value="回乡证";
		}else if(cardTpValue=="6"){
			cardTp.value="户口本";
		}else{
			cardTp.value="其他";
		}
	}
	function dsbBut(){
		document.getElementById("submit").disabled='true';
		document.getElementById("quxiao").disabled='true';
		return true;
	}
</script>
</head>
<body onload="resizeAll();loadCardTp();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition" style="display: block;">
<s:form name="daishou" method="post" id="daishou" action="credCard_confrimPost.action" target = "struFrame" theme="simple" onsubmit="return dsbBut();">
<s:token></s:token>
<table>
<tr>
		<td align="right">持卡人户名:</td>
		<td>
			<input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" readonly="readonly" maxlength="30" value="${userName}" id="userName" name="userName" />
		</td>
</tr>
<tr>
	<td align="right">
		持卡人账号:
	</td>
	<td>
		<input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" readonly="readonly"  maxlength="28" value="${dedNum}" name="dedNum" id="dedNum"/>
	
	</td>
</tr>
		<tr>
            <td align="right">
            	金额(单位:元):
            </td>
            <td>
                <input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" readonly="readonly" maxlength="10" value="${amount}" name="amount" id="amount"/>
            </td>
        </tr>
		<tr>
			<td align="right">
				证件类型:
			</td>
			<td>
                <input name="certificateTp" type="hidden" value="${certificateTp}"></input>
                <input class="type_text" id="cardTp" style="width:200px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" readonly="readonly" value=""/>
            </td>
		</tr>
		<tr>
			<td align="right">
				证件号:
			</td>
			<td>
                <input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="20" readonly="readonly" value="${certificateNo}" name="certificateNo" id="certificateNo"/>
            </td>
		</tr>
		<tr>
			<td align="right">
				手机号:
			</td>
			<td>
                <input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="11" readonly="readonly" value="${phoneNum}" id="phoneNum" name="phoneNum" />
            </td>
		</tr>
		<tr>
			<td align="right">
				有效期:
			</td>
			<td>
                <input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="4" readonly="readonly" value="${validityDt}" id="validityDt" name="validityDt" />
            </td>
		</tr>
		<tr>
			<td align="right">
				CVV2:
			</td>
			<td>
                <input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="3" readonly="readonly" value="${cvv2}" id="cvv2" name="cvv2" />
            
            </td>
		</tr>
		<tr>
			<td align="right">
				备注1:
			</td>
			<td>
                <input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" readonly="readonly" value="${remark1}" name="remark1" id="remark1"/>
            </td>
		</tr>
		<tr>
			<td align="right">
				备注2:
			</td>
			<td>
                <input class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" readonly="readonly" value="${remark2}" name="remark2" id="remark2"/>
            </td>
		</tr>
		<tr>
		<td></td>
  		<td align="left">
  			<s:submit cssClass="type_button" cssStyle="width: 100px;" name="submit" id="submit" value="确 认发送"/>
  			<input class="type_button" id="quxiao" name='quxiao'  type="button" value="取消" onclick="history.back();"/>
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