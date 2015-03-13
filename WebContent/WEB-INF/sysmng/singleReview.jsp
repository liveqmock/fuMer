<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>单笔付款复核</title>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js" type="text/javascript"></script>
<script language="javascript" src="<%=root %>/js/jquery.js" type="text/javascript"></script>
<link href="<%=root %>/styles/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" language="javascript">
function allDisplay(allDisTp){
	var trnums=$("#allDisplayCount").val();
	if(allDisTp == '+'){
		$("#allImg").remove();
		var imgStr = '<img id="allImg" src="<%=root %>/images/-.gif" onClick="allDisplay(\'-\')" alt="全部合并"/>';
		$("#allTd").append(imgStr);
	}else{
		$("#allImg").remove();
		var imgStr = '<img id="allImg" src="<%=root %>/images/+.gif" onClick="allDisplay(\'+\')" alt="全部展开"/>';
		$("#allTd").append(imgStr);
	}
	for(i = 0;i<trnums;i++){
		var v = $(".displayClass"+i).length;
		if(allDisTp == '+' && v > 0){
			continue;
		}
		display(i,allDisTp);
	}
}
function display(trId,disTp){
	if(disTp == '+'){
		$("#img"+trId).remove();
		var imgStr = '<img id="img'+trId+'" src="<%=root %>/images/-.gif" onClick="display(\''+ trId +'\',\'-\')" alt="合并"/>';
		$("#td"+trId).append(imgStr);

		var v = $(".displayClass"+trId).length;
		if(disTp == '+' && v > 0){
			return;
		}
		
		var KBPS_SRC_SETTLE_DT = $("#KBPS_SRC_SETTLE_DT"+trId).val();
		var KBPS_TRACE_NO = $("#KBPS_TRACE_NO"+trId).val();
		var SRC_MODULE_CD = $("#SRC_MODULE_CD"+trId).val();
		var SUB_TXN_SEQ = $("#SUB_TXN_SEQ"+trId).val();
		var BUSI_CD = $("#BUSI_CD"+trId).val();

		$.ajax({
            type : "post", 
            url : "<%=root%>/rogatory/queryCurr_returnTxnList.action",
            data : "KBPS_SRC_SETTLE_DT="+KBPS_SRC_SETTLE_DT+"&KBPS_TRACE_NO="+KBPS_TRACE_NO+"&SRC_MODULE_CD="+SRC_MODULE_CD+"&SUB_TXN_SEQ="+SUB_TXN_SEQ+"&busiCd="+BUSI_CD,
            dataType : "json",
            success : function(data) {
            	var v = $(".displayClass"+trId).length;
        		if(disTp == '+' && v > 0){
        			return;
        		}
                var trStr = "";
                for ( var x = 0; x < data.length; x++) {
                	trStr += '<tr style= "background:#D8D8D8">'+
                				'<td align="center" nowrap="nowrap">'+data[x].ID_NO+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].KBPS_TRACE_NO+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].TXN_RCV_TS+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].bargaining+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].SRC_ORDER_NO+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].SRC_TXN_AMT+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].OP_ST+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].RESULT_ST+'</td>'+
                				'<td align="center" nowrap="nowrap">'+data[x].ADDN_PRIV_DATA+'</td>'+
                			'</tr>';
                }
              	//构建表格 
        		var str = '<tr align="center" class="displayClass'+trId+'">'+
        					'<td colspan="8" align="left" nowrap="nowrap">'+
        						'<table class="tableBorder" border="0" cellpadding="0" cellspacing="0" align="center">'+
        							'<tr style= "background:#A4A4A4">'+
        								'<td align="center" nowrap="nowrap">户名</td>'+
        					     		'<td align="center" nowrap="nowrap">交易流水号</td>'+
        					     		'<td align="center" nowrap="nowrap">交易提交时间</td>'+
        					     		'<td align="center" nowrap="nowrap">交易类型</td>'+
        					     		'<td align="center" nowrap="nowrap">文件名</td>'+
        					     		'<td align="center" nowrap="nowrap">交易金额</td>'+
        					     		'<td align="center" nowrap="nowrap">交易状态</td>'+
        					     		'<td align="center" nowrap="nowrap">结果状态</td>'+
        					     		'<td align="center" nowrap="nowrap">注释</td>'+
        					     	'</tr>'+trStr+
        						'</table>'+
        					'</td>'+
        				  '</tr>';
        		$("#tr"+trId).after(str);
        		str = "";
            }
        });
	}else if(disTp == '-'){
		$("#img"+trId).remove();
		var imgStr = '<img id="img'+trId+'" src="<%=root %>/images/+.gif" onClick="display(\''+ trId +'\',\'+\')" alt="展开"/>';
		$("#td"+trId).append(imgStr);
		$("tr").remove(".displayClass"+trId);
	}
}
</script>
</head>

