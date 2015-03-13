var qswhU2GB=[];
var qswhSpell=[];
function UrlEncode(str){
	var i,c,ret="",strSpecial="!\"#$%&'()*+,/:;<=>?@[\]^`{|}~%";
	for(i=0;i<str.length;i++){
		if(str.charCodeAt(i)>=0x4e00){
			c=qswhU2GB[str.charCodeAt(i)-0x4e00];
			ret+="%"+c.slice(0,2)+"%"+c.slice(-2);
		}
		else{
			c=str.charAt(i);
			if(c==" ")
				ret+="+";
			else if(strSpecial.indexOf(c)!=-1)
				ret+="%"+str.charCodeAt(i).toString(16);
			else
				ret+=c;
		}
	}
	return ret;
}

function getSpell(str,sp){
	var i,c,t,ret="";
	if(sp==null)sp="";
	for(i=0;i<str.length;i++){
		if(str.charCodeAt(i)>=0x4e00){
			c=parseInt(qswhU2GB[str.charCodeAt(i)-0x4e00],16);
			if(c<55290){
				for(t=qswhSpell.length-1;t>0;t=t-2)if(qswhSpell[t]<=c)break;
				if(t>0)ret+=qswhSpell[t-1]+sp;
			}
		}
	}
	return ret.substr(0,ret.length-sp.length);
}

// 页面需要调用的方法
function merSearch(jspUrl,idName) {
    pageStr = jspUrl+"?formName=";
    pageStr += document.forms[0].name;
    pageStr += "&idName=";
    pageStr += idName;
    searchWindow = window.open(pageStr,null,"height=250, width=400,scrollbars=yes,toolbar=no,menubar=no,location=no");
}
// 页面需要调用的方法,增加了formName。
function merSearchForm(jspUrl,idName,formName) {
    pageStr = jspUrl+"?formName=";
    pageStr += formName;
    pageStr += "&idName=";
    pageStr += idName;
    searchWindow = window.open(pageStr,null,"height=250, width=400,scrollbars=yes,toolbar=no,menubar=no,location=no");
}

// 页面需要调用的方法
function merSearchCallBack(jspUrl,idName,callBackFunc) {
    pageStr = jspUrl+"?formName=";
    pageStr += document.forms[0].name;
    pageStr += "&idName=";
    pageStr += idName;
    pageStr += "&callBack=";
    pageStr += callBackFunc;
    
    searchWindow = window.open(pageStr,null,"height=250, width=400,scrollbars=yes,toolbar=no,menubar=no,location=no");
}