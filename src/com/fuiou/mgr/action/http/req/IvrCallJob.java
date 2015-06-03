package com.fuiou.mgr.action.http.req;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fuiou.mer.model.TCustmrBusi;
import com.fuiou.mer.service.TCustmrBusiService;
import com.fuiou.mgr.util.IVRUtil;

/**
 * IVR外呼job
 * @author Jerry
 *
 */
public class IvrCallJob implements Job{
	
	private TCustmrBusiService custmrBusiService = new TCustmrBusiService();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		List<TCustmrBusi> custmrBusies = custmrBusiService.getCallBackAbleBusi();
		if(custmrBusies!=null && custmrBusies.size()>0){
			for(TCustmrBusi custmrBusi:custmrBusies){
				try {
					IVRUtil.addIvrTask(custmrBusi.getMOBILE_NO());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
