package com.fuiou.mgr.action.contract;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.util.CustmrBusiVerifyWoker;

/**
 * 批量验证客户协议库
 * @author Jerry
 */
public class BatVerifyCustmrBusiJob implements Job {
	
	private static final Logger logger = Logger.getLogger(BatVerifyCustmrBusiJob.class);
	
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		List<TCustmrBusi> custmrBusies = custmrBusiService.getInvalidCustmrBusi();//获取待验证的协议库记录
		logger.debug("return result total rows:"+custmrBusies.size());
		if(custmrBusies!=null&&custmrBusies.size()>0){
			for(TCustmrBusi custmrBusi:custmrBusies){
				SystemParams.executorService.submit(new CustmrBusiVerifyWoker(custmrBusi));
			}
		}else{
			logger.debug("0 rows need verify");
		}
	}
	
	
}
