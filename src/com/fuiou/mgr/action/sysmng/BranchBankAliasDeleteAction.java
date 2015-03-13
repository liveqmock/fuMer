package com.fuiou.mgr.action.sysmng;

import com.fuiou.mer.service.TPmsBankAliasService;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class BranchBankAliasDeleteAction extends ActionSupport {
    private static final long serialVersionUID = 6876601479655308740L;
	private String id;

	public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String execute() throws Exception {
        TPmsBankAliasService tPmsBankAliasService = new TPmsBankAliasService();
        Integer idInt = null;
        try{
            idInt = Integer.valueOf(id);
        }catch(Exception e){
            addActionError("支行别名删除失败，标识有误");
            return Action.SUCCESS;
        }
        int result = tPmsBankAliasService.deleteByPrimaryKey(idInt);
        if(result==0){
            addActionError("支行别名删除失败，没有该记录");
            return Action.SUCCESS;
        }
        addActionMessage("支行别名删除成功");
		return Action.SUCCESS;
	}
}
