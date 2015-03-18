<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp" %>
<%@ taglib uri="/WEB-INF/page.tld" prefix="page"%>
<%@ page import="java.util.Date" %>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.text.SimpleDateFormat" %>
<%
    response.setHeader("Pragma", "No-Cache"); 
	response.setHeader("Cache-Control", "No-Cache"); 
	response.setDateHeader("Expires", 0); 
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateTimeStr = sdf.format(dt);
    
	Date dt2 = new Date();
	dt2.setMonth(dt2.getMonth()-12);
	String dateTimeStr2 = sdf.format(dt2);

%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<script type="text/javascript">
function checkBox(t){
	var c=document.getElementsByName("rowIds");
	for(var i=0;i<c.length;i++){
		var disabled = c[i].disabled;
		if(!disabled)
			c[i].checked=t;
	}
}
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="<%=root%>/js/jquery.js"></script>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<script type="text/javascript" src="<%=root%>/js/common.js"></script>
<link href="<%=root%>/styles/style.css" rel="stylesheet" type="text/css" />
<link href="<%=root %>/styles/all.css" rel="stylesheet" type="text/css" />
</head>
<body>
<form name="form1" id="form1" action="custmrBusi_selectCustmrBusis.action" method="post">
<%
	int totalRecords = (Integer)request.getAttribute("totalCount");
	if (0 == totalRecords) {
		out.println("<center><div style=\"fond-size:12px;\"><b>没有符合条件的记录	！</b></div></center>");
	}else {
%>
	<div class="table" style="background: white;overflow:auto;height: 500px">
  <table width="945" border="0" cellpadding="0" cellspacing="0" class="tableBorder">
  <page:pager total="<%=totalRecords %>">
    <tr>
      <td align="center" nowrap="nowrap" class="title">户名</td>
      <td align="center" nowrap="nowrap" class="title">签约渠道</td>
      <td align="center" nowrap="nowrap" class="title">账号行别</td>
      <td align="center" nowrap="nowrap" class="title">账号</td>
      <td align="center" nowrap="nowrap" class="title">业务类型</td>
      <td align="center" nowrap="nowrap" class="title">协议号</td>
      <td align="center" nowrap="nowrap" class="title">签约日期</td>
      <td align="center" nowrap="nowrap" class="title">协议状态</td>
      <td align="center" nowrap="nowrap" class="title">卡号户名验证</td>
      <td align="center" nowrap="nowrap" class="title">卡号密码验证</td>
      <td align="center" nowrap="nowrap" class="title">户名证件号验证</td>
      <td align="center" nowrap="nowrap" class="title">操作</td>
      <td align="center" nowrap="nowrap" class="title">
		<input type="checkbox" onclick="checkBox(this.checked)" title="批量解约" id="checkAll"/>
	  </td>
    </tr>
   <%int txnIndex = 0; %>
  <s:iterator value="#custmrBusiList" var="c">
    <tr <s:if test="#c.RESERVED2==1">
       disabled="disabled"
      </s:if>>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.USER_NM"/></td>
      <td align="center" nowrap="nowrap" class="tr1">
      	<s:if test="#c.GROUP_ID==5">POS</s:if>
      	<s:if test="#c.GROUP_ID==10">IVR</s:if>
      	<s:if test="#c.GROUP_ID==15">APP</s:if>
      </td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.BANK_CD"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.ACNT_NO"/></td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#c.BUSI_CD=='AC01'">代收</s:if>
      <s:elseif test="#c.BUSI_CD=='AP01'">付款</s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.CONTRACT_NO"/></td>
      <td align="center" nowrap="nowrap" class="tr1"><s:property value="#c.CONTRACT_SIGN_DT"/></td>
      <td align="center" nowrap="nowrap" class="tr1 status">
      <s:if test="#c.CONTRACT_ST==1">
                已生效
      </s:if>
      <s:elseif test="#c.CONTRACT_ST==2">
      	待验证
      </s:elseif>
      <s:elseif test="#c.CONTRACT_ST==0">
               未生效
               <s:if test="#c.RESERVED2==1">
               	,已解约
               </s:if>
               <s:if test="#c.RESERVED2==0">
               	,未解约
               </s:if>
      </s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#c.ACNT_IS_VERIFY_1==1">
                已通过
      </s:if>
      <s:elseif test="#c.ACNT_IS_VERIFY_1==0">
               未通过
      </s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#c.ACNT_IS_VERIFY_2==1">
                已通过
      </s:if>
      <s:elseif test="#c.ACNT_IS_VERIFY_2==0">
               未通过
      </s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1">
      <s:if test="#c.ACNT_IS_VERIFY_3==1">
                已通过
      </s:if>
      <s:elseif test="#c.ACNT_IS_VERIFY_3==0">
               未通过
      </s:elseif>
      </td>
      <td align="center" nowrap="nowrap" class="tr1 operTd">
      <s:if test="#c.CONTRACT_ST==1">
      	&nbsp;&nbsp;<a href="javascript:void(0)" class="unbind" onclick="unbind(<s:property value="#c.ROW_ID"/>)">解约</a>
      </s:if>
      <s:if test="#c.CONTRACT_ST==0">
      	&nbsp;&nbsp;<a href="javascript:void(0)" onclick="update(<s:property value="#c.ROW_ID"/>)">修改</a>
      </s:if>
      &nbsp;&nbsp;<a href="custmrBusi_findCustmrBusiById.action?custmrBusi.ROW_ID=<s:property value="#c.ROW_ID"/>&actionType=query" target="blank">查看</a>
      </td>
      <td align="center" nowrap="nowrap" class="tr1">
      <input type="checkbox" name="rowIds" class="checkbox" <s:if test="#c.RESERVED2==1">disabled="disabled"</s:if> value="<s:property value="#c.ROW_ID"/>"/>
      </td>
    </tr>
  </s:iterator>
    <tr>
    <td colspan="13">
    <s:if test="#custmrBusiList.size>0">
    	<button type="button" id="batBtn" style="float: right;" class="type_button">批量解约</button>
    </s:if>
    <s:else>
    	<h4 align="center">没有符合条件的信息</h4>
    </s:else>
    </td>
    </tr>
    <tr class="bg_column" height='20'>
				<td width="960" height="30" colspan="20" align="left">
					<page:navigator type='text' /><!-- 分页信息 --> 
					<input type="hidden" name="allDisplayCount" id="allDisplayCount" value="<%=txnIndex %>"/>
				</td>
			</tr>
    </page:pager>
  </table>
 </div>
<%
	}
