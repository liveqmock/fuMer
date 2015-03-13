<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.fuiou.mer.util.TDataDictConst"%>
<%@ include file="/common.jsp" %>
<%@ page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>富友商户系统|单笔贷记业务</title>
<STYLE type="text/css">

.select {
    width:360px; 
    height:19px; 
    border:#7f9db9 1px solid;
    margin-right:5px;
}
</STYLE>
<%
    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    String dateTimeStr = sdf.format(dt);
%>
<script language="javascript" src="<%=root%>/styles/iframe.js?<%=dateTimeStr%>" type="text/javascript"></script>
<link href="<%=root%>/styles/stylediv.css" rel="stylesheet" type="text/css" />
<link href="<%=root%>/styles/jquery.autocomplete.css" rel="stylesheet" type="text/css" />

<script language="javascript" src="<%=root%>/js/SearchMerchant.js"></script>
<script language="javascript" src="<%=root%>/js/jquery.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/jquery.autocomplete.js"></script>
<script language="javascript" src="<%=root%>/js/payForCheckOut.js" type="text/javascript"></script>
<script language="javascript" src="<%=root%>/js/verify.js" type="text/javascript"></script>

<script type="text/javascript">
//获取联系人的表格
function getUserView(strUrl){
	var divView=document.getElementById("userViewDiv");
	divView.innerHTML="";
	$.ajax({
		type :"post",
        url :"<%=root%>/userView.action"+strUrl,
        dataType :"json",
        success :function (data){
        	divView.innerHTML=data;
        	divView.style.display="block";
        	document.getElementById("condition").style.display="none";
           }
	});
}
//查询常用联系人
function query(){
	var userName=document.getElementById("user.USER_NAME").value;
	var acctNo=document.getElementById("user.ACCT_NO").value;
	var mobileNo=document.getElementById("user.MOBILE_NO").value;
	var url="?user.USER_NAME="+encodeURIComponent(encodeURIComponent(userName))+"&user.ACCT_NO="+encodeURIComponent(encodeURIComponent(acctNo))
	+"&user.MOBILE_NO="+encodeURIComponent(encodeURIComponent(mobileNo));
	getUserView(url);
}
//设置行的特色效果
function onfcusColor(o,i){
	var indexNum=document.getElementById("indexNum");
	var listSize=document.getElementById("listSize").value;
	if("#3370ac"==o.style.background){
		o.style.background="#ffffcc";
		indexNum.value="";
	}else{
		indexNum.value=i;
		o.style.background="#3370ac";
		for(var j=0;j<listSize;j++){
			if(i!=j){
				document.getElementById("utr"+j).style.background="#ffffcc";
			}
		}
	}
}
//查询常用联系人
function seletedUser(){
	var index=document.getElementById("indexNum").value;
	if(""==index){
		alert("请双击要选中的行");
		return;
	}
	var userName=document.getElementById("user"+index).innerHTML;
	var prov=document.getElementById("prov"+index).value;
	var city=document.getElementById("city"+index).value;
	var bank=document.getElementById("bank"+index).value;
	var acctNo=document.getElementById("acctNo"+index).innerHTML;
	var mobileNo=document.getElementById("mobileNo"+index).innerHTML;
	var pmsBank=document.getElementById("pmsBank"+index).value;
	var provs=document.getElementById("selectProv_nm");
	for(var a=0;a<provs.length;a++){
		if(provs[a].value==prov){
			provs[a].selected=true;
			break;
		}
	}
	provOnchange(city);
	cityOnchange(bank);
	document.getElementById("bankNam_nm").value=pmsBank;
    var vas = pmsBank.split(" ");
    if(vas.length === 2){
        $("#bankNo_nm").val(vas[1]);
        $("#bankName_er").text("");
        $("#bankNo_er").text("");
    }
	document.getElementById("userName_nm").value=userName;
	document.getElementById("cardNo_nm").value=acctNo;
	document.getElementById("phoneNum_nm").value=mobileNo;
}
//添加常用联系人
function createUser(){
	var selectProv_nm=document.getElementById("selectProv_nm");
	var userName_nm=document.getElementById("userName_nm");
	var cardNo_nm=document.getElementById("cardNo_nm");
	var phoneNum_nm=document.getElementById("phoneNum_nm");
	var selectCity_nm=document.getElementById("selectCity_nm");
	var selectBank_nm=document.getElementById("selectBank_nm");

	var bankNam_nm=document.getElementById("bankNam_nm");
	var s=/^\d+$/;
	if("0"==selectProv_nm.value){
		alert("请选择开户省");
		selectProv_nm.focus();
		return;
	}
	if("0"==selectCity_nm.value){
		alert("请选择开户市");
		selectCity_nm.focus();
		return;
	}
	if("0"==selectBank_nm.value){
		alert("请选择开户银行");
		selectBank_nm.focus();
		return;
	}
	if(""==bankNam_nm.value){
		alert("请填写开户行名称");
		bankNam_nm.focus();
		return;
	}
	if(""==userName_nm.value){
		alert("请填写户名");
		userName_nm.focus();
		return;
	}
	if(""==cardNo_nm.value){
		alert("请填写收款人银行账号");
		cardNo_nm.focus();
		return;
	}
	if(!s.test(cardNo_nm.value)){
		alert("收款人银行账号输入有误");
		cardNo_nm.focus();
		return;
	}
	var le=cardNo_nm.value.length;
	if(le<8||le>30){
		alert("收款人银行账号必须在8-30位之间");
		cardNo_nm.focus();
		return;
	}
	if(phoneNum_nm.value!=""){
		if(!verifyMobile(phoneNum_nm.value)){
			alert("您输入的手机号码有误");
			phoneNum_nm.focus();
			return;
		}
	}
	var strUrl="?user.USER_NAME="+encodeURIComponent(encodeURIComponent(userName_nm.value))+"&user.ACCT_NO="+cardNo_nm.value+"&user.MOBILE_NO="+phoneNum_nm.value+
	"&user.PROV_CODE="+encodeURIComponent(encodeURIComponent(selectProv_nm.value))+"&user.CITY_CODE="+encodeURIComponent(encodeURIComponent(selectCity_nm.value))+"&user.BANK_CD="+
	encodeURIComponent(encodeURIComponent(selectBank_nm.value))+"&user.PMS_BANK_NAME="+encodeURIComponent(encodeURIComponent(bankNam_nm.value));
	$.ajax({
		type :"post",
        url :"<%=root%>/createUser.action"+strUrl,
        dataType :"json",
        success :function (data){
        	if(data=="0"){
				alert("添加成功");
            }else if(data=="1"){
            	alert("添加失败");
            }else if(data=="2"){
            	alert("该联系人已存在");
            }
           }
	});
}

