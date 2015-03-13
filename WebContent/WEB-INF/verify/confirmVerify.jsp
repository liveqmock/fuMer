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
<script type="text/javascript">
            	function card(){
					var cardTp=document.getElementById("cardTp");
					var certtp=document.getElementById("certtp");
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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>账户验证|确定账户验证交易</title>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.validate.js" type="text/javascript"></script>
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
</head>
<body onload="resizeAll(),card();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
<form name="verifyreq" id="verifyreq" method="post" id="verifyreq" action="verify_simpleCommit.action" target = "struFrame">
<input type="hidden" value="confirm" name="confirm"/>
<table>
<tr>
	<td align="right">
		开户行:
    </td>
	<td>
		<input type="text" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  maxlength="28" readonly="readonly" value="<s:property value="#bankName"/>" disabled="disabled"/>
		<input type="hidden" name="bank" value="<s:property value="#accessBean.bankno"/>">
   </td>
</tr>
<tr>
	<td align="right">
		银行账号:
	</td>
	<td>
		<input type="text" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  maxlength="28" readonly="readonly" value="<s:property value="#accessBean.accntno"/>" disabled="disabled"/>
		<input name="dedNum" type="hidden" value="<s:property value="#accessBean.accntno"/>"/>
	</td>
</tr>
<tr>
			<td align="right">账户名称:</td>
			<td>
				<input type="text" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" value="<s:property value="#accessBean.accntnm"/>" readonly="readonly" disabled="disabled"/>
				<input type="hidden" value="<s:property value="#accessBean.accntnm"/>" name="userName"/>
			</td>
		</tr>
		<tr>
            <td align="right">
            	证件类型:
            </td>
            <td>
            <input type="text" id="cardTp" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  readonly="readonly" disabled="disabled"/>
            <input type="hidden" value="<s:property value="#accessBean.certtp"/>" name="certtp" id="certtp"/>
            </td>
        </tr>
		<tr>
			<td align="right">
				证件号:
			</td>
			<td>
                <input type="text" class="type_text" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" name="certno" value="<s:property value="#accessBean.certno"/>" disabled="disabled"/>
           		<input type="hidden" name="certno" value="<s:property value="#accessBean.certno"/>" >
            </td>
		</tr>
		<tr>
		<td>&nbsp;</td>
  			<td height="30"  align="left" >
  				<input type="submit" class="type_button" name="submit" value="确认发起交易" />
  				<input type="reset" class="type_button" name="reset" value="取消" onclick="location.href='verify_init.action'"/>
  			</td>
		</tr>
</table>
</form>
</div>
</body>
</html>