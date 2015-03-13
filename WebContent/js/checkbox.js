// 全部选择
function selectAll() {
    var cpOrdId;
    var selSet = document.forms[0].selectedStr_h.value;
    
	for(var i=0; i < document.forms[0].length; i++) {
		// check if type is CheckBox
		if(document.forms[0].elements[i].type =='checkbox') {
			document.forms[0].elements[i].checked =true;
			// 若集合中已存在此订单号，则从集合中删除此订单号
			cpOrdId = document.forms[0].elements[i].value+ ";";
			selSet = selSet.replace(cpOrdId, "");
	        // 再将此订单号加入集合
    	    selSet = selSet + cpOrdId;
		}
	}
    document.forms[0].selectedStr_h.value = selSet;
}

// 取消全部
function deselectAll() {
    var ElementName = "chkBox_";
    var chkName;
    var cpOrdId;
    var selSet = document.forms[0].selectedStr_h.value;    
	
	for(var i=0; i < document.forms[0].length; i++) {
		// check if type is CheckBox
		if(document.forms[0].elements[i].type =='checkbox') {
			document.forms[0].elements[i].checked =false;
			// 若集合中已存在此订单号，则从集合中删除此订单号
			cpOrdId = document.forms[0].elements[i].value+ ";";
			selSet = selSet.replace(cpOrdId, "");
		}
	}
	
    document.forms[0].selectedStr_h.value = selSet;
}

// 单击复选诓
function setChecked(ElementName) {
    var cpOrdId;
    var selSet;

    cpOrdId = eval("document.forms[0]." + ElementName + ".value") + ";";
    selSet = document.forms[0].selectedStr_h.value;
    // 若集合中已存在此订单号，则从集合中删除此订单号
    selSet = selSet.replace(cpOrdId, "");
    // 若选中则再将此订单号加入集合
    if(eval("document.forms[0]." + ElementName + ".checked")) {
        selSet = selSet + cpOrdId;
    }

    document.forms[0].selectedStr_h.value = selSet;
}

// 判断是否没有选定任何一个复选框
function isNonChecked() {
    if(document.forms[0].selectedStr_h.value.length==0) {
        return true;
    }else{
        return false;
    }
}

// Check is CheckBox checked
function isCheckBox(chkForm, onlyOne) {
	var chkList = getCheckBox(chkForm);
	var chkCount = chkList.length;
	if(chkCount < 1) {
		alert("您没有选择任何记录!");
		return false;
	}
	// Only one check
	if(onlyOne && chkCount > 1) {
		alert("请选择一条记录!");
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
