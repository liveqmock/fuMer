package com.fuiou.mgr.checker;

import java.io.File;

import com.fuiou.mer.util.TDataDictConst;
import com.fuiou.mgr.ftp.FileSigner;

/**
 * 签名校验器
 * yangliehui
 *
 */
public class SignChecker {
	public static String signCheck(String key,File file,String mchntCd){
        if(null == key || !FileSigner.verifySign(file, mchntCd, key)){
            return TDataDictConst.FIlE_SIGNER_ER;
        }
        return null;
	}
}
