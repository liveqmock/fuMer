<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>付款文件复核</title>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js" type="text/javascript"></script>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
</head>

<body style="padding: 0px; margin: 0px;">
<%
	int totalRecords = (Integer)request.getAttribute("fileCount");
	String message = (String)request.getAttribute("message");
	//out.println("totalRecords=" + totalRecords);
	if(0 == totalRecords) {
		out.println("<center><div style=\"fond-size:12px;\"><b>" + message + "</b></div></center>");
		return ;
	}else {
%>
<s:form name="form1" action="proclamAction_getAps.action" method="post" theme="simple">
	<div class="table">
     <table  class="tableBorder" border="0" cellpadding="0" cellspacing="0" align="center">
    	 <tr>
			<td align="center" nowrap="nowrap" class='title'>上传日期</td>
			<td align="center" nowrap="nowrap" class='title'>文件名</td>
			<td align="center" nowrap="nowrap" class='title'>上传操作员</td>
			<td align="center" nowrap="nowrap" class='title'>金额</td>
			<td align="center" nowrap="nowrap" class='title'>笔数</td>
			<td align="center" nowrap="nowrap" class='title'>操作</td>
		</tr>
		<page:pager total="<%=totalRecords %>">
			<s:iterator value="#request.fileList" id="trade" status="trade">
			<tr>
				<s:if test="#trade.odd == true">
			    	<tr class="tr1">
			    </s:if>
			    <s:else>
			    	<tr class="tr2">
			    </s:else>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="FILE_DT" /></td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="FILE_NM" /></td> 
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="OPR_USR_ID" /></td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="FILE_RIGHT_AMT" /></td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="FILE_RIGHT_ROWS" /></td>
					<td align="center" nowrap="nowrap" class='tr1'>
						<a href='VerifyFileView.action?fileName=<s:property value="FILE_NM"/>&fileBusiTp=<s:property value="FILE_BUSI_TP"/>' target="blank">查看&nbsp;|&nbsp;</a>
						
					    <a href='${pageContext.request.contextPath}/payfor/PayforReviewAction_fileReview.action?uploadFileName=<s:property value="FILE_NM"/>&fileBusiTp=<s:property value="FILE_BUSI_TP"/>'>复核&nbsp;|&nbsp;</a>
					    
					    <a href='${pageContext.request.contextPath}/payfor/payFor_executePayForBtnType.action?btnType=NO&uploadFileName=<s:property value="FILE_NM"/>&busiCd=<s:property value="FILE_BUSI_TP"/>'>复核不通过&nbsp;|&nbsp;</a>
					
						<a href='VerifyFileDownload.action?fileName=<s:property value="FILE_NM"/>&savePath=<s:property value="FILE_PATH"/>&fileSffx=<s:property value="FILE_NM_SFFX"/>'>下载原始文件&nbsp;|&nbsp;</a>
						
					</td>
				</tr>
			</s:iterator>
			<tr class="bg_column" height='20'>
				<td height="30" colspan="6" align="left">
				<div>
				<div style="float: left;">
				<page:navigator type='text' />
				</div>
				<div style="float: left;">
				&nbsp;
				<input type="button" class="type_button" value="返回" onclick="location.href='proclamAction_proclamActions.action'"/>
				</div>
				</div>
				</td>
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