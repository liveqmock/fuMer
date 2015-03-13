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
function subFrom(){
	location.href='credCard_toCard.action?issId='+$("input[name='bank']:checked").val();
}
</script>
</head>
<body  onload="resizeAll();" style="padding:0px; margin:0px;">
<div id="condition" style="display: block;">
<table align="center">
<tr>
<td>&nbsp;</td>
</tr>
<tr >
	<td colspan="8" align="center"><h2>请选择以下富友支持的银行(信用卡)</h2></td>
</tr>
<tr height="50">
	<td style="vertical-align:middle;" align="right"><input value="0301" type="radio" checked="checked" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803010000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0302" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803020000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0303" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803030000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0304" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803040000.gif">&nbsp;&nbsp;</td>
</tr>
<tr height="50">
	<td style="vertical-align:middle;" align="right"><input value="0305" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803050000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0306" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803060000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0310" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803100000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0100" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0801000000.gif">&nbsp;&nbsp;</td>
</tr>
<tr height="50">
	<td style="vertical-align:middle;" align="right"><input value="0104" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0801040000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0105" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0801050000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0401" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0804012902.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0410" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0804105840.gif">&nbsp;&nbsp;</td>
</tr>
<tr height="50">
	<td style="vertical-align:middle;" align="right"><input value="0308" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803080000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0307" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0803070000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"><input value="0102" type="radio" name="bank"/></td>
	<td><img src="<%=root %>/images/bank/0801020000.gif">&nbsp;&nbsp;</td>
	<td style="vertical-align:middle;" align="right"></td>
	<td></td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<td colspan="8" align="center"><input type="button" class="type_button" onclick="subFrom()" style="width: 100px;" value="确定"/></td>
</tr>
</table>
</div>
</body>
</html>