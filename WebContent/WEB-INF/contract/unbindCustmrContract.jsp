<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>客户协议管理</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />

<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
%>
<script language="javascript" src="<%=root %>/styles/iframe.js" type="text/javascript"></script>
</head>
<body  onload="resizeAll();"  style="background-color:#e8eef7; padding:0px; margin:0px;">
<s:form name="form1" action="custmrBusi_selectCustmrBusis.action" method="post" target="data_eara_frame_down"  theme="simple">
<div id="condition">
	<table>
		  <tr>
          	<td><s:text name="开始时间：" /></td>
            <td><input type="text" class="Wdate" name="custmrBusi.CONTRACT_SIGN_DT" id="fromDate"  onfocus="WdatePicker({dateFmt:'yyyyMMdd',minDate:'#F{$dp.$D(\'toDate\',{M:-120});}',maxDate:'#F{$dp.$D(\'toDate\',{d:0});}'})"/> </td>
         	<td>&nbsp;<s:text name="结束时间：" /></td>
         	<td><input type="text" class="Wdate" name="custmrBusi.CONTRACT_EXPIRE_DT" id="toDate"  onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'#F{$dp.$D(\'fromDate\',{M:120});}',minDate:'#F{$dp.$D(\'fromDate\',{d:0});}'})"/> </td>
         	<td align="right" width="100"><s:text name="手机号码：" /></td>
            <td><input type="text" name="custmrBusi.MOBILE_NO" maxlength="11" id="MOBILE_NO" /></td>
        </tr>
        <tr>
        	<td align="right"><s:text name="户名：" /></td>
            <td><input type="text" name="custmrBusi.USER_NM"  id="USER_NM" /></td>
            <td align="right"><s:text name="账号：" /></td>
            <td><input type="text" name="custmrBusi.ACNT_NO" id="ACNT_NO" /></td>
            <td align="right"><s:text name="协议状态：" /></td>
            <td>
            	<select  id="CONTRACT_ST" name="custmrBusi.CONTRACT_ST" >
            		<option value="">--请选择--</option>
            		<option value="0">未生效</option>
            		<option value="1">已生效</option>
            		<option value="2">待验证</option>	
            	</select>
            </td>
            <td> <input type="submit" name="查询" value="查询" class="type_button" />&nbsp;&nbsp;<font id="fileName_er"></font></td>
        </tr>
	</table>
</div>
</s:form>
</body>
</html>