package com.fuiou.mgr.adapter.payfor;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TBankAlias;
import com.fuiou.mer.model.TCityAlias;
import com.fuiou.mer.model.TPmsBankAlias;
import com.fuiou.mer.service.TBankAliasService;
import com.fuiou.mer.service.TCityAliasService;
import com.fuiou.mer.service.TDataDictService;
import com.fuiou.mer.service.TPmsBankAliasService;
import com.fuiou.mer.service.TSeqService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.adapter.FileAccessAdapter;
import com.fuiou.mgr.adapter.payfor.huaanfileProcess.FileAnalysisAdapter;
import com.fuiou.mgr.adapter.payfor.huaanfileProcess.IFileAnalysis;
import com.fuiou.mgr.bean.access.AccessBean;
import com.fuiou.mgr.bean.access.FileAccess;
import com.fuiou.mgr.bean.convert.PayForTxnInfBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfDetailRowBean;
import com.fuiou.mgr.bean.convert.PayForTxnInfSumBean;
import com.fuiou.mgr.bean.convert.TxnInfBean;
import com.fuiou.mgr.checkout.CheckOutBase;

public class HuaAnPayForAccessAdapter extends FileAccessAdapter{
	private static Logger logger = LoggerFactory.getLogger(HuaAnPayForAccessAdapter.class);
	private TSeqService tSeqService = new TSeqService();
	private TDataDictService tDataDictService = new TDataDictService();
	private String busiCd = TDataDictConst.BUSI_CD_PAYFOR;
	