%>
</form>
<script type="text/javascript">
	
	$(document).ready(function(){
		
		//解约
		$(".unbind").click(function(){
				var a = $(this);
				if(confirm("确定要解约该商户吗?")){
				var index=$(".operTd").index(a.parent());
				var rowId = $(".checkbox:eq("+index+")").val();
				$.post("custmrBusi_rescindCustmrBusi.action",{"custmrBusi.ROW_ID":rowId},function(response){
					if('true'==response){
						alert("解约成功");
						a.unbind("click");
						$("#form1").submit();
					}else{
						alert("解约失败，请稍后重试......");
					}
				}) 
			}  
		})
		
		$("#batBtn").click(function(){
			var size = $(".checkbox:checked").size();
			if(size==0){
				alert("请选择需要解约的客户");
				return ;
			}
			if(confirm("确定要解约吗?")){
				var s = '';
				$(".checkbox:checked").each(function(){
					s = s + $(this).val()+",";
				})		
				$.post("custmrBusi_rescindCustmrBusis.action",{"rowIds":s.substring(0,s.length-1)},function(response){
					alert(response);
					$("#form1").submit();
				})
			}
		})
		
	})
	
	//修改
	function update(rowId){
		window.parent.location.href="custmrBusi_findCustmrBusiById.action?custmrBusi.ROW_ID="+rowId+"&actionType=update";
	}
</script>
</body>
</html>
