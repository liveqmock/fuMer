package com.fuiou.mgr.directReturnPayResult.payfor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.fuiou.mer.model.TApsTxnLog;
import com.fuiou.mer.service.TApsTxnLogService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.util.TDataDictConst;

public class PayForReturnPayResultTest {
	@Test
	public void payForReturnPayResultTest(){
		TApsTxnLogService tApsTxnLogService = new TApsTxnLogService();
		TApsTxnLog tApsTxnLog1 = tApsTxnLogService.getTapsTxnLogByKey("20110628", "WMP", "542447141580", (short)0);
		Date currDbTime = tApsTxnLogService.getCurrDbTime();
		
		tApsTxnLog1.setRLAT_KBPS_SRC_SETTLE_DT(tApsTxnLog1.getKBPS_SRC_SETTLE_DT());
		tApsTxnLog1.setRLAT_SRC_MODULE_CD(tApsTxnLog1.getSRC_MODULE_CD());
		tApsTxnLog1.setRLAT_SUB_TXN_SEQ(tApsTxnLog1.getSUB_TXN_SEQ());
		tApsTxnLog1.setRLAT_KBPS_TRACE_NO(tApsTxnLog1.getKBPS_TRACE_NO());
		
		if(tApsTxnLog1.getBUSI_CD().trim().equals(TDataDictConst.BUSI_CD_PAYFOR)){
			tApsTxnLog1.setFIRST_KBPS_SRC_SETTLE_DT(tApsTxnLog1.getKBPS_SRC_SETTLE_DT());
			tApsTxnLog1.setFIRST_KBPS_TRACE_NO(tApsTxnLog1.getKBPS_TRACE_NO());
			tApsTxnLog1.setFIRST_SRC_MODULE_CD(tApsTxnLog1.getSRC_MODULE_CD());
			tApsTxnLog1.setFIRST_SUB_TXN_SEQ(tApsTxnLog1.getSUB_TXN_SEQ());
		}
		
		String tracNo = new TSeqService().getKbpsTraceNo12(currDbTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		tApsTxnLog1.setKBPS_SRC_SETTLE_DT(format.format(currDbTime));
		tApsTxnLog1.setSRC_MODULE_CD("CPS");
		tApsTxnLog1.setSUB_TXN_SEQ((short)0);
		tApsTxnLog1.setKBPS_TRACE_NO(tracNo);
		
		// 重置状态
		tApsTxnLog1.setSRC_TXN_ST("1");
		tApsTxnLog1.setDEST_TXN_ST("1");
		tApsTxnLog1.setCUSTMR_TP("");
		tApsTxnLog1.setMSG_TP("");
		tApsTxnLog1.setCAPITAL_DIR("D");
		
		tApsTxnLog1.setBUSI_CD("TP01");
		tApsTxnLog1.setDEST_RSP_CD("202012");
		tApsTxnLog1.setKBPS_RSP_CD("202012");
		tApsTxnLog1.setADDN_PRIV_DATA("");
		tApsTxnLog1.setSRC_ORDER_NO(tApsTxnLog1.getSRC_ORDER_NO());
		tApsTxnLog1.setCREDIT_ACNT_NO(tApsTxnLog1.getCREDIT_ACNT_NO());//代收卡号
		Date now =new Date();
		tApsTxnLog1.setTXN_RCV_TS(now);
		tApsTxnLog1.setTXN_FIN_TS(now);
		tApsTxnLog1.setTO_TS(now);
		tApsTxnLog1.setTXN_CD("V52");
		tApsTxnLog1.setRESRV_DATA_2(tApsTxnLog1.getRESRV_DATA_2());
		tApsTxnLogService.apsTxnLogService(tApsTxnLog1);
	}
}
