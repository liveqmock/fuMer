package com.fuiou.mgr.action.payfor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.model.TFasHolidayDet;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TCustStlInfService;
import com.fuiou.mer.service.TFasHolidayService;
import com.fuiou.mer.util.FasService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mgr.util.VirtAcntUtil;

/**
 * 废弃3天或以上的付款交易
 * @author Jerry
 *
 */
public class DiscardPayforJob implements Job {

	private Logger logger = Logger.getLogger(DiscardPayforJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("start scanf ap01 txn logs .....");
		String date = FuMerUtil.date2String(new Date(), "yyyyMMdd");//获取当前日期
		TFasHolidayService fasHolidayService = new TFasHolidayService();
		TFasHolidayDet day = fasHolidayService.getLastWorkDay(date, 3);//获取X天以前的第一个工作日
		String startDt = FuMerUtil.addDays(day.getDAY_DEF(), Calendar.MONTH, -1);//获取工作日的前一个月的日期
		TApsTxnLogService service = new TApsTxnLogService();
		logger.debug("kbps_src_settle_dt between "+startDt+" and "+day.getDAY_DEF());
		Map<String,Double> balancesMap = new HashMap<String, Double>();
		List<TApsTxnLog> txnlogs = service.getUnsendPayforLogs(startDt,day.getDAY_DEF());//获取还未发送的付款交易
		if(txnlogs!=null&&txnlogs.size()>0){
			logger.debug("find txnlogs = "+txnlogs.size());
			for(TApsTxnLog txnlog:txnlogs){
				Double balance = queryBalance(balancesMap, txnlog.getSRC_INS_CD());
				if(txnlog.getSRC_TXN_AMT() > balance){//比对余额是否足够
					service.discard(txnlog);//废弃
				}
			}
		}else{
			logger.debug("can't find txnlogs ");
		}
	}
	
	/**
	 * 查询商户虚拟户可用余额
	 * @param balancesMap
	 * @param insCd
	 */
	private Double queryBalance(Map<String,Double> balancesMap,String insCd){
		if(balancesMap.get(insCd)!=null){
			return balancesMap.get(insCd);
		}else{
			try {
				logger.debug("start query insCd="+insCd+" balance ");
				TCustStlInfService custStlInfService = new TCustStlInfService();
				String acntNo = custStlInfService.getAcntNosByIndCd(insCd).get(0);
				String[] banalces = FasService.queryBalance(insCd, acntNo, VirtAcntUtil.getSsn());
				balancesMap.put(insCd, Double.valueOf(banalces[4]));
				return balancesMap.get(insCd);
			} catch (Exception e) {
				logger.debug("query banalce occur error,ins_cd="+insCd);
			}
		}
		return 0D;
	}
}
