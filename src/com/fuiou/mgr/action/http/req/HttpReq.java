package com.fuiou.mgr.action.http.req;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.model.req.incomefor.InComeForReqCoder;
import com.fuiou.mer.model.req.incomefor.InComeForReqType;
import com.fuiou.mer.model.req.payfor.PayForReqCoder;
import com.fuiou.mer.model.req.payfor.PayForReqType;
import com.fuiou.mer.model.req.qrytrans.QryTransReqCoder;
import com.fuiou.mer.model.req.qrytrans.QryTransReqType;
import com.fuiou.mer.model.req.verify.VerifyReqCoder;
import com.fuiou.mer.model.req.verify.VerifyReqType;
import com.fuiou.mer.model.rsp.incomefor.InComeForRspCoder;
import com.fuiou.mer.model.rsp.incomefor.InComeForRspType;
import com.fuiou.mer.model.rsp.payfor.PayForRspCoder;
import com.fuiou.mer.model.rsp.payfor.PayForRspType;
import com.fuiou.mer.model.rsp.qrytrans.QryTransRspCoder;
import com.fuiou.mer.model.rsp.qrytrans.QryTransRspType;
import com.fuiou.mer.model.rsp.verify.VerifyRspCoder;
import com.fuiou.mer.model.rsp.verify.VerifyRspType;
import com.fuiou.mer.util.MemcacheUtil;
import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.http.connector.HttpReqFactory;
import com.fuiou.mgr.http.connector.HttpReqProcessInterface;
import com.fuiou.mgr.util.HttpThreadConfigUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * HTTP直连接口付款请求
 * yangliehui
 */
public class HttpReq extends ActionSupport {
	private static final long serialVersionUID = 3088720773600963724L;
	private static final Logger logger = LoggerFactory.getLogger(HttpReq.class);
	
	@Override
	public String execute() throws Exception {
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		// 商户号
		String merid = request.getParameter("merid");
		logger.debug("merid:"+merid);
		try{
			// 请求类型
			String reqtype = request.getParameter("reqtype");
			logger.debug("reqtype:"+reqtype);
			if (reqtype == null || "".equals(reqtype.trim())) {
				this.verifyRsp(reqtype, TDataDictConst.HTTP_REQTYPE_IS_NULL,
						"请求类型为空");
				return null;
			}
			if (!TDataDictConst.checkHttpReqType(reqtype)) {
				this.verifyRsp(reqtype, TDataDictConst.HTTP_REQTYPE_NOT_EXIST,
						"请求类型不存在");
				return null;
			}
			if (merid != null) {
				if ("".equals(merid.trim())) {
					this.verifyRsp(reqtype, TDataDictConst.HTTP_MCHNT_ER, "商户号为空");
					return null;
				}
				if (merid.trim().length() > 15) {
					this.verifyRsp(reqtype, TDataDictConst.HTTP_MCHNT_ER2, "商户号过长");
					return null;
				}
				boolean f = HttpThreadConfigUtil.isAcceptRequest(merid);
				if(!f){
					verifyRsp(reqtype, TDataDictConst.HTTP_REQUEST_FREQUENTLY, "请求过于频繁,稍后再试");
					return null;
				}
				if(TDataDictConst.REQTP_QRYTRANSREQ.equals(reqtype)){
					if(!HttpThreadConfigUtil.checkQueryFrequency(merid)){
						verifyRsp(reqtype, TDataDictConst.HTTP_REQUEST_FREQUENTLY, "查询请求过于频繁,稍后再试");
						return null;
					}
				}
			}
			// 请求参数
			String xml = request.getParameter("xml");
			if (xml == null || "".equals(xml.trim())) {
				this.verifyRsp(reqtype, TDataDictConst.FILE_CONTENT_ROWS_ER,
						"请求参数为空");
				return null;
			}
			logger.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS").format(new Date())+"HTTPS接收报文xml:"+xml);
			// 校验值
			String mac = request.getParameter("mac");
			if (mac == null || "".equals(mac.trim())) {
				this.verifyRsp(reqtype, TDataDictConst.FIlE_SIGNER_ER, "校验值为空");
				return null;
			}
			logger.info("密钥mac:"+mac);
			// 校验参数
			String keyValue = MemcacheUtil.getMchntKey(merid);
			if (keyValue == null || "".equals(keyValue)) {
				this.verifyRsp(reqtype, TDataDictConst.FIlE_SIGNER_ER, "未找到账户密钥");
				return null;
			}
			String macSource = merid + "|" + keyValue + "|" + reqtype + "|" + xml;
			logger.info("local md5 source:"+macSource);
			String macVerify = DigestUtils.md5Hex(macSource.getBytes(TDataDictConst.DB_CHARSET));
			logger.info("local md5 result:"+macVerify);
			if (!mac.equalsIgnoreCase(macVerify)) {
				this.verifyRsp(reqtype, TDataDictConst.FIlE_SIGNER_ER,	
						"请求参数与校验值不匹配");
				return null;
			}
			this.transDistribute(reqtype, xml, merid);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			HttpThreadConfigUtil.closeConnect(merid);
		}
		return null;
	}

