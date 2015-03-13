<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.text.SimpleDateFormat" %>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateTimeStr = sdf.format(dt);
    
	Date dt2 = new Date();
	dt2.setMonth(dt2.getMonth()-3);
	String dateTimeStr2 = sdf.format(dt2);

%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript" src="<%=root%>/js/common.js"></script>
<link href="<%=root%>/styles/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<form id="form1" name="form1" method="post" action="">
<div class="table" style="background: white;overflow:auto;height: 300px">
  <table width="945" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td align="center" nowrap="nowrap" class="title" width="87">姓名</td>
      <td align="center" nowrap="nowrap" class="title" width="122">账号行别</td>
      <td align="center" nowrap="nowrap" class="title" width="129">账号</td>
      <td align="center" nowrap="nowrap" class="title" width="120">记录生效时间</td>
      <td align="center" nowrap="nowrap" class="title" width="156">协议号</td>
      <td align="center" nowrap="nowrap" class="title" width="167">协议状态</td>
      <td align="center" nowrap="nowrap" class="title" width="122">记录状态</td>
      <td align="center" nowrap="nowrap" class="title">操作</td>
    </tr>
  <s:iterator value="#custmrBusiList" var="c">
    <tr <s:if test="#c.RESERVED2==1">
       disabled="disabled"
      </s:if>>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.USER_NM"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.BANK_CD"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.ACNT_NO"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:date name="#c.REC_CMT_TS" format="yyyy-MM-dd"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.CONTRACT_NO"/></td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#c.CONTRACT_ST==1">
                已生效
      </s:if>
      <s:elseif test="#c.CONTRACT_ST==0">
               未生效
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
      &nbsp;&nbsp;<a href="proclamAction_detailCustmr.action?custmrBusi.ROW_ID=<s:property value="#c.ROW_ID"/>&custmrBusi.MCHNT_CD=<s:property value="#c.MCHNT_CD"/>${parameter}">查看</a>
      </td>
    </tr>
  </s:iterator>
    <tr>
    <td colspan="8">
    <s:if test="#custmrBusiList.size>0">
    	${pagerStr} &nbsp;<input type="button" name="button" onclick="location.href='proclamAction_proclamActions.action'" id="button" value="返回" class="type_button"/>&nbsp;
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