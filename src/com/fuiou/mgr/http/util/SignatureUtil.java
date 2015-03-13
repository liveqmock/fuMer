package com.fuiou.mgr.http.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fuiou.mer.model.IvrContractReqBean;



/**
 * IVR卡密签约加密工具类
 * @author Jerry
 * 2014.11.17
 */
public class SignatureUtil {
	
	private static final Logger logger = Logger.getLogger(SignatureUtil.class);
	
	public static boolean validate(Object bean,String key){
		List<String> values = new ArrayList<String>();
		String signature = null;
		for(Method method:bean.getClass().getMethods()){
			try {
				if(!method.getName().startsWith("get") || "getClass".equalsIgnoreCase(method.getName()))
					continue ; 
				Object o = method.invoke(bean, null);
				if(o!=null && StringUtils.isNotEmpty(o.toString())){
					if("getSignature".equalsIgnoreCase(method.getName().toLowerCase())){
						signature = o.toString();
					}else{
						values.add(o.toString());
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		String localSignature = hex(values, key);
		return localSignature.equalsIgnoreCase(signature);
	}
	
	public static String hex(List<String> values,String key){
		String[] strs = new String[values.size()];
		for(int i=0;i<strs.length;i++){
			strs[i] = values.get(i);
		}
		Arrays.sort(strs);
		StringBuffer source = new StringBuffer();
		for(String str:strs){
			source.append(str).append("|");
		}
		String bigstr = source.substring(0,source.length()-1);
		logger.debug("bigstr:"+bigstr);
		System.out.println(bigstr);
		String result = DigestUtils.shaHex(DigestUtils.shaHex(bigstr)+"|"+key);
		logger.debug("bigstr hex result:"+result);
		return result;
	}

	
	public static void main(String[] args) {
		IvrContractReqBean bean = new IvrContractReqBean();
		bean.setReqInsCd("123");
//		bean.setAcntNo("a1");
		bean.setSignature("b9c6188854e9e877cfa7632dea7e94806919af3c");
		System.out.println(validate(bean, "123456"));
//		System.out.println(DigestUtils.shaHex("123"));
	}
}
