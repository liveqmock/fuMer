function changeHeight(){
	var allTar=data_eara_frame_up.document.body.scrollHeight;
	document.getElementById("data_eara_frame_up").height=allTar;
	document.getElementById("data_eara_frame_down").height=document.body.clientHeight-allTar;
	document.getElementById("data_eara_frame_down").scrolling="auto";
}
window.onload=changeHeight;