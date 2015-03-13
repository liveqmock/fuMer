	var start = 0;
	
	function limitTime(limit) {
		var now = Date.parse(new Date())/1000;
		if(start==0){
			start = now;
			return true;
		}else if((now - start)<limit){
			return false;
		}else{
			start = now;
			return true;
		}
	}
	