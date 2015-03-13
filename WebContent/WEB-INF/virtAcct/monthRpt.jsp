<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/common.jsp"%>
<%@ page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.fuiou.mer.util.TDataDictConst"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/fht/styles/stylediv.css" rel="stylesheet" type="text/css" />
<style type="text/css">
	.type_text {   
	width: 120px;
	height: 19px;
	border: #7f9db9 1px solid;
	margin-right: 5px;
}
body, html {
	width: 100%;
	border: 0px;
	padding: 0px;
	margin: 0px;
	line-height: 20px;
	font-family: "锟斤拷锟斤拷";
	font-size: 12px;
	color: #333333;
	background-color: #FFFFFF;
}.type_button {
	background: url( <%=root %>/images/buttonbg.gif ) repeat-x;
	text-align: center;
	color: #FFFFFF;
	border: #7fb5ea 1px solid;
	padding: 0px 12px 2px 12px !important;
	padding: 1px 8px 1px 8px;
	height: 22px !important;
	height: 23px;
	cursor: pointer;
}
</style>
<link href="/fht/styles/jquery.autocomplete.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=root %>/js/jquery.js"></script>
<script language="javascript" type="text/javascript" src="<%=root %>/My97DatePicker/WdatePicker.js"></script>
<script language="javascript" src="<%=root %>/styles/iframe.js" type="text/javascript"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#queryBtn").click(function(){
			var acnt = $("#acnt option:selected").val();
			$.post("<%=root %>/queryBlac.action",{acnt:acnt},function(response){
				if(Number(response) == -1){
					alert("查询出错！");
				}else{
					var result = response.replace("\"","");
					var arrays = result.split("|",4);
					$("#balance0").text(arrays[0]+'元');
					$("#balance1").text(arrays[1]+'元');
					$("#balance2").text(arrays[2]+'元');
					$("#balance3").text(arrays[3]+'元');
					$("#div").show();					
				}
			},'json');
		});
		
		$("#queryBtn2").click(function(){
			var reg=/^(\d{4})(\d{2})(\d{2})$/;  
			var startDt = document.getElementById('startDt').value;
			if(!reg.test(startDt)){
				alert('地球上的日期格式是yyyyMMdd这样的，重输吧-,-!');
				document.getElementById('startDt').focus();
				return ;
			}
			var endDt = document.getElementById('endDt').value;
			if(!reg.test(endDt)){
				alert('地球上的日期格式是yyyyMMdd这样的，重输吧-,-!');
				document.getElementById('endDt').focus();
				return ;
			}
			if(startDt.substring(0,6) != endDt.substring(0,6)){
				alert("暂不支持跨月查询-,-!");
				document.getElementById('endDt').focus();
				return false;
			}
			var acnt=$("#acnt").val();
			var realBookInd = $(".realBookInd:checked").val();
			document.getElementById("iframeId").src="virtacct_queryFasTxnLog.action?startDt="+startDt+"&endDt="+endDt+"&acnt="+acnt+"&realBookInd="+realBookInd;		
			$("#iframeDiv").show();
		});
		
	});
	
	function downloadDetails(date){
		var reg=/^(\d{4})(\d{2})(\d{2})$/;  
		var startDt = document.getElementById('startDt').value;
		if(startDt !='' &&!reg.test(startDt)){
			alert('地球上的日期格式是yyyyMMdd这样的，重输吧-,-!');
			document.getElementById('startDt').focus();
			return ;
		}
		var endDt = document.getElementById('endDt').value;
		if(endDt !='' &&!reg.test(endDt)){
			alert('地球上的日期格式是yyyyMMdd这样的，重输吧-,-!');
			document.getElementById('endDt').focus();
			return ;
		}
		if(startDt.substring(0,6) != endDt.substring(0,6)){
			alert("后台系统弱爆了,暂不支持跨月查询-,-!");
			document.getElementById('endDt').focus();
			return false;
		}
		document.getElementById("form").action = "virtacct_expMonthRpt.action";
		document.getElementById("form").method = "post";
		document.getElementById("form").target = "data_eara_frame_up";
		document.getElementById("form").submit();
	}
</script>
<title>余额查询</title>
</head>
<body onload="resizeAll();" style="padding: 0px; margin: 0px;">
	<s:form  method="post" action="virtacct_queryDetails.action" theme="simple" id="form">
		<div style="background-color: #e8eef7;">
		&nbsp;&nbsp;请选择账户：
		<select id="acnt" name="acnt">
			<s:iterator value="acntNos">
				<option value='<s:property/>'><s:property/></option>
			</s:iterator>
		</select>
		<input type="button" id="queryBtn" value="确定" class="type_button"/>
		<div style="display: none;" id="div"> 
			&nbsp;&nbsp;&nbsp;&nbsp;该账户的账面余额为：<label id="balance0" style="font-weight: bold;"></label>
			其中可用余额为：<label id="balance1" style="font-weight: bold;"></label>
			 未结余额为：<label id="balance2" style="font-weight: bold;"></label>
			 冻结余额为：<label id="balance3" style="font-weight: bold;"></label><br>
				&nbsp;&nbsp;&nbsp;&nbsp;筛选明细记录<br>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" checked="checked" name="realBookInd"  value="2" class="realBookInd">虚帐&nbsp;<input type="radio"  class="realBookInd" name="realBookInd" value="1">实帐
			交易日期：<input type="text" id="startDt" name="startDt" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})" class="Wdate"/>-<input type="text" id="endDt" name="endDt" onfocus="WdatePicker({dateFmt:'yyyyMMdd'})" class="Wdate"/>					
			<input type="button" id="queryBtn2" value="查看交易记录" class="type_button"/>&nbsp;
<!-- 			<input type="button" id="queryBtn3" value="下载交易记录" class="type_button" onclick="downloadDetails('date')"/>			 -->
			<input type="hidden" value="1" name="pageNo"/>
		</div> 
		</div>
		<div style="display: none; text-align: center; padding-left: 4px; padding-top:4px; width: 100%;" id="iframeDiv">
		<iframe id="iframeId" src="" style="height: 350px;width: 100%;" frameborder="0" scrolling="auto"></iframe>
		</div>
	</s:form>   
</body>
</html>