function updateUser(){
	var index=document.getElementById("indexNum").value;
	if(""==index){
		alert("请双击要编辑的行");
		return;
	}
	var rowId=document.getElementById("indexId"+index).value;
	open("<%=root%>/userInfoMgr_eidtUser.action?user.ROW_ID="+rowId);
}
function deleteUser(url){
	var index=document.getElementById("indexNum").value;
	if(""==index){
		alert("请双击要删除的行");
		return;
	}
	var rowId=document.getElementById("indexId"+index).value;
	if(confirm("确定要删除吗?")){
		$.ajax({
			type :"post",
	        url :"<%=root%>/userInfoMgr_deleteUser.action?user.ROW_ID="+rowId+"&ajaxDelete=deleteUser",
	        dataType :"json",
	        success :function (data){
	        	if(data=="0"){
					alert("删除成功");
	            }else if(data=="1"){
	            	alert("删除失败");
	            }
	        }
		});
		getUserView(url);
	}
}
function getPageNum(url){
	var pagecount=document.getElementById("taozhuang")
	if(pagecount.value==""){
        alert("请输入要跳转的页面的数量!");
        return;
   }
   if(isNaN(pagecount.value)){
        alert("对不起,要跳转的数量只能为数字!");
        return ;
   }
   if(pagecount<1){
   	   alert("对不起,要跳转的数量不能小于1!");
       return ;
   }
   var pageNum=document.getElementById("taozhuang").value;
   var strUrl="?pageNum="+pageNum+url;
   getUserView(strUrl)
	
}

