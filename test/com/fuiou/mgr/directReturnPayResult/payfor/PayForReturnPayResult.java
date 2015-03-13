package com.fuiou.mgr.directReturnPayResult.payfor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mgr.ftp.payfor.ParseXMLFile;

/**
 * 代付直接返回商户回盘文件，
 * 不解析银行返回的回盘文件，
 * 用于测试，模拟解析银行返回
 * 302文件后生成商户回盘文件。
 * yangliehui
 *
 */
public class PayForReturnPayResult {
	private static final Logger logger = Logger.getLogger(PayForReturnPayResult.class);
	private TApsTxnLogService tapsTxnLogService = new TApsTxnLogService();
	protected TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
	protected Date currDbTime = tApsTxnLogService.getCurrDbTime();
	/**
	 * 根据商户号修改交易状态，
	 * 该状态与处理302文件后的
	 * 状态一样，用于模拟测试，
	 * 相当于该交易已经接受到了
	 * 银行的回盘文件。
	 */
	public void updateTransactionStatus(){
//		String mchmtcds = new ReturnPayResult().readValue();
//		String[] mchmtcd_arr = mchmtcds.split(",");
//		for(int i = 0;i<mchmtcd_arr.length;i++){
//			int status = updateResult(mchmtcd_arr[i]);
//			if(status>0){
//				logger.info("商户：[" + mchmtcd_arr[i] + "] 交易状态修改成功");
//			}else{
//				logger.info("商户：[" + mchmtcd_arr[i] + "] 没有要修改的交易记录");
//			}
//		}
		
		int status = updateResult("");
		if(status>0){
			logger.info("商户：[" + "" + "] 交易状态修改成功");
		}else{
			logger.info("商户：[" + "" + "] 没有要修改的交易记录");
		}
	}
	
	/**
	 * 修改状态
	 * @param mchmtcd 商户号
	 * @return
	 */
	public int updateResult(String mchmtcd){
		List<TApsTxnLog> list = selectByExampleWithoutBLOBs(mchmtcd);
		if(list != null){
			for(int i = 0;i<list.size();i++){
				TApsTxnLog tApsTxnLog = list.get(i);
				tapsTxnLogService.updatePayResultStatus(tApsTxnLog);
				
				if(list.size()>0 && list.size() == 2 && i == 1){
					//insertError(tApsTxnLog);
				}
				if(list.size()>0 && list.size() == 3 && i == 2){
					//insertError(tApsTxnLog);
				}
				if(list.size()>0 && list.size() > 3 && (i> list.size()-2)){
					//insertError(tApsTxnLog);
				}
			}
			return 1;
		}
		return 0;
	}
	
	public List<TApsTxnLog> selectByExampleWithoutBLOBs(String mchnt_cd){
		List<String> mchnts = ParseXMLFile.getMchntscd(this.getClass().getResourceAsStream("/directMchntcd.xml"));
		List<TApsTxnLog> lists = tapsTxnLogService.selectByExampleWithoutBLOBs(mchnts);
		if(null == lists || lists.size() == 0){
			return null;
		}
		return lists;
	}
	
	public void insertError(TApsTxnLog tApsTxnLog1){
		String tracNo = new TSeqService().getKbpsTraceNo12(currDbTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		tApsTxnLog1.setKBPS_SRC_SETTLE_DT(format.format(currDbTime));
		tApsTxnLog1.setSRC_MODULE_CD("CPS");
		tApsTxnLog1.setSUB_TXN_SEQ((short)0);
		tApsTxnLog1.setKBPS_TRACE_NO(tracNo);
		tApsTxnLog1.setBUSI_CD("TP01");
		tApsTxnLog1.setDEST_TXN_ST("1");//成功目标状态为成功，表示退票成功。
		tApsTxnLog1.setDEST_RSP_CD("202012");
		tApsTxnLog1.setKBPS_RSP_CD("202012");
		tApsTxnLog1.setADDN_PRIV_DATA("");
		tApsTxnLog1.setSRC_ORDER_NO(tApsTxnLog1.getSRC_ORDER_NO());
		tApsTxnLog1.setCREDIT_ACNT_NO(tApsTxnLog1.getCREDIT_ACNT_NO());//代收卡号
		Date now =new Date();
		tApsTxnLog1.setTXN_RCV_TS(now);
		tApsTxnLog1.setTXN_FIN_TS(now);
		tApsTxnLog1.setTO_TS(now);
		tApsTxnLog1.setCAPITAL_DIR("D");
		tApsTxnLog1.setTXN_CD("V52");
		tApsTxnLog1.setRESRV_DATA_2(tApsTxnLog1.getRESRV_DATA_2());
		tapsTxnLogService.apsTxnLogService(tApsTxnLog1);
	}
}
