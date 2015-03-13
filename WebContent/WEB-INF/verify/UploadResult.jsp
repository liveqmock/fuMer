<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="com.fuiou.mgr.util.page.PagerUtil"%>
<%@ page import="java.util.*"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>账户验证|上传结果</title>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<script language="javascript">
window.parent.document.frames["data_eara_frame_up"].document.getElementById("VerifyFileUpload_submit").disabled=false;
</script>
</head>
<body>
<s:form name="form1" action="" method="post" theme="simple">
	<div class="table">
     <table border="0" cellpadding="0" cellspacing="0">
     	<tr>
			<TD><s:property value="#request.message"/></TD>
		</tr>
		<s:if test="#request.fileName != ''">
			<tr>
				<TD>文件名：<s:property value="#request.fileName"/></TD>
			</tr>
		</s:if>
		<tr>
			<TD><s:property value="#request.errMsg"/></TD>
		</tr>
		<s:if test="#request.rowCount != null">
			<tr>
				<TD align="center" nowrap="nowrap" class='title'>文件总行数：</TD>
				<td align="center" nowrap="nowrap" class='title'><s:property value="#request.rowCount"/></td>
				<TD align="center" nowrap="nowrap" class='title'>验证正确行数：</TD>
				<td align="center" nowrap="nowrap" class='title'><s:property value="#request.corCount"/></td>
				<TD align="center" nowrap="nowrap" class='title'>验证失败行数：</TD>
				<td align="center" nowrap="nowrap" class='title'><s:property value="#request.errCount"/></td>
			</tr>
		</s:if>
	</table>
	</div>
	<div class="table">
		<s:if test="#request.errList != null">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center" nowrap="nowrap" class='title'>错误行号</td>
					<td align="center" nowrap="nowrap" class='title'>错误行</td>
					<td align="center" nowrap="nowrap" class='title'>错误原因</td>
				</tr>
				<s:iterator value="#request.errList" id="errList" status="trade">
					<s:if test="#trade.odd == true">
				    	<tr class="tr1">
				    </s:if>
				    <s:else>
				    	<tr class="tr2">
				    </s:else>
						<td align="center" nowrap="nowrap" ><s:property value="rowNo" /></td>
						<td align="center" nowrap="nowrap" ><s:property value="line" /></td>
						<td align="center" nowrap="nowrap" ><s:property value="errMsg" /></td>
					</tr>
				</s:iterator>
			</table>	
		</s:if>
	</div>
</s:form>
</body>
</html>