//页面加载省
function ajaxOnload(){
    $.ajax({
        type :"post",
        url :"<%=root%>/payfor/jsonPayfor_returnProvList.action",
        dataType :"json",
        success :function (data){
             var shi = document.getElementById("selectProv_nm");
             document.getElementById("selectProv_nm").options.add(new Option("0","--请选择--"));
             shi.length = 1;
              for(var x=0;x<data.length;x++){
               document.getElementById("selectProv_nm").options.add(new Option(data[x].PROV_NM,data[x].PROV_CD));
              }
           }
    });
    displayTable();
}

//省Onchange 填充市
function provOnchange(city){
    //选择的省ID
    var provId= document.getElementById("selectProv_nm").value;
    var count = 0;
    $.ajax({
        type :"post",
        url :"<%=root%>/payfor/jsonPayfor_returnCityList.action",
        data :"provId=" + provId,
        dataType :"json",
        success :function (data){
            count = data.length;
             document.getElementById("selectCity_nm").length = 0;
             document.getElementById("selectCity_nm").options.add(new Option("--请选择--","0"));
              for(var x=0;x<data.length;x++){
            	  var option=new Option(data[x].CITY_NM,data[x].CITY_CD);
               	  document.getElementById("selectCity_nm").options.add(option);
                  if(city==data[x].CITY_CD){
                	  option.selected=true;
                  }
              }
            }
        });
     if(provId ==0 || count ==0){
          document.getElementById("selectCity_nm").length=0;
          document.getElementById("selectBank_nm").length=0;
          document.getElementById("selectCity_nm").options.add(new Option("--请选择--","0"));
          document.getElementById("selectBank_nm").options.add(new Option("--请选择--","0"));
      }
        
}
//市Onchange填充银行
function cityOnchange(bank) {
    var cityId = document.getElementById("selectCity_nm").value;
    $.ajax({ 
        type : "post", 
        url : "<%=root%>/payfor/jsonPayfor_returnBankList.action",
        dataType : "json",
        success : function(data) {
             document.getElementById("selectBank_nm").length = 0;
             document.getElementById("selectBank_nm").options.add(new Option("--请选择--","0"));
            for ( var x = 0; x < data.length; x++) {
                var option=new Option(data[x].BANK_NM, data[x].BANK_CD);
                document.getElementById("selectBank_nm").options.add(option);
                if(bank==data[x].BANK_CD){
                	option.selected=true;
                }
            }
        }
    });
}

    //获取支行信息
    function getPms(){
        var city =  document.getElementById("selectCity_nm").value;
        var bank = document.getElementById("selectBank_nm").value;
        
        document.getElementById("bankNam_nm").value="";//清空开户行名称
        document.getElementById("bankNo_nm").value="";//清空开户行行号
        $("#bankNam_nm").unautocomplete();
        $.post("<%=root%>/payfor/jsonPayfor_returnSubBank.action",{'city':city,'bank':bank},function(data,textStatus){

           
         
           var tmp = data.split(',');
            $("#bankNam_nm").autocomplete(tmp, {
                matchContains: true,
                minChars: 0,
                max: 500,
                width: 430
                //scrollHeight:400
            });
        });
    }
    
    //重置
    function getValue(){
     var va  = $("#bankNam_nm").val();
     var vas = va.split(" ");
     if(vas.length === 2){
         $("#bankNo_nm").val(vas[1]);
         $("#bankName_er").text("");
         $("#bankNo_er").text("");
     }
    }
    //选取的id,显示错误信息的id,错误描述
    checkout = function(id,id_me,me){
        var id_value= $("#"+id).val();
        if(0 == id_value || id_value == undefined){
            $("#"+id_me).text(me);
            return false;
        }else{
            $("#"+id_me).text("");
        }
        return true;
    }
    //选取的id,显示错误信息的id,正则
    function checkOutRormula(id,id_me,me,exp){
        
        var id_value= $("#"+id).val();
        if(!exp.test(id_value)){
            $("#"+id_me).text(me);
            return false;
        }else{
            $("#"+id_me).text("");
        }
        return true;
    }
    //银行帐号
    function bankNameRormula(){
        var formula = /^[0-9]{8,30}?$/;
        var result;
        var bl = false;
        if(!formula.test($("#cardNo_nm").val())){
            $("#cardNo_er").text("请填写正确收款人银行帐号!");
            return bl;
        }
            return true;
    }
    //户名
    function userNameRormula(){
        var formula = /(^[()（）a-zA-Zａ-ｚＡ-Ｚ0-9０-９\u4e00-\u9fa5]+$)/;
        var resultUserName = true;
        if(!checkout("userName_nm","userName_er","请填写户名!")){
            resultUserName = false;
        }
        return resultUserName;
    }
    //金额
    function amountRormula(){
        
        var formula = /^(0\.[1-9]\d?|0\.[0][1-9]|[1-9]\d{0,13}(\.\d{1,2})?)$/;
        if(!checkout("amount_nm","amount_er","请填写金额！")){
            return false;
        }
         if(!checkOutRormula("amount_nm","amount_er","请填写正确金额!",formula)){
            return false ;
        }
         var resultAmount = false;
            $.ajax({ 
                type : "post", 
                async:false,
                url : "<%=root%>/payfor/jsonPayfor_returnAmount.action",
                data :"amount=" + $("#amount_nm").val(),
                dataType : "json",
                success : function(amountData) {
                    if(false == amountData){
                        $("#amount_er").text("金额超限!");
                    }else{
                        $("#amount_er").text("");
                        resultAmount = true;
                    }
                }
            }); 
        return resultAmount;
    }
    //企业流水
    function enSerialNumRormula(){
        if("" != $("#enSerialNum_nm").val() && $("#enSerialNum_er").val() != undefined){
            var formula = /^[a-zA-Z0-9]*$/;
            return checkOutRormula("enSerialNum_nm","enSerialNum_er","请填写正确企业流水号!",formula);
        }else{
        	$("#enSerialNum_er").text("");
            return true;
        }
    }
    
    //手机号
    function phoneNumRormula(){
        if("" != $("#phoneNum_nm").val() && $("#phoneNum_nm").val() != undefined){
            var formula = /^1(\d{10})$/;
            return checkOutRormula("phoneNum_nm","phoneNum_er","请填写正确手机号!",formula);
        }else{
        	$("#phoneNum_er").text("");
            return true;
        }
    }
    //重置
    function btnReset(){
        $("input[id$='_nm']").val("");
        $("font[id$='_er']").text("");
        $("select[id$='_nm']").get(0).selectedIndex=0;
        $("#selectCity_nm").empty();
        $("#selectBank_nm").empty();
        $("#selectCity_nm").prepend("<option value='0'>--请选择--</option>");
        $("#selectBank_nm").prepend("<option value='0'>--请选择--</option>");
    }
     function btnSub(){
         if(!checkout("selectProv_nm","selectProv_er","请选择省!")){
                return  false;
            }
            if(!checkout("selectCity_nm","selectCity_er","请选择市!")){
                return  false;
            }
            if(!checkout("selectBank_nm","selectBank_er","请选择银行!")){
                return  false;
            }
            if(!checkout("bankNam_nm","bankName_er","请填写开户行名称!")){
                return  false;
            }
            if(!checkout("bankNo_nm","bankNo_er","请填写正确开户行名称！")){
                return  false;
            }
            if(!bankNameRormula()){
				return false;
            }
            if(!userNameRormula()){//户名
                return  false;
            }
            if(!amountRormula()){//金额
				return false;
            }
            if(!enSerialNumRormula()){//企业流水帐号
                return  false;
            }
            if(!phoneNumRormula()){//手机号
                return  false;
            }
            document.daishou.btnsubId.disabled=true;
            document.daishou.reset.disabled=true;
    }
     function displayTable(){
    		document.getElementById("userViewDiv").style.display="block";
    		document.getElementById("condition").style.display="none";
    		document.getElementById("userViewDiv").style.display="none";
    		document.getElementById("condition").style.display="block";
    }
