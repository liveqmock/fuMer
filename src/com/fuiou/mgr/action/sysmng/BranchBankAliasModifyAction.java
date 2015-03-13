package com.fuiou.mgr.action.sysmng;

import com.fuiou.mer.service.TPmsBankAliasService;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class BranchBankAliasModifyAction extends ActionSupport {
    private static final long serialVersionUID = 6876601479655308740L;
	private String id;
	private String branchBankAlias;
    private String branchBankName;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

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
        Integer idInt = null;
        try{
            idInt = Integer.valueOf(id);
        }catch(Exception e){
            addActionError("支行别名修改失败，标识有误");
            return Action.SUCCESS;
        }
        int result = tPmsBankAliasService.updateByPrimaryKeySelective(branchBankAlias, branchBankName, idInt);
        if(result==0){
            addActionError("支行别名修改失败，别名重复或没有该记录");
            return Action.SUCCESS;
        }
        addActionMessage("支行别名修改成功");
		return Action.SUCCESS;
	}
}
