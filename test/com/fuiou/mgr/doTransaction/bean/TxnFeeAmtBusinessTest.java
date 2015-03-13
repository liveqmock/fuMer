package com.fuiou.mgr.doTransaction.bean;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fuiou.mer.model.TCardBin;
import com.fuiou.mgr.bean.business.InComeForBusiDetailRowBean;
import com.fuiou.mgr.bean.business.PayForBusiDetailRowBean;
import com.fuiou.mgr.doTransaction.Access.ForAccessTxnFeeAmtRutBean;
import com.fuiou.mgr.doTransaction.Access.TxnFeeAmtBusinessAccess;

public class TxnFeeAmtBusinessTest{
	TxnFeeAmtBusinessAccess txnFeeAmtBusiness;

    @Before
    public void setUp() throws Exception{
    	txnFeeAmtBusiness = new TxnFeeAmtBusinessAccess();
    }

    @Test
    public void testGetFiles(){
    	ForAccessTxnFeeAmtRutBean forTxnFeeAmtBean=new ForAccessTxnFeeAmtRutBean();
    	List incomeList = new ArrayList();
    	List payList = new ArrayList();
    	forTxnFeeAmtBean.setMchntCd("");
    	forTxnFeeAmtBean.setBusiCd("");
    	forTxnFeeAmtBean.setCurrDbTime(new Date());
    	InComeForBusiDetailRowBean inComeForBusiDetailRowBean= new InComeForBusiDetailRowBean();
    	inComeForBusiDetailRowBean.setDetailAmt(0);
    	inComeForBusiDetailRowBean.setBankCd("");
    	TCardBin tCardBin = new TCardBin();
    	tCardBin.setCARD_ATTR("");
    	inComeForBusiDetailRowBean.getOperationBean().settCardBin(tCardBin);
    	incomeList.add(inComeForBusiDetailRowBean);
    	PayForBusiDetailRowBean payForBusiDetailRowBean = new PayForBusiDetailRowBean();
    	payForBusiDetailRowBean.setDetailAmt(0);
    	payForBusiDetailRowBean.setBankCd("");
    	payForBusiDetailRowBean.setProvCode("");
    	payForBusiDetailRowBean.setCityCode("");
    	payList.add(payForBusiDetailRowBean);
    	forTxnFeeAmtBean.setInComeForList(incomeList);
    	forTxnFeeAmtBean.setPayForList(payList);
    	forTxnFeeAmtBean = txnFeeAmtBusiness.doTxnFeeAmtBusiness(forTxnFeeAmtBean);
        assertNotNull(forTxnFeeAmtBean);
    }

}
