package com.fuiou.mgr.ftp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.TDataDictConst;

public class FileSigner{
    private static Logger logger = LoggerFactory.getLogger(FileSigner.class);
    private static int SIGN_LENGTH = 32;
    
    public static void sign(File file, String mchntCd, String pwd){
        if(file==null || mchntCd==null || pwd==null){
            logger.error("sign params error");
            return;
        }
        if(!file.exists()){
            logger.error("file to be signed not exist");
            return;
        }
        List<String> lines = null;
        try{
            lines = FileUtils.readLines(file, TDataDictConst.FILE_CODE);
        }catch(IOException e){
            logger.error("sign file error", e);
            return;
        }
        StringBuffer stringBuffer = getTrimedNotEmptyLinesToString(lines);
        stringBuffer.append(mchntCd).append(pwd);
//        String sign = MD5Util.encode(stringBuffer.toString(), TDataDictConst.FILE_CODE);
        String sign;
        try{
            sign = DigestUtils.md5Hex(stringBuffer.toString().getBytes(TDataDictConst.FILE_CODE));
        }catch(UnsupportedEncodingException e){
            logger.error("sign file error", e);
            return;
        }
        String sign2 = System.getProperty("line.separator") + sign;
        RandomAccessFile randomAccessFile = null;
        try{
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(sign2.getBytes(TDataDictConst.FILE_CODE));
        }catch(Exception e){
            logger.error("sign file error", e);
        }finally{
            if(randomAccessFile != null){
                try{
                    randomAccessFile.close();
                }catch(IOException e){
                    logger.error("file close error", e);
                }
            }
        }
    }
    
    public static boolean verifySign(File file, String mchntCd, String pwd){
        if(file==null || mchntCd==null || pwd==null){
            logger.error("verify sign params error");
            return false;
        }
        if(!file.exists()){
            logger.error("file to be verified not exist");
            return false;
        }
        List<String> lines = null;
        try{
            lines = FileUtils.readLines(file, TDataDictConst.FILE_CODE);
        }catch(IOException e){
            logger.error("verify file sign error", e);
            return false;
        }
        StringBuffer stringBuffer = getTrimedNotEmptyLinesToString(lines);
        if(stringBuffer.length() < SIGN_LENGTH){
            logger.error("file to be verified is too short");
            return false;
        }
        String toSign = stringBuffer.substring(0, stringBuffer.length()-SIGN_LENGTH);
        String sign = stringBuffer.substring(stringBuffer.length()-SIGN_LENGTH);
//        String sign2 = MD5Util.encode(toSign+mchntCd+pwd, TDataDictConst.FILE_CODE);
        String sign2;
        try{
            sign2 = DigestUtils.md5Hex((toSign+mchntCd+pwd).getBytes(TDataDictConst.FILE_CODE));
        }catch(UnsupportedEncodingException e){
            logger.error("verify file sign error", e);
            return false;
        }
        if(sign.equals(sign2)){
            return true;
        }else{
            logger.info("file invalid sign");
            return false;
        }
    }
    
    public static List<String> getTrimedNotEmptyLines(List<String> lines){
        List<String> trimedNotEmptyLines = new ArrayList<String>();
        for(String line: lines){
            if(line.trim().length()!=0){
                trimedNotEmptyLines.add(line);
            }
        }
        return trimedNotEmptyLines;
    }
    
    public static StringBuffer getTrimedNotEmptyLinesToString(List<String> lines){
        StringBuffer stringBuffer = new StringBuffer();
        for(String line: lines){
            if(line.trim().length()!=0){
                stringBuffer.append(line);
            }
        }
        return stringBuffer;
    }
}
