
function resizeAll() {
	resizeUp();
	resizeDown();
}

function resizeUp() {
	var upIframe;
	var upH;

	if (window==null) return ;
	if (window.parent==null) return ;
	if (window.parent.parent==null) return ;
	
	var struFrame = window.parent.parent.document.getElementById("struFrame");
	if (struFrame==null) return ;
	var allTar = struFrame.style.height.toLowerCase();
	if (allTar.indexOf("px")>=0) {
		var pos = allTar.indexOf("px");
		allTar = allTar.substring(0,pos);
	}
	
	var upIframe = window.parent.document.getElementById("data_eara_frame_up");
	if (upIframe==null) return ;

	if (window.navigator.appName == "Microsoft Internet Explorer") {
		upH = document.body.scrollHeight;
	} 
	if (window.navigator.appName == "Netscape") {
		upH = document.body.offsetHeight;
	}
	if (upH>allTar-40) {
		upH = allTar - 40;
		//alert("allTar="+allTar+",upH="+upH);
	}
	upIframe.style.height = upH + 'px';
}

function resizeDown() {
	if (window==null) return ;
	if (window.parent==null) return ;
	if (window.parent.document==null) return ;
	if (window.parent.parent==null) return ;
	
	var struFrame = window.parent.parent.document.getElementById("struFrame");
	if (struFrame==null) return ;
	
	var upIframe = window.parent.document.getElementById("data_eara_frame_up");
	if (upIframe==null) return ;
	
	var downIframe = window.parent.document.getElementById("data_eara_frame_down");
	if (downIframe==null) return ;
	
	var allTar = struFrame.style.height.toLowerCase();
	if (allTar.indexOf("px")>=0) {
		var pos = allTar.indexOf("px");
		allTar = allTar.substring(0,pos);
	}

	var upTar = upIframe.style.height.toLowerCase();
	if (upTar.indexOf("px")>=0) {
		var pos = upTar.indexOf("px");
		upTar = upTar.substring(0,pos);
	}
	
//	alert("allTar="+allTar+",upTar="+upTar+",allTar-upTar="+(allTar - upTar));
	if (allTar - upTar <= 0) 
		downIframe.style.height = '40px';
	else
		downIframe.style.height = (allTar - upTar)+'px';
}

