<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>

<%@page import="com.fuiou.mer.util.TDataDictConst"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>批量代收|查询上传文件</title>
<sx:head/>

<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<script language="javascript">
function valiFileName() {
    for( var i=0 ; i<document.getElementById("fileName").value.length ; i ++ ){
		var c = document.getElementById("fileName").value.charAt(i) ;
		if(c == '%'){
			document.getElementById("fileName_er").innerHTML="文件名中不能包含百分号%!";//innerHTML
			document.getElementById("fileName").focus(); 
			document.getElementById("fileName").select();
			return false;
		}else{
			document.getElementById("fileName_er").innerHTML="";//innerHTML
		}
	}
    document.forms[0].submit();
}
</script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
	String fileBusiTp = com.fuiou.mer.util.TDataDictConst.BUSI_CD_INCOMEFOR;
	request.setAttribute("fileBusiTp",fileBusiTp);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
</head>
<body onload="resizeAll();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<div id="condition">
<s:form name="queryForm" method="post" id="statDay" action="VerifyFileQuery.action" target="data_eara_frame_down" theme="simple">
	<table>
		<s:fielderror></s:fielderror>
		<tr>
			<td align="right">文件上传开始时间：</td>
			<td><input type="text" class="Wdate" name="startDate" id="startDate" value="<%=dateTimeStr %>" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})"/></td>
			<td align="right">文件上传结束时间：</td>
			<td><input type="text" class="Wdate" name="endDate" id="endDate" value="<%=dateTimeStr %>" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})"/> </td>
			<td align="right">文件名：</td>
			<td><input type="text" name="fileName" id="fileName" /></td>
			<td align="right"><input type="button" name="查询" value="查询" class="type_button" onclick="valiFileName();"/>&nbsp;&nbsp;<font id="fileName_er"></font></td>
		</tr>
<!-- 		<tr> -->
<!-- 			<td align="right">状态：</td> -->
<%-- 			<td><select name="stateSel" id="stateSel"> --%>
<!-- 				<option value="">全部</option> -->
<!-- 				<option value="0">等待处理</option> -->
<!-- 				<option value="X">正在处理</option> -->
<!-- 				<option value="RFST9">处理完成</option> -->
<%-- 			</select></td> --%>
<!-- 			<td align="right">文件名：</td> -->
<!-- 			<td><input type="text" name="fileName" id="fileName" /></td> -->
<!-- 			<td align="right"><input type="button" name="查询" value="查询" class="type_button" onclick="valiFileName();"/>&nbsp;&nbsp;<font id="fileName_er"></font></td> -->
<!-- 		</tr> -->
	</table>
	<s:hidden name="fileBusiTp" id="fileBusiTp" value="%{#request.fileBusiTp}"></s:hidden>
</s:form>
</div>
</body>
</html>