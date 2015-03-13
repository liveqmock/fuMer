package com.fuiou.mgr.action.payfor;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mer.model.TRootBankInf;
import com.fuiou.mer.model.VCityInf;
import com.fuiou.mer.model.VProvInf;
import com.fuiou.mer.service.TPmsBankInfService;
import com.fuiou.mer.service.VCityInfService;
import com.fuiou.mer.service.VProvInfService;
import com.fuiou.mer.util.CardUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mgr.checkout.CheckOutBase;
import com.fuiou.mgr.util.Formator;
import com.fuiou.mgr.util.StringUtil;
import com.opensymphony.xwork2.ActionSupport;

public class PayForAjaxAction extends ActionSupport {

    private static final long serialVersionUID = -3966566045916356611L;
    private List<VProvInf> provInfs;
	public List<VProvInf> getProvInfs() {
		return provInfs;
	}
	public void setProvInfs(List<VProvInf> provInfs) {
		this.provInfs = provInfs;
	}

	/**
	 * <p>
	 * 返回省ProvList对象
	 * </p>
	 * 
	 * @return
	 */
	public String returnProvList() {
		provInfs = new ArrayList<VProvInf>();
		VProvInfService vProvInfService = new VProvInfService();
		List<VProvInf> vProvInfs = vProvInfService.selectVProvInfAll();
		if (vProvInfs.size() > 0) {
			String info = "";
			info = "[";
			for (int i = 0; i < vProvInfs.size(); i++) {
				info += "{";
				info += "PROV_CD:'" + vProvInfs.get(i).getPROV_CD() + "|"+vProvInfs.get(i).getPROV_NM()+"',PROV_NM:'" + vProvInfs.get(i).getPROV_NM() + "'";
				info += "}";
				if (!(i == vProvInfs.size() - 1)) {
					info += ",";
				}
			}
			info += "]";
			writeString(info);
		}
		return null;
	}
	// 市区/县
	public String returnCityList() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String provId = request.getParameter("provId");
		if (StringUtil.isEmpty(provId)) {
			return null;
		}
		String provIds[] = provId.split("\\|");
		VCityInfService cityInfService = new VCityInfService();
		List<VCityInf> cityInfs = cityInfService.selectVCityInfByProvId(provIds[0]);
		if (cityInfs.size() > 0) {
			String info = "";
			info = "[";
			for (int i = 0; i < cityInfs.size(); i++) {
				info += "{";
				info += "CITY_CD:'" + cityInfs.get(i).getCITY_CD()+"|"+cityInfs.get(i).getCITY_NM() + "',CITY_NM:'" + cityInfs.get(i).getCITY_NM() + "'";
				info += "}";
				if (!(i == cityInfs.size() - 1)) {
					info += ",";
				}
			}
			info += "]";
			writeString(info);
		}
		return null;
	}
	
	//银行
	public String returnBankList(){
		List<TRootBankInf> bankInfs = SystemParams.tRootBankInfs;
		if(bankInfs.size()>0){
			String info = "";
			info = "[";
			for (int i = 0; i < bankInfs.size(); i++) {
				info += "{";
				info += "BANK_CD:'" + bankInfs.get(i).getBANK_CD() +"|"+bankInfs.get(i).getBANK_NM()+ "',BANK_NM:'" + bankInfs.get(i).getBANK_NM() + "'";
				info += "}";
				if (!(i == bankInfs.size() - 1)) {
					info += ",";
				}
			}
			info += "]";
			writeString(info);
		}
		return null;
	}
	
	public String returnSubBank(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String city = request.getParameter("city");
		String bank = request.getParameter("bank");
		String citys[] =city.split("\\|");
		String banks[] = bank.split("\\|");
		TPmsBankInfService service = new TPmsBankInfService();
		String data = service.selectByBankCdAndCityCd(banks[0], citys[0]);
		if(StringUtil.isNotEmpty(data)){
			writeString(data);
		}

		return null; 
	}
	
	public String returnIssCd(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String cardNo = request.getParameter("cardNo");
		String selectBank_nm = request.getParameter("selectBank_nm");
		String[] banks = selectBank_nm.split("\\|", 2);// 开户银行
		// 检查银行账号
        TCardBin tCardBin = CardUtil.getTCardBinByCardNo(cardNo, SystemParams.cardBinMap);
        //获取发卡机构号issCd，校验开户行代码substring(2,6)与总行代码是否一致,
        String issCd = CardUtil.getIssCdByCardNo(cardNo, SystemParams.cardBinMap);
      //卡BIN不为空，需要校验用户总行代号和卡BIN表里的是否一致,收款人银行账号是否正确
        if(tCardBin!=null){ //卡BIN不为空，需要校验用户总行代号和卡BIN表里的是否一致
            if(null == issCd || issCd.length()!=10 || !issCd.substring(2, 6).equals(banks[0])){
            	writeString("false");
            }else{
                TRootBankInf tRootBankInf = SystemParams.bankMap.get(banks[0]);
                if(tRootBankInf==null){
                    writeString("false");
                }else{
                    writeString("true");
                }
            }
        }else{ //卡BIN为空，找到总行代号对应的开户行代号 4位转10位
        	writeString("true");
        }
        return null;
	}
	
	public String returnAmount(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String amount = request.getParameter("amount");
		// 检查金额
        long money = Formator.yuan2Fen(amount);
        if (money < 1 || money > new CheckOutBase().getMaxAmtPayFor()) {
        	writeString("false");
        }else{
        	writeString("true");
        }
        return null;
	}
	
	public void writeString(String info) {
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter printWriter;
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
			printWriter.write(info);
			printWriter.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
