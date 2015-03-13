<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=root %>/js/jquery.js"></script>
<title>账户充值</title>
</head>
<body>
	<s:form name="recharge" method="post" id="recharge" action="virtAcct_recharge.action" target = "struFrame" theme="simple">
<table>
<tr>
	<td align="right">
		开户行:
    </td>
	<td>
		<s:select cssStyle="width:204px; height:19px; border:#7f9db9 1px solid; margin-right:5px;" list="#request.bankList" listKey="BANK_CD" listValue="BANK_NM" id="bankList" name="bankList"  />
		<s:property value="fieldErrors.bankList"/><font color="red">*</font>
   </td>
</tr>
<tr>
	<td align="right">
		银行账号:
	</td>
	<td>
		<s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  maxlength="28" name="dedNum" id="dedNum"/>
		<s:property value="fieldErrors.dedNum"/><font color="red">*</font>
	</td>
<tr>
	<td align="right">
		充值金额:
	</td>
	<td>
		<s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  maxlength="28" name="amt" id="amt"/>
		<s:property value="fieldErrors.amt"/><font color="red">*</font>
	</td>
</tr>
 <tr>
            <td height="30"  align="right" colspan="2" style="padding-right: 16px;">
            		<input type="button" id="sure" width="80" name="btnsubId" class="type_button" value="确认">
            		<input type="submit" id="send" width="80" name="btnsubId" class="type_button" value="充值" disabled="disabled">
                    <input type="reset"  id="reset" width="80" name="reset"  class="type_button"   value="重置" >
            </td>
        </tr> 
</table>
	</s:form>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#sure").click(function(){
				$(this).attr("disabled",true);
				$("#bankList").attr("disabled",true);
				$("#dedNum").attr("disabled",true);
				$("#amt").attr("disabled",true);
				$("#send").attr("disabled",false);
			})
			
				$("#reset").click(function(){
					$("#sure").attr("disabled",false);
					$("#bankList").attr("disabled",false);
					$("#dedNum").attr("disabled",false);
					$("#amt").attr("disabled",false);
					$("#send").attr("disabled",true);
			})
		})
	</script>
</body>
</html>