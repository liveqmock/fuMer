<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/all.css" rel="stylesheet" type="text/css" />

<script language="javascript" src="/fuMer/js/jquery.js" type="text/javascript"></script>
<title>余额查询</title>
</head>
<body onload="resizeAll();" style="padding: 0px; margin: 0px;">
	<div class="table">
	<s:if test="#mchntRlatBlacs.size > 0">
     <table border="0" cellpadding="0" cellspacing="0" class="tableBorder">
		<tr>
			<td align="center" nowrap="nowrap" class='title'>分公司商户名称</td>
			<td align="center" nowrap="nowrap" class='title'>分公司商户号</td>
			<td align="center" nowrap="nowrap" class='title'>账户名称</td>
			<td align="center" nowrap="nowrap" class='title'>账号</td>
			<td align="center" nowrap="nowrap" class='title'>开户行</td>
			<td align="center" nowrap="nowrap" class='title'>账面余额(元)</td>
			<td align="center" nowrap="nowrap" class='title'>可用余额(元)</td>
			<td align="center" nowrap="nowrap" class='title'>未结余额(元)</td>
			<td align="center" nowrap="nowrap" class='title'>冻结余额(元)</td>
	    </tr>
	    <s:iterator value="#mchntRlatBlacs" id="trade" status="trade">
	    <tr>
	    	<td align="center" nowrap="nowrap" class='tr1'><s:property value="INS_NAME_CN" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="MCHNT_CD" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="outAcntNm" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="outAcntNo" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="bankNm" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="balance0" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="balance1" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="balance2" /></td>
			<td align="center" nowrap="nowrap" class='tr1'><s:property value="balance3" /></td>
		</tr>
	    </s:iterator>
	</table>
	</s:if>
	<s:else>
	<center><div style="fond-size:12px;"><b>没有符合条件的记录！</b></div></center>
	</s:else>
	</div>
</body>
</html>