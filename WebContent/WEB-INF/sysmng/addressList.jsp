<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统</title>
<script language="javascript" src="<%=root%>/js/SearchMerchant.js"></script>
<script language="javascript" src="<%=root%>/js/jquery.js"
	type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/verify.js"
	type="text/javascript"></script>
<link href="<%=root%>/styles/stylediv.css" rel="stylesheet"
	type="text/css" />
<script language="javascript" src="<%=root%>/styles/iframe.js"
	type="text/javascript"></script>
</head>
<body onload="resizeAll();"	style="background-color: #e8eef7; padding: 0px; margin: 0px;">
<s:form action="addressList_findByMchntCd.action" method="post" id="form">
	<div id="container">
		<s:iterator value="#request.list" var="a">
			<table class="contactors">
				<tr>
					<td colspan="9"><strong><s:property value="#a.RESERVED1"/></strong></td>
				</tr>
				<tr>
					<td>姓名：</td>
					<td><input type="text" style="width: 100px;" value="<s:property value="#a.NAME"/>" disabled="disabled" class="name"/></td>
					<td>座机号：</td>
					<td><input type="text" style="width: 120px;" value="<s:property value="#a.PHONE"/>" disabled="disabled" class="phone"/></td>
					<td>手机号：</td>
					<td><input type="text" style="width: 100px;" value="<s:property value="#a.MOBILEPHONE"/>" disabled="disabled" class="mobile"/></td>
					<td>邮箱：</td>
					<td><input type="text" style="width: 120px;" value="<s:property value="#a.EMAIL"/>" disabled="disabled" class="email"/></td>
					<td>
						<s:hidden name="ROW_ID" cssClass="rowid"/>
						<input type="button" value="修改" onclick="update(this)" class="updateBtn">&nbsp;
						<input type="button" value="保存" onclick="save(this,'update')"  disabled="disabled" class="saveBtn">&nbsp;
						<input type="button" value="删除" onclick="deleteByRowId(this)" class="deleteBtn">
					</td>
				</tr>
		</table>
		</s:iterator>
	</div>
	<s:if test="list.size()>0">
			<p>&nbsp;</p>
			<div>
				<strong>公共邮箱：</strong><input type="text" name="publicEmail" id="publicEmail" value="<s:property value="#request.msg"/>" <s:if test="#request.msg!=null">disabled="disabled"</s:if>/>
				<input type="button" value="修改" id="updatePublicEmailBtn" onclick="updatePublicEmail()" >&nbsp;
				<input type="button" value="保存" id="savePublicEmailBtn" onclick="savePublicEmail()" <s:if test="#request.msg!=null">disabled="disabled"</s:if>>&nbsp;
			</div>
		</s:if>
</s:form>
	<div align="left">
		<input type="button" class="type_button" id="addNewBtn" value="添加新联系人" />
	</div>
	<table id="demo" style="display: none;">
		<tr>
			<td colspan="9"><select>
					<option value="">--请选择职位名称--</option>
					<s:iterator value="#request.positions" var="p">
						<option value="<s:property value="#p.POSITION_NO"/>|<s:property value="#p.POSITION_NAME"/>"><s:property value="#p.POSITION_NAME"/></option>
					</s:iterator>
			</select></td>
		</tr>
		<tr>
			<td>姓名：</td>
			<td><input type="text" style="width: 100px;" class="name"/></td>
			<td>座机号：</td>
			<td><input type="text" style="width: 120px;" class="phone"/></td>
			<td>手机号：</td>
			<td><input type="text" style="width: 100px;" class="mobile"/></td>
			<td>邮箱：</td>
			<td><input type="text" style="width: 120px;" class="email"/></td>
			<td><input type="button" value="修改"  onclick="update(this)"  disabled="disabled" class="updateBtn">&nbsp;<input type="button" value="保存" onclick="save(this,'add')" class="saveBtn">&nbsp;<input type="button" value="删除" onclick="deleteByRowId(this)" class="deleteBtn"></td>
		</tr>
		<tr>
		</tr>
	</table>
</body>
<script type="text/javascript">
		$(document).ready(function() {
			$("#addNewBtn").click(function() {
				var table= $("#demo").clone();
				table.css("display","block");
				table.removeAttr("id");
				table.attr("class","contactors");
				$("#container").append(table);
			})
			
		})
		
		function save(btn,flag){
			var index = $(".saveBtn").index(btn);
			var name = $(".name:eq("+index+")").val();
			var phone = $(".phone:eq("+index+")").val();
			var mobile = $(".mobile:eq("+index+")").val();
			var email = $(".email:eq("+index+")").val();
			var rowid,positionInf;
			if('add' == flag){
				positionInf = $(".contactors:eq("+index+") select option:selected").val();
				if(positionInf == ''){
					alert("请选择职务信息");
					return ;
				}
			}else{
				rowid = $(".rowid:eq("+index+")").val();
			}
			if(name == undefined || name == ''){
				alert("请输入姓名");
				return ;
			}
			if(phone !='' && !verifyPhoneNum(phone)){
				alert("请输入正确的座机号码");
				return ;
			}
			if(mobile !='' && !verifyMobile(mobile)){
				alert("请输入正确手机号码");
				return ;
			}
			if(email != '' && !verifyEmail(email)){
				alert("请输入正确的邮箱");
				return ;
			}
			if('add'==flag){
				$.post("addressList_addNew.action",{"bean.NAME":name,"bean.PHONE":phone,"bean.MOBILEPHONE":mobile,"bean.EMAIL":email,"bean.POSITION_NO":positionInf},function(resp){
					alert(resp);
					location.reload();
				})	
			}else{
				$.post("addressList_updateByPK.action",{"bean.NAME":name,"bean.PHONE":phone,"bean.MOBILEPHONE":mobile,"bean.EMAIL":email,"bean.ROW_ID":rowid},function(resp){
					alert(resp);
					location.reload();
				})
			}
		}
		
		function update(btn){
			var index = $(".updateBtn").index(btn);
			$(".name:eq("+index+")").attr("disabled",false);
			$(".phone:eq("+index+")").attr("disabled",false);
			$(".mobile:eq("+index+")").attr("disabled",false);
			$(".email:eq("+index+")").attr("disabled",false);
			$(".saveBtn:eq("+index+")").attr("disabled",false);
			btn.disabled = true;
		}
		
		function deleteByRowId(btn){
			var index = $(".deleteBtn").index(btn);
			var rowIdSize = $(".rowid:eq("+index+")").size();
			if(rowIdSize == 0){
				$(".contactors:eq("+index+")").remove();
			}else{
				if(confirm("确定要删除此条记录?")){
					btn.disabled=true;
					var index = $(".deleteBtn").index(btn);
					var rowid = $(".rowid:eq("+index+")").val();
					$.post("addressList_deleteByPK.action",{"bean.ROW_ID":rowid},function(resp){
						alert(resp);
						location.reload()
					})	
				}
			}
		}
		
		function updatePublicEmail(){
			$("#publicEmail").attr("disabled",false);
			$("#savePublicEmailBtn").attr("disabled",false);
		}
		
		function savePublicEmail(){
			var publicMail = $("#publicEmail").val();
			if(!verifyEmail(publicMail)){
				alert("邮件格式错误");
				return ;
			}
			$.post("addressList_setPublicMail.action",{msg:publicMail},function(resp){
				alert(resp);
				$("#publicEmail").attr("disabled",true);
				$("#savePublicEmailBtn").attr("disabled",true);
			})
		}
	</script>
</html>