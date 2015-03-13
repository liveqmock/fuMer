<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="com.fuiou.mer.util.TDataDictConst"%>
<%
    Map busiCdMap=new LinkedHashMap();
	busiCdMap.put(TDataDictConst.BUSI_CD_REFUND_TICKET, "退票付款失败");
    request.setAttribute("busiCdMap",busiCdMap);
    
    Map destTxnStMap=new LinkedHashMap();
    destTxnStMap.put("", "--请选择--");
    request.setAttribute("destTxnStMap",destTxnStMap);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>退票交易查询</title>
<script language="javascript" src="<%=root %>/js/SearchMerchant.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<link href="<%=root %>/styles/style_up.css" rel="stylesheet" type="text/css" />
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
	Date dt = new Date();
	Date dt2 = new Date();
	dt2.setMonth(dt2.getMonth()-6);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateTimeStr = sdf.format(dt);
	String dateTimeStr2 = sdf.format(dt2);
%>
<script language="javascript" src="<%=root %>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript">
	function query(){
		document.form1.action="returnTicket_execute.action";
		document.form1.submit();
	}
	function download(){
		document.form1.action="downloadHis.action";
		document.form1.submit();
	}
</script>
</head>
<body  onload="resizeAll();"  style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition">
<s:form name="form1" action="" method="post" target="data_eara_frame_down" theme="simple">
	<table>
        <s:actionerror/>
        <tr>
          	<td><s:text name="开始时间：" /></td>
            <td><input type="text" style="width:95px;" class="Wdate" name="fromDate" id="fromDate" value="<%=dateTimeStr2 %>" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})"/> </td>
         	<td></td>
         	<td>&nbsp;<s:text name="结束时间：" /></td>
         	<td><input type="text" Style="width:95px;" class="Wdate" name="toDate" id="toDate" value="<%=dateTimeStr %>" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})"/> </td>
        	<td></td>
        	<td align="right" nowrap="nowrap"><s:text name="户名：" /></td>
			<td><s:textfield name="idNo" id="idNo" cssStyle="width:95px;"/></td>
        	<td align="right"><s:text name="文件名：" /></td>
            <td><s:textfield name="srcOrderNo" id="srcOrderNo" cssStyle="width:105px;"/></td>
            <td><s:property value="fieldErrors.srcOrderNo"/> </td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td align="right"><s:text name="业务类型：" /></td>
            <td><s:select list="#request.busiCdMap"  id="busiCd" name="busiCd" onchange="check()" cssStyle="width:95px;"></s:select></td>
            <td><s:property value="fieldErrors.dedNum"/> 
            	<input type="hidden" name="destTxnSt" value=""/>
            </td>
            <td align="right"><s:text name="账号：" /></td>
            <td><s:textfield name="dOrcAccountNo" id="dOrcAccountNo" cssStyle="width:95px;"/></td>
            <td><s:property value="fieldErrors.dOrcAccountNo"/> </td>
            <td align="right" nowrap="nowrap"><s:text name="金额范围：" /></td>
			<td><s:textfield name="minAmt" id="minAmt" onblur="checkAmt(this.value)" cssStyle="width:95px;"/>-</td>
			<td><s:textfield name="maxAmt" id="maxAmt" onblur="checkAmt(this.value)" cssStyle="width:95px;"/>元</td>
            <td> <button onclick="query();" class="type_button" id="queryBtn">查询</button>
            	&nbsp;&nbsp;&nbsp;<button onclick="download();" class="type_button" id="downloadBtn">下载</button>
            </td>
                        <td>&nbsp;</td>
            
        </tr>
	</table>
</s:form>
</div>
</body>
<script type="text/javascript">
	function check(){
		var busicd = document.getElementById("busiCd").value;
		if(busicd == 'TP01'){
			document.all[ "destTxnSt"].disabled=true; 
		}else{
			document.all[ "destTxnSt"].disabled=false;
		}
	}
	
	function checkAmt(){
		var minAmt = document.getElementById("minAmt").value;
		var maxAmt = document.getElementById("maxAmt").value;
		minAmt = minAmt.replace(/(^\s*)|(\s*$)/g, "");  
		maxAmt = maxAmt.replace(/(^\s*)|(\s*$)/g, "");  
		if(minAmt == '' && maxAmt == ''){
			document.getElementById("queryBtn").disabled=false;
			document.getElementById("downloadBtn").disabled=false;			
		}else{
			var pattern =/^-?\d+\.{0,}\d{0,}$/;
			if(minAmt != '' && !pattern.test(minAmt) ){
				alert("请输入正确的金额");
				document.getElementById("queryBtn").disabled=true;
				document.getElementById("downloadBtn").disabled=true;
				return ;
			}
			if(maxAmt != '' && !pattern.test(maxAmt) ){
				alert("请输入正确的金额");
				document.getElementById("queryBtn").disabled=true;
				document.getElementById("downloadBtn").disabled=true;
				return ;
			}
			document.getElementById("queryBtn").disabled=false;
			document.getElementById("downloadBtn").disabled=false;		
		}
	}
</script>
</html>