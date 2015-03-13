<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ include file="/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支行别名-新增</title>
<link href="<%=root%>/styles/style_up.css" rel="stylesheet" type="text/css" />
</head>
<body onload="resizeAll();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
<s:form method="post" name="form" action="branchBankAliasAdd.action" theme="simple" target="struFrame">
    <s:actionerror/>
    <table>
        <tr>
            <td align="right">关键字：</td>
            <td>
                <s:textfield name="branchBankAlias" id="branchBankAlias" maxlength="80"/>
                <s:property value="fieldErrors.branchBankAlias"/><font color="red">*</font>
            </td>
        </tr>
        <tr>
            <td align="right">标准名称：</td>
            <td>
                <s:textfield name="branchBankName" id="branchBankName" maxlength="80"/>
                <s:property value="fieldErrors.branchBankName"/><font color="red">*</font>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td align="left"><s:submit cssClass="type_button" name="确认" value="确认" /></td>
        </tr>
    </table>
</s:form>
</div>
</body>
</html>