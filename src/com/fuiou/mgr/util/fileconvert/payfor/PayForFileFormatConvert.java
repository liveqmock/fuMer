package com.fuiou.mgr.util.fileconvert.payfor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TFileErrInf;
import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;

/**
 * 文件格式转换
 * yangliehui
 *
 */
public abstract class PayForFileFormatConvert{
	private static Logger logger = LoggerFactory.getLogger(PayForFileFormatConvert.class);
	
	protected TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
	protected TDataDictService tDataDictService = new TDataDictService();
	
	protected LinkedHashMap<String, String> errorMap = new LinkedHashMap<String, String>();
	protected HashMap<String, String> sameFileMap = new HashMap<String, String>();
	protected List<TFileErrInf> errListMap = new ArrayList<TFileErrInf>();
	protected List<TFileErrInf> oneTxnErrListMap = new ArrayList<TFileErrInf>();
	protected PayForTxnInfBean payForTxnInfBean;
	
	protected String busiCd;
	
	protected String mchntCd;

	protected TOperatorInf tOperatorInf;
	
	protected TInsMchntInf tInsMchntInf;
	
	public TOperatorInf gettOperatorInf() {
		return tOperatorInf;
	}

	public void settOperatorInf(TOperatorInf tOperatorInf) {
		this.tOperatorInf = tOperatorInf;
	}

	public TInsMchntInf gettInsMchntInf() {
		return tInsMchntInf;
	}

	public void settInsMchntInf(TInsMchntInf tInsMchntInf) {
		this.tInsMchntInf = tInsMchntInf;
	}

	public String getBusiCd() {
		return busiCd;
	}

	public void setBusiCd(String busiCd) {
		this.busiCd = busiCd;
	}
	
	public String getMchntCd() {
		return mchntCd;
	}

	public void setMchntCd(String mchntCd) {
		this.mchntCd = mchntCd;
	}

	/**
	 * 操作文件，返回需要的格式对象
	 * @param convertFile 要转换的文件
	 * @return
	 */
	public abstract Map<String, String> handlerFile(AccessBean convertFile);

	
	/**
	 * 设置交易类型
	 */
	public abstract void setBusi_CD();
	
	/**
	 * 设置商户号
	 * @param mchntCdStr
	 */
	public abstract void setMchnt_Cd(String mchntCdStr);
	
	/**
	 * 操作员
	 * @param tOperatorInf
	 */
	public abstract void setTOperatorInfFromPage(TOperatorInf tOperatorInfFromPage);
	
	public abstract void setTInsInfFromPage(TInsMchntInf tInsInfFromPage);


	 
    /**
     * 创建txt文件
     * @param savefile 保存目录
     * @return
     */
    public boolean createFile(File savefile){
    	if (!savefile.getParentFile().exists()) {
    		savefile.getParentFile().mkdirs();
		}
    	List<PayForTxnInfDetailRowBean> list = payForTxnInfBean.getPayForTxnInfDetailRowBeanList();
    	BufferedOutputStream write = null;
    	try {
    		//写入文件
			write = new BufferedOutputStream(new FileOutputStream(savefile));
			write.write(payForTxnInfBean.getPayForTxnInfSumBean().getMchntCd().trim().getBytes());
			write.write("|".getBytes());
			write.write(payForTxnInfBean.getPayForTxnInfSumBean().getBusiCd().trim().getBytes());
			write.write("|".getBytes());
			write.write(payForTxnInfBean.getPayForTxnInfSumBean().getTxnDate().trim().getBytes());
			write.write("|".getBytes());
			write.write(payForTxnInfBean.getPayForTxnInfSumBean().getDayNo().trim().getBytes());
			write.write("|".getBytes());
			write.write(Integer.valueOf(payForTxnInfBean.getPayForTxnInfSumBean().getSumDetails()).toString().trim().getBytes());
			write.write("|".getBytes());
			write.write(FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(payForTxnInfBean.getPayForTxnInfSumBean().getSumAmt())).trim().toString().getBytes());
			write.write("\n".getBytes());
			for(int i = 0;i<list.size();i++){
				PayForTxnInfDetailRowBean payForFileDetailRowBean = list.get(i);
				write.write(payForFileDetailRowBean.getDetailSeriaNo().trim().getBytes());
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getBankCd().trim().getBytes());
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getCity().trim().getBytes());
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getBankNo().trim().getBytes());
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getBankAccount().trim().getBytes());
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getAccountName().trim().getBytes());
				write.write("|".getBytes());
				try {
					write.write(FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(payForFileDetailRowBean.getDetailAmt())).trim().toString().getBytes());
				} catch (Exception e) {
					write.write(payForFileDetailRowBean.getDetailAmt().trim().getBytes());
				}
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getEnpSeriaNo().trim().getBytes());
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getMemo().trim().getBytes());
				write.write("|".getBytes());
				write.write(payForFileDetailRowBean.getMobile().trim().getBytes());
				write.write("\n".getBytes());
			}
			write.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
    	return true;
    }
}
