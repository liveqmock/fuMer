<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改密码</title>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String dateTimeStr = sdf.format(dt);
%>
<STYLE type="text/css">
.select {
    width:360px; 
    height:19px; 
    border:#7f9db9 1px solid;
    margin-right:5px;
}
</STYLE>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript">
function getResult(s){
	  if(s.length < 8){
	   return 0;
	  }
	  var ls = 0;
	  if (s.match(/[a-z]/ig)){
	   ls++;
	  }
	  if (s.match(/[0-9]/ig)){
	   ls++;
	  }
	   if (s.match(/(.[^a-z0-9])/ig)){
	   ls++;
	  }
	  return ls
}

function check(){
	var os = document.getElementById("originPassword").value;
	if(os==''){
		alert("请输入原密码");
		return false;
	}
	var ns = document.getElementById("newPassword").value;
	var rns = document.getElementById("renewPassword").value;
	var r = getResult(ns);
	if(r<2){
		alert("密码必须由字母与数字组合不少于8位组成");
		return false;
	}
	if(ns!=rns){
		alert("新密码输入不一致");
		return false;
	}
	return true;
}
</script>
</head>
<body onload="resizeAll();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
<s:form  action="newPasswordReq.action" method="post" theme="simple" onsubmit="return check()" target = "struFrame" >
	<table>
		<tr>
			<td align="right">原密码：</td>
			<td>
				<s:password maxlength="20" name="originPassword" id="originPassword"/>
				<s:property value="fieldErrors.originPassword" /><font color="red">*</font>
			</td>
		</tr>
		<tr>
            <td align="right">新密码：</td>
            <td>
                <s:password maxlength="20" name="newPassword" id="newPassword"/>
                <s:property value="fieldErrors.newPassword" /><font color="red">*</font>
            </td>
        </tr>
        <tr>
            <td align="right">再次输入新密码：</td>
            <td>
                <s:password maxlength="20" name="renewPassword" id="renewPassword"/>
                <s:property value="fieldErrors.renewPassword" /><font color="red">*</font>
            </td>
        </tr>
		<tr>
		  <td></td>
			<td align="left"><s:submit cssClass="type_button" name="确认" value="确认" /></td>
		</tr>
	</table>
</s:form>
</div>
</body>
</html>