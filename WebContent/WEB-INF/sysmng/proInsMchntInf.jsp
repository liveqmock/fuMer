<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>历史交易查询</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<link href="<%=root%>/styles/style.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	Date dt2 = new Date();
	dt2.setMonth(dt2.getMonth()-3);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String dateTimeStr = sdf.format(dt);
	String dateTimeStr2 = sdf.format(dt2);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript" src="<%=root%>/js/common.js"></script>
</head>
<body  onload="resizeAll();"  style="background-color:#e8eef7; padding:0px; margin:0px;">

<div class="table" style="background: white;overflow:auto;height: 300px">
<s:if test="#tInsMchntInfList.size>0">	
  <table width="945" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td align="center" nowrap="nowrap" class="title">商户名称</td>
      <td align="center" nowrap="nowrap" class="title">商户号</td>
      <td align="center" nowrap="nowrap" class="title">记录创建时间</td>
      <td align="center" nowrap="nowrap" class="title">商户业务风险等级</td>
      <td align="center" nowrap="nowrap" class="title">记录状态</td>
      <td align="center" nowrap="nowrap" class="title">操作</td>
    </tr>
  <s:iterator value="#tInsMchntInfList" var="c">
    <tr>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.INS_NAME_CN"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.MCHNT_CD"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:date name="#c.REC_CRT_TS" format="yyyy-MM-dd"/></td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#c.RESERVED2==1">
                高
      </s:if>
      <s:elseif test="#c.RESERVED2==2">
                中
      </s:elseif>
      <s:elseif test="#c.RESERVED2==3">
                低
      </s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#c.ROW_ST==1">
                已生效
      </s:if>
      <s:elseif test="#c.ROW_ST==0">
               未生效
      </s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1">
      <a href="proclamAction_detailMchnt.action?rowId=<s:property value="#c.ROW_ID"/>${parameter}">查看详情</a>
      </td>
    </tr>
  </s:iterator>
    <tr>
    <td colspan="6">
    <div>
    	<div style="float: left;">
    	${pagerStr}
    	</div>
    	<div style="float: left;">
    	&nbsp;<input type="button" name="button" onclick="location.href='proclamAction_proclamActions.action'" id="button" value="返回" class="type_button"/>&nbsp;
    	</div>
    </div>
    </td>
    </tr>
  </table>
</s:if>
<h4 align="center">${resultList}</h4>
</div>
</body>
</html>