
<%
	// Set request data code convertion
	request.setCharacterEncoding("utf-8");
	// Application common variables
	String root = request.getContextPath();
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
	response.setDateHeader("Expires", 0); 
%>