<body style="padding: 0px; margin: 0px;">
<%
	int totalRecords = (Integer)request.getAttribute("fileCount");
	String message = (String)request.getAttribute("message");
	//out.println("totalRecords=" + totalRecords);
	if(0 == totalRecords) {
		out.println("<center><div style=\"fond-size:12px;\"><b>" + message + "</b></div></center>");
		return ;
	}else {
%>
<s:form name="form1" action="proclamAction_getAps.action" method="post" theme="simple">
	<div class="table">
     <table  class="tableBorder" border="0" cellpadding="0" cellspacing="0" align="center">
    	 <tr>
    	 <s:if test="#fileBusiTp == 1">
    	  <td id="allTd" align="center" nowrap="nowrap" class='title'>
    	  	<img id="allImg" src="<%=root %>/images/+.gif" onClick="allDisplay('+')" alt="全部展开"/>
    	  </td> 
    	  </s:if>	
    	  <td align="center" nowrap="nowrap" class='title'>户名</td>
    	  <td align="center" nowrap="nowrap" class='title'>业务类型</td>
    	  <td align="center" nowrap="nowrap" class='title'>收款帐号</td>
    	  <td align="center" nowrap="nowrap" class='title'>交易金额</td>
		  <td align="center" nowrap="nowrap" class='title'>交易提交时间</td>
			<td align="center" nowrap="nowrap" class='title'>流水号</td>
			<td align="center" nowrap="nowrap" class='title'>操作</td>
		</tr>
		<page:pager total="<%=totalRecords %>">
		<%int txnIndex = 0; %>
			<s:iterator value="#request.fileList" id="trade" status="trade">
				<s:if test="#trade.odd == true">
			    	<tr class="tr1" id="tr<%=txnIndex %>">
			    </s:if>
			    <s:else>
			    	<tr class="tr2" id="tr<%=txnIndex %>">
			    </s:else>
			    <s:else>
			    	<s:if test="#fileBusiTp == 1">
			    	<td></td>
			    	</s:if>
			    </s:else>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="ID_NO" /></td>
					<td align="center" nowrap="nowrap" class='tr1'>
					<s:if test="%{BUSI_CD=='AC01'}">代收</s:if>
					<s:elseif test="%{BUSI_CD=='YZ01'}">账户验证</s:elseif>
					<s:elseif test="%{BUSI_CD=='AP01'}">付款</s:elseif>
					</td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="CREDIT_ACNT_NO" /></td> 
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="SRC_TXN_AMT"/></td>
					<td align="center" nowrap="nowrap" class='tr2'><s:property value="TXN_RCV_TS" /></td>
					<td align="center" nowrap="nowrap" class='tr1'><s:property value="KBPS_TRACE_NO" /></td>
					<td align="center" nowrap="nowrap" class='tr1'>					
					<a href='${pageContext.request.contextPath}/payfor/PayforReviewAction_singleReview.action?KBPS_TRACE_NO=<s:property value="KBPS_TRACE_NO" />&KBPS_SRC_SETTLE_DT=<s:property value="KBPS_SRC_SETTLE_DT" />&fileBusiTp=<s:property value="BUSI_CD"/>' >复核&nbsp;|&nbsp;</a>
					
					<a href='${pageContext.request.contextPath}/payfor/PayforReviewAction_singleReviewNoPass.action?KBPS_TRACE_NO=<s:property value="KBPS_TRACE_NO" />&KBPS_SRC_SETTLE_DT=<s:property value="KBPS_SRC_SETTLE_DT" />&fileBusiTp=<s:property value="BUSI_CD"/>' >复核不通过&nbsp;|&nbsp;</a>	
					</td>
				</tr>
				<%txnIndex++; %>
			</s:iterator>
			<tr class="bg_column" height='20'>
				<td height="30" colspan="8" align="left">
				<div>
				<div style="float: left;">
				<page:navigator
					type='text' />
					<input type="hidden" name="allDisplayCount" id="allDisplayCount" value="<%=txnIndex %>"/>
				</div>
				<div style="float: left;">
				&nbsp;
				<input type="button" class="type_button" value="返回" onclick="location.href='proclamAction_proclamActions.action'"/>
				</div>
				</div>
				</td>
			</tr>
		</page:pager>
	</table> 
	</div>
</s:form>
<%
	}
%>
</body>
</html>