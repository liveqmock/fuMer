<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>富友商户系统</title>
<link href="styles/all.css" rel="stylesheet" type="text/css" />
</head>
<div align="right" style="margin-right: 16%;margin-top: 10px;"><a href="changeLang.do?request_locale=zh_CN">简体中文</a>&nbsp;&nbsp;<a href="changeLang.do?request_locale=en_US">English</a></div>
<body>
<%
 	Object o = session.getAttribute("operatorInf");
	if(null!=o){
%>
	<script type="text/javascript">
		window.location.href="index.jsp";
	</script>
<%		
	}
%> 
<div id="indexImg">
<div id="indexLogin"><s:form name="form1" action="login.action" method="post" theme="simple">
	<div align="center"><s:actionerror/></div>
	<ul class="rightUl">
		<li class="li01"><s:textfield name="mChantCd" id="mChantCd" value="" cssClass="inputStyle" maxlength="15"/></li>
		<li class="li01"><s:textfield name="loginId" value="" id="loginId" cssClass="inputStyle" maxlength="20"/></li>
		<li class="li01"><s:password name="loginPwd" id="loginPwd" cssClass="inputStyle" /></li>
		<li class="li02"><s:textfield name="sRand" id="sRand" size="4" maxlength="4" cssClass="inputStyle" /></li>
		<li class="li03"><img alt="" src="image.jsp" width="57" height="23"/></li>
	</ul>
	<ul class="leftUl">
		<li><s:text name="login.form.mchntId"/>:</li>
		<li><s:text name="login.form.operatorId"/>:</li>
		<li><s:text name="login.form.password"/>:</li>
		<li><s:text name="login.form.verifyCode"/>:</li>
	</ul>
	<div class="clear"></div>
	<input type="submit" name="" class="submitBt" value="<s:text name="login.form.loginBtn"/>" />
</s:form></div>
<div id="indexFoot">上海富友支付服务有限公司 021-68547333<br />
上海浦东新区民生路1399号太平人寿大厦502室 邮编200135<br />
<s:text name="login.foot"/>
<a href="http://www.miibeian.gov.cn/" target="_blank">沪ICP备08023407号</a>
</div>
</div>
</body>
</html>