package com.fuiou.mgr.action.sysmng;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TPmsBankAlias;
import com.fuiou.mer.service.TPmsBankAliasService;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BranchBankAliasDetailAction extends ActionSupport {
    private static final long serialVersionUID = 6876601479655308740L;
	private String id;

	public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String execute() throws Exception {
        ActionContext context = ActionContext.getContext();
        HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        TPmsBankAliasService tPmsBankAliasService = new TPmsBankAliasService();
        Integer idInt = null;
        try{
            idInt = Integer.valueOf(id);
        }catch(Exception e){
            addActionError("支行别名修改失败，标识有误");
            return Action.ERROR;
        }
        TPmsBankAlias tPmsBankAlias = tPmsBankAliasService.selectByPrimaryKey(idInt);
        if(tPmsBankAlias==null){
            addActionError("支行别名修改失败，没有该记录");
            return Action.ERROR;
        }
        request.setAttribute("tPmsBankAlias", tPmsBankAlias);
		return Action.SUCCESS;
	}
}
