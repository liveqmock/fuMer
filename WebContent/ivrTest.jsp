<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%-- <form action="<%=root%>/contract_getMhcntListByCard.do" method="post"> --%>
<%-- <form action="<%=root%>/contract_isContractMobile.do" method="post"> --%>
<%-- 	<form action="<%=root%>/contract_orderPay.do" method="post"> --%>
	<form action="<%=root%>/contract_vpcCrypt.do" method="post">
<%-- 		<form action="<%=root%>/contract_getOrderInf.do" method="post"> --%>
		<table>
			<tr>
				<td>XML:</td>
				<td>
					<textarea rows="10" cols="40" name="xml"></textarea>
				</td>
			</tr>
		</table>
		<input type="submit" value="提交"/>
	</form>
</body>
</html>