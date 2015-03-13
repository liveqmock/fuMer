<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String dateTimeStr = sdf.format(dt);
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript" src="<%=root%>/js/common.js"></script>
<link href="<%=root%>/styles/style.css" rel="stylesheet" type="text/css" />
<link href="styles/leftmenu.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/all.css" rel="stylesheet" type="text/css" />
</head>
<body>
<form id="form1" name="form1" method="post" action="userInfoMgr_addInit.action">
<div class="table">
  <table width="945" border="0" cellpadding="0" cellspacing="0" class="tableBorder">
    <tr>
      <td align="center" nowrap="nowrap" class="title" width="87">户名</td>
      <td align="center" nowrap="nowrap" class="title" width="129">省份</td>
      <td align="center" nowrap="nowrap" class="title" width="120">市/县</td>
      <td align="center" nowrap="nowrap" class="title" width="156">开户行</td>
      <td align="center" nowrap="nowrap" class="title" width="167">账号</td>
      <td align="center" nowrap="nowrap" class="title" width="122">手机号码</td>
      <td align="center" nowrap="nowrap" class="title" width="122">证件类型</td>
      <td align="center" nowrap="nowrap" class="title" width="122">证件号</td>
      <td align="center" nowrap="nowrap" class="title" colspan="2">操作</td>
    </tr>
  <s:iterator value="#userList" var="u">
    <tr>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#u.USER_NAME"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#u.PROV_NAME"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#u.CITY_NAME"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#u.BANK_NAME"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#u.ACCT_NO"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#u.MOBILE_NO"/></td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#u.CUSTMR_NO_TP==''"></s:if>
      <s:elseif test="#u.CUSTMR_NO_TP==0">身份证</s:elseif>
      <s:elseif test="#u.CUSTMR_NO_TP==1">护照</s:elseif>
      <s:elseif test="#u.CUSTMR_NO_TP==2">军官证</s:elseif>
      <s:elseif test="#u.CUSTMR_NO_TP==3">士兵证</s:elseif>
      <s:elseif test="#u.CUSTMR_NO_TP==5">户口本</s:elseif>
      <s:elseif test="#u.CUSTMR_NO_TP==7">其他</s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#u.CUSTMR_NO"/></td>
      <td align="center" nowrap="nowrap" class="tr1" width="66"><a target="data_eara_frame_top" href="userInfoMgr_eidtUser.action?user.ROW_ID=<s:property value="#u.ROW_ID"/>">编辑</a></td>
      <td align="center" nowrap="nowrap" class="tr1" width="60"><a href="javascript:if(confirm('确定要删除吗?')){location.href='userInfoMgr_deleteUser.action?user.ROW_ID=<s:property value="#u.ROW_ID"/>';}">删除</a></td>
    </tr>
  </s:iterator>
    <tr>
    <td colspan="10">
    <s:if test="#userList.size>0">
    	${pagerStr}
    </s:if>
    <s:else>
    	<h4 align="center">没有符合条件的信息</h4>
    </s:else>
    </td>
    </tr>
  </table>
  </div>
</form>
</body>
</html>