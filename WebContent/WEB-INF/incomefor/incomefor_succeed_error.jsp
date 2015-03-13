<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ include file="/common.jsp"%>
<%@ page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统|批量上传代收文件</title>
<sx:head />
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript">
window.parent.document.frames["data_eara_frame_up"].document.getElementById("submit").disabled=false;
</script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
	String isDisplayFeeMsg = (String)request.getAttribute("isDisplayFeeMsg");
%>
<script language="javascript"
	src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>"
	type="text/javascript"></script>
<script type="text/javascript">
	function ver_del(it) {
		if (confirm("确认删除该记录?删除后将不可恢复！")) {
			self.location.href = it.attributes['url'].value;
		}
	}
	function ver_sub(it) {
		if (confirm("确认提交吗！")) {
			self.location.href = it.attributes['url'].value;
		}
	}
</script>
</head>
<body>
<div id="condition">
<s:if test="#fileRows ==0 ">
	提示：存在不正确记录!
</s:if><s:else>
${message }<br>
</s:else>
<br>
	<table class="tableBorder" border="0">
	  <tr>
	    <td colspan="6" align="center">汇总信息</td>
	  </tr>
	  <tr>
	    <td width="60">文件名</td>
	    <td width="100">${fileNam }</td>
	    <td width="160">&nbsp;</td>
	  </tr>
	  <tr>
	    <td>业务代码</td>
	    <td>明细</td>
	    <td>金额(单位：元)</td>
	    <%
	    	if("1".equals(isDisplayFeeMsg)){
	    %>
	    <td>手续费(单位：元)</td>
	    <%
	    	}
	    %>
	    <td>应收(单位：元)</td>
	    <td>实际到账(单位：元)</td>
	  </tr>
	  <tr>
	    <td>${fileBusiTp }</td>
	    <td>${fileRows }</td>
	    <td>${fileAmt }</td>
	    <%
	    	if("1".equals(isDisplayFeeMsg)){
	    %>
	    <td>${feeAmt }</td>
	    <%
	    	}
	    %>
	    <td>${srcAmt }</td>
	    <td>${destAmt }</td>
	  </tr>
	</table>

<s:if test="#sameFileMap.size() > 0 ">
<br>
提示：近期有相同笔数和金额文件上传，请确认没有上传重复文件<br/>
文件名：${sameFileMap.fileNam }<br/>
上传时间：${sameFileMap.filaDate }<br/>
</s:if>
<s:if test="#errListMap.size() > 0 ">
	<table  class="tableBorder" width="1098" border="0">
		<s:fielderror></s:fielderror>
		<tr>
			<!--<td width="44">序号</td>
			<td width="114">扣款人开户行代码</td>
			<td width="114">收款人银行帐号</td>
			<td width="67">户名</td>
			<td width="60">金额</td>
			<td width="84">企业流水号</td>
			<td width="55">备注</td>
			<td width="98">手机号</td>
			<td width="127">错误描述</td>
		-->
		<td width="40">行数</td><td width="460">原值</td><td width="598">错误描述</td>
		</tr>
		<s:iterator value="errListMap" var="tFeleErrorInf">
			<tr>
				<td><s:property value="ERR_ROW_SEQ " /></td>
				<td><s:property value="ERR_ROW_CONTENT " /></td>
				<td><s:property value="ERR_ROW_DESC " /></td>

				<!--
				<td><s:property value="FILE_MCHNT_CD " /></td>
				<td><s:property value="ERR_ROW_CONTENT " /></td>
				<td><s:property value="ERR_ROW_DESC " /></td>
				<td><s:property value="ROW_CRT_TS " /></td>

				<td><s:property value=" " /></td>
				<td><s:property value=" " /></td>
				<td><s:property value=" " /></td>
				<td><s:property value=" " /></td>-->
			</tr>
		</s:iterator>
	</table>
</s:if> 
<s:if test="#fileRows >0 ">
<input type="button" value="确认" onClick="ver_sub(this)" Class="type_button"
	url="${pageContext.request.contextPath}/incomefor/incomefor_executeBtnType.action?btnType=OK&uploadFileName=${fileNam}"" />
&nbsp;&nbsp;<input type="button" value="取消" onClick="ver_del(this)" Class="type_button"
	url="${pageContext.request.contextPath}/incomefor/incomefor_executeBtnType.action?btnType=NO&uploadFileName=${fileNam}" />
</s:if>
</div>
</body>
</html>