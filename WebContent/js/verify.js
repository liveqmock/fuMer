/**
 * 验证手机号码
 * @param mobile
 */
function verifyMobile(mobile){
	var p = /^1\d{10}$/;
	if(p.test(mobile)){
		return true;
	}else{
		return false;
	}
}

/**
 * 验证email
 * @param email
 */
function verifyEmail(email){
	var p = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
	if(p.test(email)){
		return true;
	}else{
		return false;
	}
}

/**
 * 验证座机号码
 * @param phone
 */
function verifyPhoneNum(phone){
	var p =/^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/; 
	if(p.test(phone)){
		return true;
	}else{
		return false;
	}
}

