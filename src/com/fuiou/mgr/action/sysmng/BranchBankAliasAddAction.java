package com.fuiou.mgr.action.sysmng;

import com.fuiou.mer.service.TPmsBankAliasService;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class BranchBankAliasAddAction extends ActionSupport {
    private static final long serialVersionUID = 6876601479655308740L;
	private String branchBankAlias;
	private String branchBankName;

    public String getBranchBankAlias(){
        return branchBankAlias;
    }

    public void setBranchBankAlias(String branchBankAlias){
        this.branchBankAlias = branchBankAlias;
    }

    public String getBranchBankName(){
        return branchBankName;
    }

    public void setBranchBankName(String branchBankName){
        this.branchBankName = branchBankName;
    }

    public String execute() throws Exception {
        TPmsBankAliasService tPmsBankAliasService = new TPmsBankAliasService();
        int result = tPmsBankAliasService.insert(branchBankAlias, branchBankName);
        if(result==0){
            addActionError("支行别名新增失败，请检查是否存在重复别名或字段过长");
            return Action.INPUT;
        }
		return Action.SUCCESS;
	}
}
