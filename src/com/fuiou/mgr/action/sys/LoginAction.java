package com.fuiou.mgr.action.sys;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TInsMchntInf;
import com.fuiou.mer.model.TMenuInf;
import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.service.TInsMchntInfService;
import com.fuiou.mer.service.TMenuInfService;
import com.fuiou.mer.service.TOperatorInfService;
import com.fuiou.mer.util.FuMerUtil;
import com.fuiou.mer.util.SystemParams;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private String loginId;//登录号

	private String loginPwd;//登录密码

	private String mChantCd;//商户号

	private String sRand;//验证码

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getMChantCd() {
		return mChantCd;
	}

	public void setMChantCd(String chantCd) {
		mChantCd = chantCd;
	}

	public String getSRand() {
		return sRand;
	}

	public void setSRand(String rand) {
		sRand = rand;
	}

	@Override
	public String execute() throws Exception {
		ActionContext context = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
		HttpSession session = request.getSession();
		// 校验
		if(FuMerUtil.isEmpty(mChantCd)){
			addActionError("商户代码不能为空！");
			return Action.INPUT;
		}
		if(FuMerUtil.isEmpty(loginId)){
			addActionError("操作员代码不能为空！");
			return Action.INPUT;
		}
		if(FuMerUtil.isEmpty(loginPwd)){
			addActionError("操作员密码不能为空！");
			return Action.INPUT;
		}
		if(FuMerUtil.isEmpty(sRand)){
			addActionError("验证码不能为空！");
			return Action.INPUT;
		}
		String rand = (String) session.getAttribute("sRand"); // 得到放在session中的验证码
        if(rand == null){
            addActionError("验证码不正确！");
            return Action.INPUT;
        }
        if(!rand.equals(this.getSRand())){
            addActionError("验证码不正确！");
            return Action.INPUT;
        }
		TOperatorInfService tOperatorInfService=new TOperatorInfService();
		TOperatorInf tOperatorInf=tOperatorInfService.selectByMchntCdAndLoginId(mChantCd, loginId);
		
		TInsMchntInfService tInsMchntInfService = new TInsMchntInfService();
		
		TInsMchntInf tInsInf =tInsMchntInfService.selectTInsInfByMchntCd(mChantCd);
		if(null==tOperatorInf || null==tInsInf){
			addActionError("该商户或用户不存在！");
			return Action.INPUT;
		}
		String pwd = DigestUtils.md5Hex(loginPwd.getBytes(TDataDictConst.DB_CHARSET));
		if(!pwd.equals(tOperatorInf.getLOGIN_PWD())){
			addActionError("密码不正确！");
			return Action.INPUT;
		}
		//根据角色取得对应的菜单
		TMenuInfService tMenuInfService = new TMenuInfService();
		TMenuInf tMenuInf = tMenuInfService.selectByPrimaryKey(TDataDictConst.USAGE_KEY_NEW_MCHNT_PLAT);
        String menuContent = tMenuInf.getMENU_CONTENT();
        String roleId = tOperatorInf.getROLE_ID();
        @SuppressWarnings("rawtypes")
        List list = tMenuInfService.getAllMenuByRoleID(roleId, menuContent);
        if(null==list || list.size()==0){
        	addActionError("该用户无操作权限！");
			return Action.INPUT;
		}
        //刷新分公司
        SystemParams.refreshInsInf(tInsInf);
		//用户，机构，菜单信息放入会话中
		session.setAttribute(TDataDictConst.MENU_LIST, list);
		session.setAttribute(TDataDictConst.OPERATOR_INF, tOperatorInf);
		session.setAttribute(TDataDictConst.INS_INF, tInsInf);
		if(session.getAttribute("resourceBundle")==null){//如果没有选择语言则用默认的语言
			Locale locale = Locale.SIMPLIFIED_CHINESE;
			ResourceBundle rb = ResourceBundle.getBundle("messageResource", locale);
			ActionContext.getContext().getSession().put("WW-TRANS-I18N-LOCALE", locale);
			ActionContext.getContext().getSession().put("resourceBundle", rb);
		}
		return Action.SUCCESS;
	}
}
