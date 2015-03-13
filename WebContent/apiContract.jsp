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
	<form action="<%=root%>/api_contract.do" method="post">
		<table>
			<tr>
				<td>XML:</td>
				<td>
					<textarea rows="30" cols="80" name="xml">
						<?xml version="1.0" encoding="UTF-8"?>
						<custmrBusi>
								<srcChnl>WEB</srcChnl>
								<busiCd>AC01</busiCd>
								<bankCd>0104</bankCd>
								<userNm>陆琪</userNm>
								<mobileNo>13905193853</mobileNo>
								<credtTp>0</credtTp>
								<credtNo>320882198508135213</credtNo>
								<acntTp>01</acntTp>
								<acntNo>6228480041012957217</acntNo>
								<mchntCd>0002900F0345178</mchntCd>
								<isCallback>0</isCallback>
								<reserved1>保留字段1</reserved1>
								<signature>asdada</signature>
						</custmrBusi>
					</textarea>
				</td>
			</tr>
		</table>
		<input type="submit" value="提交"/>
	</form>
</body>
</html>