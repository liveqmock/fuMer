<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件查看</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="table" style="width:300px">

<s:property value="#request.message"/>
<table  class="tableBorder" border="0" cellspacing="0" cellpadding="0" width="300px">
	<tr class="tr1">
		<td align="right">文件名：</td>
		<td><s:property value="#request.fileName"/></td>
	</tr>
	<tr class="tr1">
		<td align="right">商户号：</td>
		<td><s:property value="#request.fileMerNo"/></td>
	</tr>
	<tr class="tr1">
		<td align="right">文件日期：</td>
		<td><s:property value="#request.fileDate"/></td>
	</tr>
	<tr class="tr1">
		<td align="right">文件序号：</td>
		<td><s:property value="#request.fileSeq"/></td>
	</tr>
	<tr class="tr1">
		<td align="right">文件大小：</td>
		<td><s:property value="#request.fileSize"/>&nbsp(Byte)</td>
	</tr>
	<tr class="tr1">
		<td align="right">文件总行数：</td>
		<td><s:property value="#request.rowCount"/></td>
	</tr>
	<tr class="tr1">
		<td align="right">文件总金额</td>
		<td><s:property value="#request.fileAmt"/></td>
	</tr>
	<tr class="tr1">
		<td align="right">文件状态：</td>
		<td><s:property value="#request.fileState"/></td>
	</tr>
	<tr class="tr1">
		<td align="right">结果文件生成状态：</td>
		<td><s:property value="#request.resultFileState"/></td>
	</tr>
	<s:if test="#request.resultFileName != ''">
		<tr class="tr1">
			<td align="right">结果文件名：</td>
			<td><s:property value="#request.resultFileName"/></td>
		</tr>
	</s:if>
	<tr class="tr1">
		<td align="right">操作员：</td>
		<td><s:property value="#request.fileOperId"/></td>
	</tr>	
</table>

</div>
</div>
</body>
</html>