</script>
</head>
<body onload="resizeAll();ajaxOnload();" style="background-color:#e8eef7; padding:0px; margin:0px;">
<div id="condition" style="display: block;">
<form name="daishou" id="daishou" action="<%=root%>/payfor/payForOnce_executepayForOnceReq.action" method="post" target = "struFrame"  onsubmit="return btnSub()">
<input type="hidden" name="interBankNo">
<table>
<tr>
    <td align="right">
        <s:text  name="开户省:" />
    </td>
    <td>
        <select style="width:204px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" id="selectProv_nm" name="selectProv_nm" onchange="provOnchange()" >
            <option value="0">--请选择--</option>
        </select><font color="red">*</font><font id="selectProv_er"></font> 
   </td>
</tr>
<tr>
    <td align="right">
        <s:text  name="开户市:" />
    </td>
    <td>
        <select style="width:204px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  id="selectCity_nm" name="selectCity_nm" onchange="cityOnchange()">
        <option value="0">--请选择--</option>
        </select><font color="red">*</font><font id="selectCity_er"></font>
   </td>
</tr>
<tr>
    <td align="right">
        <s:text  name="开户银行:" />
    </td>
    <td>
        <select style="width:204px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  id="selectBank_nm" name="selectBank_nm" onchange="getPms()"> 
            <option value="0">--请选择--</option>
        </select><font color="red">*</font><font id="selectBank_er"></font>
   </td>
