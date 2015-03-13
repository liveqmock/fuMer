package com.fuiou.mgr.directReturnPayResult.payfor;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 返回商户回盘文件入口
 * yangliehui
 *
 */
public class DoPayForReturnPayResult implements Job{
	/**
	 * 返回商户回盘文件入口
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		PayForReturnPayResult payForReturnPayResult = new PayForReturnPayResult();
		payForReturnPayResult.updateTransactionStatus();
	}
}
