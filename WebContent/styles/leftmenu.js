// JavaScript Document
/*var heightID = null;
function subclose(){
var obj = eval(subdiv2.style.height.replace("px", ""));
if (obj>0){
subdiv2.style.height=obj-10;
heightID=setTimeout("subclose()", 1);
}
else{
subdiv2.style.display="none";
clearTimeout(heightID);
if (window.navigator.appName!="Microsoft Internet Explorer"){
subdiv2.style.height=0;
}
}
}

var intTimeStep=20; 
var isIe=(window.ActiveXObject)?true:false; 
var intAlphaStep=(isIe)?5:0.05; 
var curSObj=null; 
var curOpacity=null; 
var subheight;
function startObjVisible(objId) 
{ 

 //subheight=document.getElementById(objId).style.height;
 curSObj=document.getElementById(objId); 
 setObjState(); 
} 
function setObjState(evTarget) 
{ 
 if (curSObj.style.display==""){curOpacity=1;setObjClose();} 
 else{ 
 if(isIe) 
 { 
 //curSObj.style.cssText='display:none;Z-index:99; FILTER: alpha(opacity=0);position:relative;'; 
 curSObj.style.cssText='display:none;Z-index:99; FILTER: alpha(opacity=0);position:absolute;'; 
 curSObj.filters.alpha.opacity=0; 


 }else 
 { 
 curSObj.style.opacity=0 
 } 
 curSObj.style.display=''; 
 
 curOpacity=0; 
 setObjOpen();
 } 
} 
 
function setObjOpen() 
{ 
 if(isIe) 
 { 
 curSObj.filters.alpha.opacity+=intAlphaStep; 
 if (curSObj.filters.alpha.opacity<100) setTimeout('setObjOpen()',intTimeStep); 
 }else{ 
 curOpacity+=intAlphaStep; 
 curSObj.style.opacity =curOpacity; 
 if (curOpacity<1) setTimeout('setObjOpen()',intTimeStep); 
 } 
} 
 
function setObjClose() 
{ 
 if(isIe) 
 { 
 curSObj.filters.alpha.opacity-=intAlphaStep; 
 if (curSObj.filters.alpha.opacity>0) { 
 setTimeout('setObjClose()',intTimeStep);} 
 else {curSObj.style.display="none";
 subclose();
 } 
 }else{ 
 curOpacity-=intAlphaStep; 
 if (curOpacity>0) { 
 curSObj.style.opacity =curOpacity; 
 setTimeout('setObjClose()',intTimeStep);} 
 else {
 curSObj.style.display="none";
 subclose();
 } 
 }
 
} 


//function navHover(ID){
	//for(i=0;i<20;i++){
	//	document.getElementById(ID).style.display="block";
	//	startObjVisible(ID);
	//	}
//}
//if (window.attachEvent) window.attachEvent("onload", navHover);
//function submenu(subID,num){

//var heightID = null;
function subopen(divID){
element=document.getElementById(divID);
var obj = eval(element.style.height.replace("px", ""));
if (obj<72){
element.style.height=obj+15;
heightID=setTimeout("subopen(divID)", 1);
}
else{
element.style.height=72;
clearTimeout(heightID);
startObjVisible("subul2");
if (window.navigator.appName!="Microsoft Internet Explorer"){
subdiv2.style.height=0;
}
}
}*/
function submenu(size, ulID){
	for(i=0;i<size;i++){
		if(ulID != "sub"+i)
			document.getElementById("sub"+i).className="none";
	}
	element= document.getElementById(ulID);
	element.className = (element.className.toLowerCase() == "current"?"none":"current");
}