	/**
	 * 校验后发现错误返回的结果
	 * 
	 * @param reqType
	 *            请求类型
	 * @param ret
	 *            返回码
	 * @param memo
	 *            说明
	 * @throws Exception
	 */
	public void verifyRsp(String reqType, String ret, String memo) {
		ActionContext context = ActionContext.getContext();
		HttpServletResponse response = (HttpServletResponse) context
				.get(ServletActionContext.HTTP_RESPONSE);
		String xml = "";
		if (reqType == null || "".equals(reqType)) {
			xml = ret+"  "+memo;
		} else {
			if (reqType.equals(TDataDictConst.REQTP_INCOMEFORREQ)) {
				InComeForRspType inComeForRspType = new InComeForRspType();
				inComeForRspType.setRet(ret);
				inComeForRspType.setMemo(memo);
				try {
					xml = InComeForRspCoder.marshal(inComeForRspType);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			} else if (reqType.equals(TDataDictConst.REQTP_SINCOMEFORREQ)) {
				InComeForRspType inComeForRspType = new InComeForRspType();
				inComeForRspType.setRet(ret);
				inComeForRspType.setMemo(memo);
				try {
					xml = InComeForRspCoder.marshal(inComeForRspType);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			} else if (reqType.equals(TDataDictConst.REQTP_PAYFORREQ)) {
				PayForRspType payForRspType = new PayForRspType();
				payForRspType.setRet(ret);
				payForRspType.setMemo(memo);
				try {
					xml = PayForRspCoder.marshal(payForRspType);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			} else if (reqType.equals(TDataDictConst.REQTP_VERIFYREQ)) {
				VerifyRspType verifyRspType = new VerifyRspType();
				verifyRspType.setRet(ret);
				verifyRspType.setMemo(memo);
				try {
					xml = VerifyRspCoder.marshal(verifyRspType);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			} else if (reqType.equals(TDataDictConst.REQTP_QRYTRANSREQ)) {
				QryTransRspType qryTransRspType = new QryTransRspType();
				qryTransRspType.setRet(ret);
				qryTransRspType.setMemo(memo);
				try {
					xml = QryTransRspCoder.marshal(qryTransRspType);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			} 
			logger.info(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS")
			.format(new Date())+"HTTPS发送报文xml:"+xml);
		}
		System.out.println(" 返回客户端xml:"
				+ (Pattern.compile("\\s*|\t|\r|\n").matcher(xml))
						.replaceAll(""));
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(
					response.getOutputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printWriter.write(xml);
		printWriter.close();
	}

	/**
	 * 根据请求类型分配任务
	 * 
	 * @param reqType
	 *            请求类型
	 * @param xml
	 *            请求参数
	 */
	public void transDistribute(String reqType, String xml, String mchnt) {
		ActionContext context = ActionContext.getContext();
		HttpServletResponse response = (HttpServletResponse) context
				.get(ServletActionContext.HTTP_RESPONSE);
		HttpReqProcessInterface httpReqProcessInterface = HttpReqFactory
				.getTxnProcessClass(reqType);
		String rspXml = "";
		String busiCd = "";
		if (reqType.equals(TDataDictConst.REQTP_INCOMEFORREQ)
				|| reqType.equals(TDataDictConst.REQTP_SINCOMEFORREQ)) {
			InComeForReqType inComeForReqType = null;
			try {
				inComeForReqType = InComeForReqCoder.unmarshal(xml);
				if (inComeForReqType == null) {
					this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL,
							"请求参数解析错误");
					return;
				}
				// 校验必填项

			} catch (JAXBException e) {
				this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL, "请求参数解析错误");
				e.printStackTrace();
				return;
			}
			// 调用收款接口
			busiCd = TDataDictConst.BUSI_CD_INCOMEFOR;
			rspXml = httpReqProcessInterface.httpProcess(inComeForReqType,
					mchnt, busiCd);
		} else if (reqType.equals(TDataDictConst.REQTP_PAYFORREQ)) {
			PayForReqType payForReqType = null;
			try {
				payForReqType = PayForReqCoder.unmarshal(xml);
				if (payForReqType == null) {
					this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL,
							"请求参数解析错误");
					return;
				}
				// 校验必填项

			} catch (JAXBException e) {
				this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL, "请求参数解析错误");
				e.printStackTrace();
				return;
			}
			// 调用付款接口
			busiCd = TDataDictConst.BUSI_CD_PAYFOR;
			rspXml = httpReqProcessInterface.httpProcess(payForReqType, mchnt,
					busiCd);
		} else if (reqType.equals(TDataDictConst.REQTP_VERIFYREQ)) {
			VerifyReqType verifyReqType = new VerifyReqType();
			try {
				verifyReqType = VerifyReqCoder.unmarshal(xml);
				if (verifyReqType == null) {
					this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL,
							"请求参数解析错误");
					return;
				}
				// 校验必填项

			} catch (JAXBException e) {
				this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL, "请求参数解析错误");
				e.printStackTrace();
				return;
			}
			// 调用账户验证接口
			busiCd = TDataDictConst.BUSI_CD_VERIFY;
			rspXml = httpReqProcessInterface.httpProcess(verifyReqType, mchnt,
					busiCd);
		} else if (reqType.equals(TDataDictConst.REQTP_QRYTRANSREQ)) {
			QryTransReqType qryTransReqType = new QryTransReqType();
			try {
				qryTransReqType = QryTransReqCoder.unmarshal(xml);
				if (qryTransReqType == null) {
					this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL,
							"请求参数解析错误");
					return;
				}
				// 校验必填项

			} catch (JAXBException e) {
				this.verifyRsp(reqType, TDataDictConst.UNSUCCESSFUL, "请求参数解析错误");
				e.printStackTrace();
				return;
			}
			// 调用交易查询接口
			busiCd = qryTransReqType.getBusicd();
			rspXml = httpReqProcessInterface.httpProcess(qryTransReqType,
					mchnt, busiCd);
		} else if(reqType.equals(TDataDictConst.REQTP_ADDCUSTMRBUSI)){
			rspXml = httpReqProcessInterface.httpProcess(xml, mchnt, busiCd);
		}
		System.out.println(" 返回客户端xml:"	+ (Pattern.compile("\\s*|\t|\r|\n").matcher(rspXml)).replaceAll(""));
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(
					response.getOutputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printWriter.write(rspXml);
		printWriter.close();
	}
	
	public static void main(String[] args) {
		String s = "0002900F0262020|123456|sincomeforreq|<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><incomeforreq><ver>1.0</ver><merdt>20140722</merdt><orderno>22223222222221111</orderno><bankno>0102</bankno><accntno>9558801001114990000</accntno><accntnm>查晓峰 </accntnm><amt>100</amt><entseq>wacaiPay_207</entseq><memo></memo><mobile></mobile><certtp></certtp><certno></certno></incomeforreq>";
		try {
			System.out.println(DigestUtils.md5Hex(s.getBytes(TDataDictConst.DB_CHARSET)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}
