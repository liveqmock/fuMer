<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统|批量上传贷记文件</title>
<sx:head/>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateTimeStr = sdf.format(dt);
	
	String feeMsg = (String)request.getAttribute("feeMsg");
	String isDisplayFeeMsg = (String)request.getAttribute("isDisplayFeeMsg");
	
	String busiCd = (String)request.getAttribute("busiCd");
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
</head>
<body onload="resizeAll();" style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<div id="condition">
<s:form name="form1" action="/payfor/payForOnce_payForRequest.action" method="post" target = "struFrame" enctype="multipart/form-data" theme="simple" onsubmit="submit.disabled=true; reset.disabled=true;">
<s:hidden name ="selectProv" />                            
<s:hidden name ="selectProv_nm" />              
<s:hidden name ="selectProv_cd" />              
<s:hidden name ="selectCity" />                 
<s:hidden name ="selectCity_nm" />              
<s:hidden name ="selectCity_cd" />              
<s:hidden name ="selectBank" />                 
<s:hidden name ="selectBank_nm" />              
<s:hidden name ="selectBank_cd" />              
<s:hidden name ="bankNam" />
<s:hidden name ="bankNam_nm" />                
<s:hidden name ="bankNam_cd" />                     
<s:hidden name ="bankNo_nm" />                  
<s:hidden name ="accessBean.bankAccount" />                  
<s:hidden name ="accessBean.accountName" />                
<s:hidden name ="accessBean.amount" />                  
<s:hidden name ="accessBean.enpSeriaNo" />             
<s:hidden name ="accessBean.memo" />                  
<s:hidden name ="accessBean.mobile" />
<table >
		<tr>
			<td align="right"><s:text name="开户省：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{selectProv}" name="selectProv" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="开户市：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{selectCity}" name="selectCity" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="开户银行：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{selectBank}" name="selectBank" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="开户行名称：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{bankNam}" name="bankNam" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="开户行行号：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{bankNam_cd}" name="bankNam_cd" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="付款人银行账号：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.bankAccount}" name="accessBean.bankAccount" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="户名：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.accountName}" name="accessBean.accountName" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
            <td align="right"><s:text name="金额(单位:元)：" /></td>
            <td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.amount}" name="accessBean.amount" disabled="true"/>&nbsp;
            <%
            if("1".equals(isDisplayFeeMsg)){
            %>
            <font color="red">手续费：${feeAmt }元/笔</font></td>
            <%
            	}
            %>
        </tr>
		<tr>
			<td align="right"><s:text name="企业流水账号：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.enpSeriaNo}" name="accessBean.enpSeriaNo" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="备注：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.memo}" name="accessBean.memo" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td align="right"><s:text name="手机号：" /></td>
			<td><s:textfield cssClass="type_text" cssStyle="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  value="%{accessBean.mobile}" name="accessBean.mobile" disabled="true"/>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
				<%
					if("".equals(feeMsg)){
				%>
					<input type="submit" class="type_button" name="submit" value="确认发起交易" >
					<%
					if("AP01".equals(busiCd)){
					%>
						<input type="button" class="type_button" name="reset" onclick="javascript:window.location.href='doNothing.action?forwardAction=/WEB-INF/payfor/PayforReq.jsp';" value="取消" />
					<% 
					}
					%>
		            <%
		            	if("1".equals(isDisplayFeeMsg)){
		            %>
		            <br/><font color="red">收您${srcAmt }元，实际到账金额${destAmt }元</font>
		            <%
		            	}
		            %>
				<%
					}else{
				%>
					<font color="red"> <%=feeMsg %></font>
				<%
					}
				%>
            </td>
		</tr>
	</table>
	<s:hidden name="accessBean.busiCd" id="busiCd" value="%{accessBean.busiCd}"></s:hidden>
	<s:hidden name="provCode" id="provCodeId" value="%{provCode}"></s:hidden>
	<s:hidden name="cityCodeName" id="cityCodeId" value="%{cityCode}"></s:hidden>
	<s:hidden name="bankCodeName" id="bankCodeId" value="%{bankCode}"></s:hidden>
</s:form>
</div>
</body>
</html>