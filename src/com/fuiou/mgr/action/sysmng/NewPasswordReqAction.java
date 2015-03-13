package com.fuiou.mgr.action.sysmng;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TOperatorInf;
import com.fuiou.mer.service.TOperatorInfService;
import com.fuiou.mer.util.TDataDictConst;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class NewPasswordReqAction extends ActionSupport {

    private static final long serialVersionUID = 6876601479655308740L;

	private String originPassword;
	private String newPassword;
	private String renewPassword;

	public String getOriginPassword(){
        return originPassword;
    }

    public void setOriginPassword(String originPassword){
        this.originPassword = originPassword;
    }

    public String getNewPassword(){
        return newPassword;
    }

    public void setNewPassword(String newPassword){
        this.newPassword = newPassword;
    }

    public String getRenewPassword(){
        return renewPassword;
    }

    public void setRenewPassword(String renewPassword){
        this.renewPassword = renewPassword;
    }

    public String execute() throws Exception {
        ActionContext context = ActionContext.getContext();
        HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        HttpSession session = request.getSession();
        TOperatorInf tOperatorInf = (TOperatorInf)session.getAttribute(TDataDictConst.OPERATOR_INF);
//        String orgPwd = MD5Util.encode(originPassword, TDataDictConst.DB_CHARSET);
        String orgPwd = DigestUtils.md5Hex(originPassword.getBytes(TDataDictConst.DB_CHARSET));
        if(!orgPwd.equals(tOperatorInf.getLOGIN_PWD())){
            //addActionMessage("密码不正确");
            addFieldError("originPassword","密码不正确");
            return Action.INPUT;
        }
//        String toLoginPwd = MD5Util.encode(newPassword, TDataDictConst.DB_CHARSET);
        String toLoginPwd = DigestUtils.md5Hex(newPassword.getBytes(TDataDictConst.DB_CHARSET));
        TOperatorInfService tOperatorInfService = new TOperatorInfService();
        int rows = tOperatorInfService.updateLoginPwdByMchntCdAndLoginId(toLoginPwd, tOperatorInf.getMCHNT_CD(), tOperatorInf.getLOGIN_ID());
        if(rows==0){
            addActionMessage("密码修改失败");
            return Action.INPUT;
        }
        
        tOperatorInf.setLOGIN_PWD(toLoginPwd);
		return Action.SUCCESS;
	}
}