</tr>
<tr>
    <td align="right">
        <s:text  name="开户行名称:" />
    </td>
    <td>
        <input type="text" id="bankNam_nm" name="bankNam_nm" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" onblur="getValue();">
        <font color="red">*</font><font id="bankName_er"></font>
   </td>
</tr>
<tr>
    <td align="right">
        <s:text  name="开户行行号:" />
    </td>
    <td>
        <input type="text"  name="bankNo_nm" id="bankNo_nm" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;"  disabled/><font color="red">*</font><font id="bankNo_er"></font>
   </td>
</tr>
<tr>
    <td align="right">
        <s:text name="收款人银行账号:" />
    </td>
    <td>
        <input type="text" id="cardNo_nm" name="accessBean.bankAccount" onblur="bankNameRormula()"  style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="28">
        <font color="red">*</font><font id="cardNo_er" ></font>
    </td>
</tr>
<tr>
            <td align="right"><s:text name="户名:" /></td>
            <td>
                <input type="text"  name="accessBean.accountName" id="userName_nm" onblur="userNameRormula()" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" />
                <font color="red">*</font><font id="userName_er"></font>
            </td>
        </tr>
        <tr>
            <td align="right">
                <s:text name="金额(单位:元):" />
            </td>
            <td>
               <input type="text" name="accessBean.amount" id="amount_nm" onblur="amountRormula()" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="10"  />
               <font color="red">*</font><font id="amount_er"></font>
            </td>
        </tr>
        <tr>
            <td align="right">
                <s:text name="企业流水账号:" />
            </td>
            <td>
                <input type="text"  name="accessBean.enpSeriaNo" id="enSerialNum_nm" onblur="enSerialNumRormula()" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="80" />
                <font id="enSerialNum_er"></font>
            </td>
        </tr>
        <tr>
            <td align="right">
                <s:text name="备注:" />
            </td>
            <td>
                <input type="text"  name="accessBean.memo" id="remark_nm" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="30" />
                <font id="remark_er"></font>
            </td>
        </tr>
        <tr>
            <td align="right">
                <s:text name="手机号:" />
            </td>
            <td>
               <input type="text" name="accessBean.mobile" id="phoneNum_nm" onblur="phoneNumRormula()" style="width:200px; height:19px; border:#7f9db9 1px solid;margin-right:5px;" maxlength="11" />
               <font id="phoneNum_er"></font>
            </td>
        </tr> 
        <tr>
		<td></td>
  		<td align="left">
  			<input type="submit" id="btnsubId" name="btnsubId" style="width: 100px;" class="type_button" value="确认">
            <input type="button" style="width: 100px;" class="type_button" name="reset" value="重置"  onclick="btnReset()">
  		</td>
  		</tr>
  		<tr>
  		<td></td>
  		<td align="left">
  			<input type="button" style="width: 100px;" value="加入联系人" class="type_button" onclick="createUser()"/>
  			<input type="button" style="width: 100px;" value="选择联系人" class="type_button" onclick="getUserView('?pageNum=1')"/>
  		</td>
		</tr>
</table>
<s:hidden name="accessBean.busiCd" id="busiCd" value="AP01"></s:hidden>
</form>
</div>
<div style="height: 110px;width: 800">
<div id="userViewDiv" class="table"></div>
</div>
</body>
</html>