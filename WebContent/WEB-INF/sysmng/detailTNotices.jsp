<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>富友商户系统</title>
<link href="<%=root%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="http://cdn.bootcss.com/html5shiv/3.7.0/html5shiv.js"></script>
  <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</head>
<body>
  <div class="container">
  	<h3 class="page-header"><s:property value="#request.notice.NOTICE_TITLE"/></h3>
  	<p class="lead"><s:property value="#request.notice.NOTICE_CONTENT"/></p>
  	<div class="row">
  	     <div class="col-md-4 col-md-offset-8">
	       <s:if test="#request.notice.attachmentName != null">
		      <input type="button" class="btn btn-default" value="下载附件" onclick="download('<s:property value="#request.notice.NOTICE_NO"/>')"/>
	       </s:if>
	          <input type="button" class="btn btn-default" value="返回" onclick="history.go(-1)"/>
	     </div>
    </div>
  </div>
<form id="form1" name="form1" method="post" action="proclamAction_.action">
	<input type="hidden" name="notice.NOTICE_NO" id="noticeNo"/>
</form>
</body>
  <script src="<%=root%>/bootstrap/js/jquery-1.11.2.min.js"></script>
  <script src="<%=root%>/bootstrap/js/bootstrap.min.js"></script>
  <script type="text/javascript">
	function download(noticeNo){
		document.getElementById("noticeNo").value=noticeNo;
		document.getElementById("form1").submit();
	}
  </script>
</html>