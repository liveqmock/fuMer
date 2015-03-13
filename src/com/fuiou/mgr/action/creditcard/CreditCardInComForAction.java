package com.fuiou.mgr.action.creditcard;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.util.CardUtil;
import com.fuiou.mer.util.DateUtils;
import com.fuiou.mer.util.MD5Util;
import com.fuiou.mer.util.Object2Xml;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.bean.creditcard.CreditCardBean;
import com.fuiou.mgr.http.httpClient.HttpClientHelper;
import com.fuiou.mgr.util.Formator;
import com.fuiou.mgr.util.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class CreditCardInComForAction extends ActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(CreditCardInComForAction.class);
	private String userName;
	private String dedNum;
	private String amount;
	private String certificateTp;
	private String certificateNo;
	private String phoneNum;
	private String validityDt;
	private String cvv2;
	private String remark1;
	private String remark2;
	private String forward;
	private String frame;
	private String msg;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getFrame() {
		return frame;
	}
	public void setFrame(String frame) {
		this.frame = frame;
	}
	public String getForward() {
		return forward;
	}
	public void setForward(String forward) {
		this.forward = forward;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDedNum() {
		return dedNum;
	}
	public void setDedNum(String dedNum) {
		this.dedNum = dedNum;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCertificateTp() {
		return certificateTp;
	}
	public void setCertificateTp(String certificateTp) {
		this.certificateTp = certificateTp;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getValidityDt() {
		return validityDt;
	}
	public void setValidityDt(String validityDt) {
		this.validityDt = validityDt;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public String getRemark2() {
		return remark2;
	}
	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	@Override
	public String execute() throws Exception {
		
		return "cardConfirm";
	}
	public String confrimPost() throws UnsupportedEncodingException{
		// 操作员
		TOperatorInf tOperatorInf = (TOperatorInf) ActionContext.getContext().getSession().get(TDataDictConst.OPERATOR_INF);
		
		CreditCardBean creditCardBean = new CreditCardBean();
//		userName = URLEncoder.encode(userName, "UTF-8");
		creditCardBean.setAccntnm(userName);
		creditCardBean.setAccntno(dedNum);
		creditCardBean.setBankno("0000");
		creditCardBean.setCerttp(certificateTp);
		creditCardBean.setCertno(certificateNo);
		creditCardBean.setCvv2(cvv2);
		creditCardBean.setMerdt(DateUtils.getCurrentDate());
		creditCardBean.setMemo(remark1+"|"+remark2);
		creditCardBean.setMemo2(tOperatorInf.getLOGIN_ID()+"|卡号末4位:"+dedNum.substring(dedNum.length()-4));
		creditCardBean.setMobile(phoneNum);
		creditCardBean.setOrderno(DateUtils.getCurrentDate("yyyyMMddHHmmssSSS"));
		creditCardBean.setValidthru(validityDt);
		creditCardBean.setVer("1.00");
//		TMchntKeyService tMchntKeyService = new TMchntKeyService();
//		String keyValue = tMchntKeyService.getKeyValueByMchntCd(tOperatorInf.getMCHNT_CD());
		//敖炯修改(去掉签约20130729)
//		CreditCardBean creditCardBean2 = getBean(creditCardBean,"identify","identifyreq","identifyrsp", tOperatorInf.getMCHNT_CD(), "creditCard_identify", SystemParams.getProperty("creditCard_key"));
//		if(creditCardBean2 != null){
//			if("000000".equals(creditCardBean2.getRet())||"200031".equals(creditCardBean2.getRet())){
				creditCardBean.setAmt(Formator.yuan2Fen(amount)+"");
				creditCardBean.setOrderno(DateUtils.getCurrentDate("yyyyMMddHHmmssSSS"));
				CreditCardBean creditCardBean3 = getBean(creditCardBean,"incomefor" ,"incomeforreq","incomeforrsp",tOperatorInf.getMCHNT_CD(), "creditCard_incomfor", SystemParams.getProperty("creditCard_key"));
				if(creditCardBean3 != null){
					msg = creditCardBean3.getMemo();
				}else{
					msg = "服务器超时";
				}
//			}else{
//				msg = creditCardBean2.getMemo();
//			}
//		}else{
//			msg = "服务器超时";
//		}
		return "error";
	}
	public String card(){
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		String[] param = forward.split("\\|");
		String src=param[0].trim();
		if(param.length>1) frame=param[1].trim().split("=")[1];
		if(null==frame || "".equals(frame)){//没有指定则采用默认的frame页面
			setFrame("/WEB-INF/pages/stru/StruDefault.jsp");
		}
		if(!src.contains("action")){
			src="toJSP.action?toJSP="+src;
		}
		request.setAttribute("src", src);
		return "redirectURL";
	}
	public CreditCardBean getBean(CreditCardBean creditCardBean,String reqType,String reqTpDoc,String rspTpDoc,String mchntCd,String urlPro,String key){
		CreditCardBean creditCardBean2 = null;
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+Object2Xml.object2xml(creditCardBean, reqTpDoc);
		List<String[]> list = new ArrayList<String[]>();
		String[] nv1 = new String[] {"merid",mchntCd};
		String[] nv2 = new String[] { "xml", xml };
		list.add(nv1);
		list.add(nv2);
		String[] nv3 = new String[] {"reqtype",reqType};
		list.add(nv3);
		String url = SystemParams.getProperty(urlPro);
		String macSource = mchntCd+"|"+key+"|"+reqType+"|"+xml;
		String mac = MD5Util.encode(macSource, "UTF-8").toUpperCase();
		String[] nv4 = new String[]{"mac",mac};
		list.add(nv4);
		String nvPairs = HttpClientHelper.getNvPairs(list, "UTF-8");
		logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS")+reqType+":发送报文"+xml);
		logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS")+reqType+":发送参数"+nvPairs);
		String outStr = HttpClientHelper.doHttp(url, HttpClientHelper.POST,"UTF-8", nvPairs, SystemParams.getProperty("timeOut"));
		logger.info(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss SSS")+rspTpDoc+":接受报文"+outStr);
		if(StringUtil.isNotEmpty(outStr)){
			String[] outXml = outStr.split("\\&"); 
			creditCardBean2 = (CreditCardBean) Object2Xml.xml2object(outXml[2].substring(outXml[2].lastIndexOf("?")+2), rspTpDoc, CreditCardBean.class);
		}
		return creditCardBean2;
	}
	
	/**
	 * 跳转
	 * @return
	 */
	public String toCard(){
		return "card";
	}
	
	/**
	 * 校验卡号
	 */
	public void checkCard(){
		HttpServletResponse response=(HttpServletResponse)ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
		HttpServletRequest request=(HttpServletRequest)ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
		PrintWriter printWriter =null;
		String result="";
		try {
			response.setCharacterEncoding("UTF-8");
			printWriter = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
			String cardNo=request.getParameter("cardNo");
//			String issCd=request.getParameter("issCd");
			if(StringUtil.isEmpty(cardNo)){
				result="1";
				return;
			}
			TCardBin cardBin = CardUtil.getTCardBinByCardNo(cardNo, SystemParams.cardBinMap);//从卡号获取发卡机构号
			String code=null,memo=null;
			if(null==cardBin){
				code = "0";
				memo = "非法卡号";
			}else{
				String bankCd = cardBin.getISS_INS_CD().substring(3, 6);//行别代码
				String cardAttr = cardBin.getCARD_ATTR();//卡属性
				if(SystemParams.getProperty("BANK_CDS_CREDIT_CARD").contains(bankCd) && ("02".equals(cardAttr) || "03".equals(cardAttr))){//可以代扣的条件
					code = "1";
				}else{
					code = "0";
				}
				memo = "本卡为"+cardBin.getISS_INS_NM()+("01".equals(cardAttr)?"借记卡":"02".equals(cardAttr)?"贷记卡":"准贷记卡")+("1".equals(code)?",可以支持扣款":",不支持扣款");
			}
			result = code+"|"+memo;
		} catch (Exception e) {
			e.printStackTrace();
			result="0|非法卡号";
		}finally{
			printWriter.write(result);
			printWriter.close();
		}
	}
	
}
