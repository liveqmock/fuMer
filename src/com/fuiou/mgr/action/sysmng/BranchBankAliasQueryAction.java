package com.fuiou.mgr.action.sysmng;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.fuiou.mer.model.TPmsBankAlias;
import com.fuiou.mer.service.TPmsBankAliasService;
import com.fuiou.mgr.util.page.PagerUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BranchBankAliasQueryAction extends ActionSupport {
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
        ActionContext context = ActionContext.getContext();
        HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        TPmsBankAliasService tPmsBankAliasService = new TPmsBankAliasService();
        int totalCount = tPmsBankAliasService.selectCountByExample(branchBankAlias, branchBankName);
        if (totalCount == 0) {
            request.setAttribute("msg", "未查询到数据!");
            request.setAttribute("totalCount", totalCount);
            return Action.SUCCESS;
        }
        int[] pageInfo = PagerUtil.getStartEndIndex(request, totalCount);
        List<TPmsBankAlias> list = tPmsBankAliasService.selectByPmsBankAliasAndNm(branchBankAlias, branchBankName, pageInfo[0], pageInfo[1]);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("aliasList", list);
		return Action.SUCCESS;
	}
}