	PayForTxnInfBean payForTxnInfBean;
	PayForTxnInfSumBean payForTxnInfSumBean;
	List<PayForTxnInfDetailRowBean> payForTxnInfDetailRowBeanList;
	@Override
	public TxnInfBean analysisTxn(String mchntCd, AccessBean accessBean, String txnInfSource, String txnDataType) {
//		payForTxnInfBean=new PayForTxnInfBean();
//		payForTxnInfSumBean=payForTxnInfBean.getPayForTxnInfSumBean();
//		payForTxnInfDetailRowBeanList=payForTxnInfBean.getPayForTxnInfDetailRowBeanList();
//		super.analysisTxn(mchntCd, accessBean, txnInfSource,txnDataType);
//		FileAccess fileAccess = (FileAccess)accessBean;
//		File convertFile = fileAccess.getFile();
//		
//		if(null != convertFile){
//			logger.debug("开始处理华安的文件,文件名为:"+convertFile.getName());
//			try {
//				// 转换poi对象
//				HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(convertFile));
//				
//				// 获取第一个sheet
//				HSSFSheet firstSheet=book.getSheetAt(0);
//				if(null != firstSheet){
//					Integer sumRows = firstSheet.getLastRowNum();//总行数
//					if(sumRows<=0){
//						return null;
//					}else{
//						int rowSumFor = 0;
//						HSSFRow row0 = firstSheet.getRow(0);
//						try {
//							String businType = getCellValue(row0.getCell(0));
//							String payCon = getCellValue(row0.getCell(1));
//							String account = getCellValue(row0.getCell(5));
//							if(!"业务类型".equals(businType)||!"付款公司名称".equals(payCon)||!"收款方账号".equals(account)){
//								return null;
//							}
//						} catch (Exception e) {
//							return null;
//						}
//						// 循环设置明细行
//						int actualRowNum = 0;
//						for(int i = 1;i <= sumRows; i++){
//							actualRowNum ++;
//							PayForTxnInfDetailRowBean payForTxnInfDetailRowBean = new PayForTxnInfDetailRowBean();
//							HSSFRow row = firstSheet.getRow(i);
//							
//							String bankNameHw = bankNm(getCellValue(row.getCell(7)));//收款人开户行，excel数据
//							String bankId = bankConvert(bankNameHw,mchntCd);//收款人开户行代码(总行代码)
//							
//							String cityNameHw = getCellValue(row.getCell(9));//收款人开户行城市，excel数据
//							String cityId = cityConvert(cityNameHw,mchntCd);//收款人开户行城市代码
////							String bankName = getCellValue(row.getCell(7));//收款人开户行支行名称
//							String count = getCellValue(row.getCell(5));//收款人银行帐号
//							String ownerName = getCellValue(row.getCell(6));//户名
//							String amt = getCellValue(row.getCell(14));//金额
//							String memo = getCellValue(row.getCell(15));//备注
//							if("".equals(bankNameHw)&&"".equals(cityNameHw)&&"".equals(bankNameHw)&&"".equals(count)&&"".equals(ownerName)&&"".equals(amt)&&"".equals(memo)){
//								break;
//							}
//							rowSumFor ++;
//							payForTxnInfDetailRowBean.setDetailSeriaNo(getRowSeriaNo(i));
//							payForTxnInfDetailRowBean.setBankCd(bankId);
//							payForTxnInfDetailRowBean.setCity(cityId);
//							payForTxnInfDetailRowBean.setBankNo(bankNameHw);
//							payForTxnInfDetailRowBean.setBankAccount(count.replaceAll("-", ""));
//							payForTxnInfDetailRowBean.setAccountName(ownerName);
//							try {
//								payForTxnInfDetailRowBean.setDetailAmt(FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(amt)));
//							} catch (Exception e) {
//								payForTxnInfDetailRowBean.setDetailAmt(amt);
//							}
//							payForTxnInfDetailRowBean.setMemo("华安" + memo);
//							String stimedStringAmt = "";
//							try {
//								stimedStringAmt = FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(amt));
//							} catch (Exception e) {
//								stimedStringAmt = amt;
//							}
//							String trimedString = getRowSeriaNo(i) + "|" + bankId + "|" + cityId + "|" + bankNameHw + "|" + count.replaceAll("-", "") + "|" + ownerName + "|" + stimedStringAmt + "|" + "" + "|" + memo + "|" + "";
//							payForTxnInfDetailRowBean.setLineStr(trimedString);
//							payForTxnInfDetailRowBean.setActualRowNum(actualRowNum);
//							payForTxnInfDetailRowBeanList.add(payForTxnInfDetailRowBean);
//						}
//						//System.out.println(getPayForFileBean().getPayForDetailRows().size());
//						if(rowSumFor == 0){
//							return null;
//						}else{
//							// 设置汇总行
//							setMchntCdGotoBean(mchntCd);//设置商户号
//							setBusiCdGotoBean();//设置交易类型
//							setDate();//设置交易日期
//							setFileSerialNum();//设置文件序号
//							setFileRowsAndSum();//设置文件总行数和金额
//							payForTxnInfSumBean.setSumRow(payForTxnInfSumBean.getMchntCd()+"|"+payForTxnInfSumBean.getBusiCd()+"|"+payForTxnInfSumBean.getTxnDate()+"|"+payForTxnInfSumBean.getDayNo()+"|"+payForTxnInfSumBean.getSumDetails()+"|"+payForTxnInfSumBean.getSumAmt());
//							// 设置文件信息
//							payForTxnInfBean.setFileName(payForTxnInfSumBean.getBusiCd()+"_"+payForTxnInfSumBean.getTxnDate()+"_"+payForTxnInfSumBean.getDayNo()+".txt");
//							payForTxnInfBean.setFile(convertFile);
//							payForTxnInfBean.setMchntCd(mchntCd);
//							payForTxnInfBean.setTxnInfType(busiCd);	
//							payForTxnInfBean.setOprUsrId(accessBean.getOprUsrId());
//						}
//					}
//				}else {
//					return null;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//	            logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP)+convertFile.getName());
//	            return null;
//			} 
//		}else{
//            logger.error(TDataDictConst.FILE_READ_EP+" "+tDataDictService.selectTDataDictByClassAndKey(TDataDictConst.RET_CODE_SYS,TDataDictConst.FILE_READ_EP)+convertFile.getName());
//            return null;
//		}
//		return payForTxnInfBean;
		FileAccess fileAccess=(FileAccess)accessBean;
		IFileAnalysis fileAnalysis=FileAnalysisAdapter.analysisTxn(fileAccess.getFileName());
		if(fileAnalysis==null){
			 logger.error(TDataDictConst.FILE_READ_EP+"  文件类型不正确");
			return null;
		}
		return fileAnalysis.analysisTxn(mchntCd, accessBean, txnInfSource, txnDataType);
	}
	
//	/**  
//     * 获取Excel中某个单元格的值  
//     *   
//     * @param cell  
//     * @return  
//     */ 
//
//    public String getCellValue(HSSFCell cell) {  
//        String value = "";
//        if(null == cell){
//            return value;
//        }
//        switch (cell.getCellType()) {  
//        case HSSFCell.CELL_TYPE_NUMERIC: // 数值型  
//            if (HSSFDateUtil.isCellDateFormatted(cell)) {  
//                // 如果是date类型则 ，获取该cell的date值  
//                value = HSSFDateUtil.getJavaDate(cell.getNumericCellValue())  
//                        .toString();  
//                java.util.Date date1 = new Date(value);  
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
//                value = format.format(date1);  
//            } else {// 纯数字  
//                value = String.valueOf(cell.getNumericCellValue());  
//            }  
//            break;  
//        /* 此行表示单元格的内容为string类型 */  
//        case HSSFCell.CELL_TYPE_STRING: // 字符串型  
//            value = cell.getStringCellValue();  
//            break;  
//        case HSSFCell.CELL_TYPE_FORMULA:// 公式型  
//            // 读公式计算值  
//            value = String.valueOf(cell.getNumericCellValue());  
//            if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串  
//                value = cell.getStringCellValue().toString();  
//            }  
//            cell.getCellFormula();  
//            break;  
//        case HSSFCell.CELL_TYPE_BOOLEAN:// 布尔  
//            value = " " + cell.getBooleanCellValue();  
//            break;  
//        /* 此行表示该单元格值为空 */ 
//        case HSSFCell.CELL_TYPE_BLANK: // 空值  
//            value = "";  
//            break;  
//        case HSSFCell.CELL_TYPE_ERROR: // 故障  
//            value = "";  
//            break;  
//        default:  
//            value = cell.getStringCellValue().toString();  
//        }  
//        return value;  
//   
//    }
//    
//    /**
//	 * 银行转换为ID(与数据库对应)
//	 * @param bankName 银行名称
//	 * @param mchntCdn 商户号
//	 * @return 银行id
//	 */
//	public String bankConvert(String bankName,String mchntCd){
//	    TBankAlias tBankAlias = new TBankAliasService().bankConvert(bankName, mchntCd);
//	    if(tBankAlias==null){
//	        return "";
//	    }else{
//	        return tBankAlias.getBANK_CD();
//	    }
//	}
//	
//	/**
//	 * 文件上传的开户行名称转换成标准名称
//	 * @param bankNmOfFile 文件上传的名称
//	 * @return 标准名称
//	 */
//	public String bankNm(String bankNmOfFile){
//		TPmsBankAlias tPmsBankAlias = new TPmsBankAliasService().pmsBankConvert(bankNmOfFile);
//		if(tPmsBankAlias == null){
//			return bankNmOfFile;
//		}else{
//			return tPmsBankAlias.getPMS_BANK_NM();
//		}
//	}
//	
//	/**
//	 * 城市转换为ID(与数据库对应)
//	 * @param cityName 城市名称
//	 * @param mchntCd 商户号
//	 * @return 城市id
//	 */
//	public String cityConvert(String cityName,String mchntCd){
//		List<TCityAlias> list = new TCityAliasService().selectByExample("", cityName);
//		if(list.size() == 0){
//			if(cityName.length() != 0){
//				cityName = cityName.substring(0, cityName.length()-1);
//				List<TCityAlias> list2 = new TCityAliasService().selectByExample("", cityName);
//				if(list2.size() == 0){
//					return "";
//				}else{
//					TCityAlias tCityAlias2 = list2.get(0);
//					return tCityAlias2.getCITY_CD();
//				}
//			} else {
//				return "";
//			}
//		}else{
//			TCityAlias tCityAlias = list.get(0);
//			return tCityAlias.getCITY_CD();
//		}
//	}
//	
//	/**
//	 * 获取行号
//	 * @param rowNo 当前行
//	 * @return
//	 */
//	public String getRowSeriaNo(int rowNo){
//		if(rowNo >= 1 && rowNo < 10){
//			return "00000"+rowNo;
//		}else if(rowNo >= 10 && rowNo < 100){
//			return "0000"+rowNo;
//		}else if(rowNo >= 100 && rowNo < 1000){
//			return "000"+rowNo;
//		}else {
//			return "00"+rowNo;
//		}
//	}
//	
//	/**
//	 * set文件序号
//	 * @return 序号
//	 */
//	public String setFileSerialNum(){
//		String fileSerialId = tSeqService.huaAnFileSerialNum();
//		if(null != fileSerialId && !"".equals(fileSerialId)){
//			payForTxnInfSumBean.setDayNo(fileSerialId);
//			return fileSerialId;
//		}
//		return null;
//	}
//	
//	/**
//	 * set文件总行数和金额
//	 * @param payForFileBean
//	 * @return
//	 */
//	public int setFileRowsAndSum(){
//		int rows = 0;
//		double sums = 0;
//		if(null != payForTxnInfDetailRowBeanList){
//			for(PayForTxnInfDetailRowBean payForTxnInfDetailRowBean:payForTxnInfDetailRowBeanList){
//				rows ++;
//				try {
//					String amtRow = FuMerUtil.formatFenToYuan(FuMerUtil.formatYuanToFen(payForTxnInfDetailRowBean.getDetailAmt()));
//					if (CheckOutBase.validMoney(amtRow)) {
//						sums += Double.parseDouble(amtRow);
//					} else {
//						sums += 0l;
//					}
//				} catch (Exception e) {
//					sums += 0l;
//				}
//			}
//			long sumFen = Long.valueOf(FuMerUtil.formatYuanToFen(Double.valueOf(sums)));
//			payForTxnInfSumBean.setSumAmt(FuMerUtil.formatFenToYuan(sumFen));
//			payForTxnInfSumBean.setSumDetails(Integer.valueOf(rows).toString());
//			return rows;
//		}
//		return 0;
//	}
//	
//	/**
//	 * set交易日期
//	 * @return
//	 */
//	public String setDate(){
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//		Date date = new Date();
//		String dateStr = format.format(date);
//		payForTxnInfSumBean.setTxnDate(dateStr);
//		return dateStr;
//	}
//	
//	/**
//	 * set交易类型至文件Bean
//	 * @return
//	 */
//	public String setBusiCdGotoBean(){
//		if(null != TDataDictConst.BUSI_CD_PAYFOR && !"".equals(TDataDictConst.BUSI_CD_PAYFOR)){
//			payForTxnInfSumBean.setBusiCd(TDataDictConst.BUSI_CD_PAYFOR);
//			return TDataDictConst.BUSI_CD_PAYFOR;
//		}
//		return null;
//	}
//	
//	/**
//	 * set商户号至文件Bean
//	 * @return
//	 */
//	public String setMchntCdGotoBean(String mchntCdStr){
//		payForTxnInfSumBean.setMchntCd(mchntCdStr);
//		return null;
//	}
}
