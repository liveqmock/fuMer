package com.fuiou.mgr.util;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.util.SystemParams;

/**
 * 针对http对接商户线程数量的控制工具类
 * @author Jerry
 */
public class HttpThreadConfigUtil {
	
	private static Logger logger = LoggerFactory.getLogger(HttpThreadConfigUtil.class);

	public static Map<String,AtomicInteger> lockMap = new ConcurrentHashMap<String,AtomicInteger>();
	public static Map<String,Long> queryMap = new ConcurrentHashMap<String,Long>();
	
	public static Map<String, AtomicInteger> getLockMap() {
		return lockMap;
	}

	public static boolean isAcceptRequest(String mchntCd){
		Integer nums = SystemParams.httpThreadsCfgMap.get(mchntCd);
		boolean f = false;
		if(null==nums||nums==0){
			//在缓存里面找不到则去数据库找
			TDataDictService dataDictService = new TDataDictService();
			String value = dataDictService.selectTDataDictByClassAndKey("HTTP_THREAD_CFG", mchntCd);
			try{
				if(StringUtil.isNotEmpty(value)){
					nums = Integer.valueOf(value.trim());
					SystemParams.httpThreadsCfgMap.put(mchntCd, nums);
					f= true;
				}
			}catch (Exception e) {
				logger.debug("=========  http thread numbers config error ,not support this Merchant ,mchntCd="+mchntCd+"=============");
			}
		}else{
			f = true;
		}
		if(!f){
			logger.debug("=========  http service not support this Merchant ,mchntCd="+mchntCd+"=============");
			return f;
		}
		AtomicInteger atomicInteger = lockMap.get(mchntCd);
		if(null == atomicInteger){
			logger.debug("============ first accept "+mchntCd+" http request=======");
			lockMap.put(mchntCd, new AtomicInteger(1));
			return true;
		}else{
			int i = nums - atomicInteger.get();
			logger.debug("==========="+mchntCd+"=== current thread numbers is "+atomicInteger.get()+"=======");
			if(i>0){
				int j = atomicInteger.incrementAndGet();
				logger.debug("==========="+mchntCd+"=== current thread numbers is "+j+"=======");
				return true;
			}
		}
		logger.debug("==========="+mchntCd+"=== current thread numbers too many,please request after =======");
		return false;
	}
	
	public static void closeConnect(String mchntCd){
		Integer nums = SystemParams.httpThreadsCfgMap.get(mchntCd);
		if(null==nums||nums==0){
			logger.debug("=========  http service not support this Merchant ,mchntCd="+mchntCd+"=============");
			return;
		}
		AtomicInteger atomicInteger = lockMap.get(mchntCd);
		int j = atomicInteger.get();
		if(j>0)
		 j = atomicInteger.decrementAndGet();
		logger.debug("=========="+mchntCd+"= closeConnect ,current active thread numbers is "+j+"=========");
	}
	
	public static boolean checkQueryFrequency(String mchntCd){
		if(null==queryMap.get(mchntCd)){
			logger.debug(" ============first accept "+mchntCd+" query  request========");
			queryMap.put(mchntCd, new Date().getTime());
			return true;
		}else{
			Long lastQueryTs = queryMap.get(mchntCd);
			logger.debug("======mchnt_cd="+mchntCd+" last query request is "+lastQueryTs.toString()+" ========");
			String queryFrequency = SystemParams.getProperty("queryFrequency");
			Long now = new Date().getTime() ;
			if(now - lastQueryTs > Integer.valueOf(queryFrequency)*60*1000){
				queryMap.put(mchntCd, now);
				return true;
			}
		}
		return false;
	}
	
	
}



