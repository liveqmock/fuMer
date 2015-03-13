// JavaScript Document
var frameheight = null;
var upH = null;

function height() {
    //document.getElementById("navmenu").style.height = document.body.clientHeight - 80;
    document.getElementById("data").style.height = document.body.clientHeight - 120;
    document.getElementById("data_eara").style.height = document.body.clientHeight - 160;
    frameheight = document.getElementById("data_eara").style.height;
    document.getElementById("struFrame").style.height = frameheight;
}
function frame() {
    //alert(upheight);
    /*var upTar=document.getElementById("data_eara_frame_up");
    var downTar=document.getElementById("data_eara_frame_down");
    var getFFVersion=navigator.userAgent.substring(navigator.userAgent.indexOf("Firefox")).split("/")[1];
    //extra height in px to add to iframe in FireFox 1.0+ browsers
    //�߶�
    var FFextraHeight=getFFVersion>=0.1? 16 : 0 ;


    if (upTar.contentDocument && upTar.contentDocument.body.offsetHeight){
    //ns6 syntax
    upTar.height = upTar.Document.body.scrollHeight;
    //alert(upTar.height);
    downTar.height =Document.body.clientHeight-upTar.height;

    }
    else if (upTar.Document && upTar.Document.body.scrollHeight){
    //ie5+ syntax
    upTar.height = upTar.document.body.clientHeight;
    downTar.height =parent.document.body.clientHeight-upTar.height-90;

    }*/
}
function upheight() {
    upH = document.body.scrollHeight;
    if (window.navigator.appName == "Microsoft Internet Explorer") {
        if(window.parent.document.getElementById("data_eara_frame_up")!=null){
        	window.parent.document.getElementById("data_eara_frame_up").height = upH;
        }
    }
    if (window.navigator.appName != "Microsoft Internet Explorer") {
        if(window.parent.document.getElementById("data_eara_frame_up")!=null){
        	window.parent.document.getElementById("data_eara_frame_up").height = upH;
        }
    }
}
function downheight() {
    var upTar = window.parent.document.getElementById("data_eara_frame_up").height;
    var allTar = window.parent.document.body.clientHeight;
    window.parent.document.getElementById("data_eara_frame_down").height = allTar - upTar;
}

function menu_over(overName)
{
    if (document.getElementById(overName).className != "current") {
        document.getElementById(overName).className = "menu_over";
    }
}
function menu_off(offName)
{
    if (document.getElementById(offName).className != "current") {
        document.getElementById(offName).className = "menu_off";
    }
}
function menu_down(downName)
{
    for (i = 1; i < 20; i++) {
        document.getElementById("menu" + i).className = "menu_off";
    }
    document.getElementById(downName).className = "current";
}

//��������

var selectedTr = null;

function doRowHint(tr) {
    if (selectedTr != null && selectedTr != tr) {
        selectedTr.className = selectedTr.className.replace(/\bselect_tr\b/g, '');
    }
    selectedTr = tr;
    if (selectedTr.className.indexOf(' select_tr') == -1) {
        selectedTr.className += ' select_tr';
    } else {
        selectedTr.className = selectedTr.className.replace(/\bselect_tr\b/g, '');
        selectedTr = null;
    }

}

function loadHint() {
    var tables = document.getElementsByTagName("TABLE");
    var rows = null;

    if (tables.length > 0) {
        if (tables[0].tBodies.length != 0) {
            rows = tables[0].tBodies[0].rows;
        } else {
            rows = tables[0].rows;
        }
    }
    if (rows != null) {
        for (var i = 0; i < rows.length; i++) {
            rows[i].onclick = function() {
                doRowHint(this);
            }
        }
    }
}

function TableHint(tableObj) {
    this.rows = tableObj.rows;
    this.tr = null;
}

TableHint.prototype.doHint = function(tr) {
    if (this.tr != null && this.tr != tr) {
        this.tr.className = this.tr.className.replace(/\bselect_tr\b/g, '');
    }
    this.tr = tr;
    if (this.tr.className.indexOf(' select_tr') == -1) {
        this.tr.className += ' select_tr';
    } else {
        this.tr.className = this.tr.className.replace(/\bselect_tr\b/g, '');
        this.tr = null;
    }

}