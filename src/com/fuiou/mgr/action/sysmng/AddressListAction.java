package com.fuiou.mgr.action.sysmng;

import java.io.IOException;
import java.util.List;


import com.fuiou.mer.model.TAddressListInf;
import com.fuiou.mer.model.TPositionInf;
import com.fuiou.mer.service.TAddressListInfService;
import com.fuiou.mgr.action.BaseAction;

public class AddressListAction extends BaseAction {

	private static final long serialVersionUID = -4596027761244473036L;
	
	private TAddressListInfService addressListInfService = new TAddressListInfService();
	private List<TAddressListInf> list;
	private List<TPositionInf> positions;
	private TAddressListInf bean;
	private String msg;
	
	public List<TPositionInf> getPositions() {
		return positions;
	}
	public void setPositions(List<TPositionInf> positions) {
		this.positions = positions;
	}
	public TAddressListInf getBean() {
		return bean;
	}
	public void setBean(TAddressListInf bean) {
		this.bean = bean;
	}
	public List<TAddressListInf> getList() {
		return list;
	}
	public void setList(List<TAddressListInf> list) {
		this.list = list;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String findByMchntCd(){
		list = addressListInfService.findByMchntCd(tInsInf.getMCHNT_CD());
		positions = addressListInfService.findAllPositions();
		if(list!=null && list.size()>0){
			msg = list.get(0).getPUBLIC_EMAIL();
		}
		return "findByMchntCd";
	}
	
	public void addNew(){
		try {
			int rows = addressListInfService.add(bean, tOperatorInf);
			if(rows == 1){
				msg = "保存成功";
			}else{
				msg = "保存失败";
			}
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(msg);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateByPK(){
		try{
			int rows = addressListInfService.updateByPK(bean, tOperatorInf);
			if(rows == 1){
				msg = "保存成功";
			}else{
				msg = "保存失败";
			}
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(msg);
			response.getWriter().flush();
			response.getWriter().close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteByPK(){
		try{
			bean =  addressListInfService.findByRowId(bean.getROW_ID());
			if(bean.getMCHNT_CD().equals(tOperatorInf.getMCHNT_CD())){
				int i = addressListInfService.deleteByPK(bean.getROW_ID());
				if(i==1){
					msg = "删除成功";
				}else{
					msg = "删除失败";
				}
			}else{
				msg = "删除失败";
			}
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(msg);
			response.getWriter().flush();
			response.getWriter().close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setPublicMail(){
		try{
			int rows = addressListInfService.setPublicMail(msg, tOperatorInf);
			if(rows > 0){
				msg = "公共邮箱设置成功";
			}else{
				msg = "公共邮箱设置失败";
			}
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(msg);
			response.getWriter().flush();
			response.getWriter().close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
