// Set focus & select input text
function doSelect(fld) {
	fld.focus();
	fld.select();
}

// Check is empty or null
function isEmpty(str) {
	if(str == null || trim(str) == "") return true;
	return false;
}

// Check is a positive intger
function isPosInteger(val) {
	if(val == null) return false;
	var str = val.toString();
	for(var i=0; i < str.length;i++) {
		var ch = str.charAt(i);
		if(ch < '0' || ch > '9') return false;
	}
	return true;
}

// Check is an intger
function isInteger(val) {
	if(val == null) return false;
	var str = val.toString();
	for(var i=0; i < str.length;i++) {
		var ch = str.charAt(i);
		if(i == 0 && ch == '-') continue;
		if(ch < '0' || ch > '9') return false;
	}
	return true;
}

// Check is a number(include '-', '.')
function isNumber(val) {
	if(val == null) return false;
	var oneDecimal = false;
	var str = val.toString();
	for(var i=0; i < str.length;i++) {
		var ch = str.charAt(i);
		if(i == 0 && ch == '-') continue;
		if(ch == '.' && !oneDecimal) {
			oneDecimal = true;
			continue;
		}
		if(ch < '0' || ch > '9') return false;
	}
	return true;
}

function chkemail(a) 
{ 
	var i=a.length;
	var temp = a.indexOf('@');
	var tempd = a.indexOf('.');
	if (temp > 1) {
	  if ((i-temp) > 3){
	    if ((i-tempd)>1&&tempd!=-1){
	     return true;
	    }   
      }
	}
	return false;
}
function checkTel(val){
	var str = val.toString();
	for(var i=0; i < str.length;i++) {
		var ch = str.charAt(i);
		if((ch < '0' || ch > '9')&& ch !='-') return false;
	}
	return true;
}
// Check is CheckBox checked
function isCheckBox(chkForm, onlyOne) {
	var chkList = getCheckBox(chkForm);
	var chkCount = chkList.length;
	if(chkCount < 1) {
		alert("请选择需要处理的记录!");
		return false;
	}
	// Only one check
	if(onlyOne && chkCount > 1) {
		alert("选择的记录只能是一条!");
		return false;
	}
	return true;
}
// Get the checked CheckBox indexs
function getCheckBox(chkForm) {
	var chkList = new Array();
	var chkCount = 0;
	// parse each form's fields
	var row = 0;
	for(var i=0; i < chkForm.length; i++) {
		// check if type is CheckBox
		if(chkForm.elements[i].type =='checkbox') {
			// check if checked
			if(chkForm.elements[i].checked) {
				chkList[chkCount] = row;
				chkCount++;
			}
			row++;
		}
	}
	return chkList;
}

// 如果是0，选择所有的checkbox。否则所有的都不选
function doAllChoose(chkForm,chkflag){
	// parse each form's fields
	for(var i=0; i < chkForm.length; i++) {
		// check if type is CheckBox
		if(chkForm.elements[i].type =='checkbox') {
			if(chkflag==0)
				chkForm.elements[i].checked =true;
			else
				chkForm.elements[i].checked =false;
		}
	}
}
function checkIP(sIPAddress) {
	var exp=/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
	var reg = sIPAddress.match(exp);
	if(reg==null) {
        return false;
	} else {
        return true;
	}
}

function checkpageSize(o, u) {
	var pageSize = o
	if (pageSize.value == "") {
		alert("请输入每页的条数!");
		pageSize.focus();
		return;
	}
	if (pageSize.value < 1) {
		alert("对不起,每页的条数不能小于1!");
		pageSize.focus();
		return 
	}
    if(isNaN(pageSize.value)){
        alert("对不起,每页的条数只能为数字!");
        pageSize.focus();
        return ;
    }
    document.getElementById("hidurl").value=u+pageSize.value;
    location.replace(u+pageSize.value);
}
function checkPageCount(o,url){
    var pagecount=o;
    if(pagecount.value==""){
         alert("请输入要跳转的页面的数量!");
         pagecount.focus();
         return;
    }
    if(isNaN(pagecount.value)){
         alert("对不起,要跳转的数量只能为数字!");
         pagecount.focus();
         return ;
    }
    if(pagecount<1){
    	alert("对不起,要跳转的数量不能小于1!");
        pagecount.focus();
        return ;
    }
    location.replace(url+pagecount.value);
}
