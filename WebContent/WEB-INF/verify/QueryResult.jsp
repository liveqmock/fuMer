<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>账户验证文件查询结果</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<%
	int totalRecords = (Integer)request.getAttribute("fileCount");
	//out.println("totalRecords=" + totalRecords);
	String meg = (String)request.getAttribute("message");
	if (0 == totalRecords) {
		out.println("<center><div style=\"fond-size:12px;\"><b>"+meg+"</b></div></center>");
		return;
	} else {
%>
<s:form name="form1" action="VerifyFileQuery.action" method="post" theme="simple">
	<div class="table">
     <table  class="tableBorder" border="0" cellpadding="0" cellspacing="0" align="center">
    	 <tr>
			<td align="center" nowrap="nowrap" class='title'>上传日期</td>
			<td align="center" nowrap="nowrap" class='title'>文件名</td>
			<td align="center" nowrap="nowrap" class='title'>上传操作员</td>
			<td align="center" nowrap="nowrap" class='title'>处理状态</td>
			<td align="center" nowrap="nowrap" class='title'>操作</td>
		</tr>
		<page:pager total="<%=totalRecords %>">
			<s:iterator value="#request.fileList" id="trade" status="trade">
				<s:if test="#trade.odd == true">
			    	<tr class="tr1">
			    </s:if>
			    <s:else>
			    	<tr class="tr2">
			    </s:else>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="FILE_DT" /></td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="FILE_NM" /></td> 
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="OPR_USR_ID" /></td>
					<td align="center" nowrap="nowrap" class='tr2'>
						<s:if test="FILE_ST == 0">商户未确认</s:if>
						<s:if test="FILE_ST == 1">商户已确认</s:if>
						<s:if test="FILE_ST == 2">运营人员已确认</s:if>
						<s:if test="FILE_ST == 3">运营人员退回</s:if>
						<s:if test="FILE_ST == 4">超时退回</s:if>
						<s:if test="FILE_ST == 5">正在处理</s:if>
						<s:if test="FILE_ST == 6">银行文件生成失败</s:if>
						<s:if test="FILE_ST == 7">银行文件生成成功</s:if>
						<s:if test="FILE_ST == 8">处理失败</s:if>
						<s:if test="FILE_ST == 9">处理成功</s:if>
					</td>
					<td align="center" nowrap="nowrap" class='tr1'>
						<a href='VerifyFileView.action?fileName=<s:property value="FILE_NM"/>&fileBusiTp=<s:property value="#request.fileBusiTp"/>' target="blank">查看&nbsp;|&nbsp;</a>
						<s:if test="FILE_ST == 0">
							<a href='incomefor_executeBtnType.action?btnType=OK&uploadFileName=<s:property value="FILE_NM"/>&fileBusiTp=<s:property value="#request.fileBusiTp"/>' target="blank">确认&nbsp;|&nbsp;</a>
						</s:if>
						<s:else>
							确认&nbsp;|&nbsp;
						</s:else>
						<s:if test="FILE_ST == 0 || FILE_ST == 1">
							<a href='incomefor_executeBtnType.action?btnType=NO&uploadFileName=<s:property value="FILE_NM"/>&fileBusiTp=<s:property value="#request.fileBusiTp"/>' target="blank">删除&nbsp;|&nbsp;</a>
						</s:if>
						<s:else>
							删除&nbsp;|&nbsp;
						</s:else>
						<a href='VerifyFileDownload.action?fileName=<s:property value="FILE_NM"/>&savePath=<s:property value="FILE_PATH"/>&fileSffx=<s:property value="FILE_NM_SFFX"/>'>下载原始文件&nbsp;|&nbsp;</a>
						<s:if test="FILE_RSP_ST == 1 ">
							<a href='VerifyFileDownload.action?fileName=<s:property value="FILE_RSP_NM"/>&savePath=<s:property value="FILE_PATH"/>&fileSffx=.txt'>下载响应文件</a>
						</s:if>
						<s:else>
							下载响应文件
						</s:else>
					</td>
				</tr>
			</s:iterator>
			<tr class="bg_column" height='20'>
				<td width="960" height="30" colspan="20" align="left"><page:navigator
					type='text' /></td>
			</tr>
		</page:pager>
	</table>
	</div>
</s:form>
<%
	}
%>
</body>
</html>