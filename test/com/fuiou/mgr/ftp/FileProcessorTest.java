package com.fuiou.mgr.ftp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuiou.mer.util.TDataDictConst;

public class FileProcessorTest{
    Logger logger = LoggerFactory.getLogger(FileProcessorTest.class);
    File file = new File("D:/fuiou/ftp/00011200M000000/upload/AP01_20110817_0003.txt");
    
    public void moveToHistoryDir(){
        if(file==null){
            logger.error("src file null");
        }
        File destDir = new File(file.getParentFile().getParentFile(), TDataDictConst.FTP_DIR_UPLOAD_HIS);
        File destFile = new File(destDir, file.getName());
        int i=1;
        while(destFile.exists()){
            i++;
            destFile = new File(destDir, file.getName()+"."+String.valueOf(i));
        }
        try{
            FileUtils.moveFile(file, destFile);
        }catch(IOException e){
            logger.error("move file failed", e);
        }
    }

    @Before
    public void setUp() throws Exception{
    }

    @Test
    public void testMoveToHistoryDir(){
        moveToHistoryDir();
    }
    
//    @Test
//    public void testInsertToDb(){
//        FileProcessor pf = new PayForFileProcessor();
//        // pf.setFileErrInfs(fileErrInfs)
//        // pf.setFileInf(fileInf)
//        // pf.settApsTxnLogs(tApsTxnLogs)
//        //
//        // 错误信息集
//        List<TFileErrInf> tfileErrInfs = new ArrayList();
//        TFileErrInf tFileErrInf = new TFileErrInf();
//        tFileErrInf.setFILE_NM("test3");
//        tfileErrInfs.add(tFileErrInf);
//
//        // 正确信息集
//        List<TApsTxnLog> tApsTxnLogs = new ArrayList();
//        TApsTxnLog tApsTxnLog = new TApsTxnLog();
//        tApsTxnLog.setKBPS_SRC_SETTLE_DT("20110106");
//        tApsTxnLog.setSRC_MODULE_CD("CPS");
//        tApsTxnLog.setKBPS_TRACE_NO("101706004396");
//        tApsTxnLog.setSUB_TXN_SEQ((short) 0);
//        tApsTxnLogs.add(tApsTxnLog);
//
//        // 汇总行信息
//        TFileInf tfileInf = new TFileInf();
//
//        tfileInf.setFILE_NM("test3");
//        tfileInf.setFILE_MCHNT_CD("test3");
//        tfileInf.setFILE_DT("test3");
//        tfileInf.setFILE_BUSI_TP("test");
//        tfileInf.setFILE_SEQ("4");
//        tfileInf.setFILE_NM_SFFX("5a");
//        tfileInf.setFILE_PATH("6");
//        tfileInf.setFILE_SIZE(1);
//        tfileInf.setFILE_AMT(2l);
//        tfileInf.setFILE_ROWS(3);
//        tfileInf.setFILE_RIGHT_AMT(2l);
//        tfileInf.setFILE_RIGHT_ROWS(3);
//        tfileInf.setFILE_ST((short) 5);
//        tfileInf.setFILE_RSLT_NM("1");
//        tfileInf.setFILE_RSLT_ST((short) 3);
//        tfileInf.setERR_DESC("1");
//        tfileInf.setOPR_USR_ID("2");
//        tfileInf.setROW_CRT_TS(new Date());
//        tfileInf.setREC_UPD_TS(new Date());
//
//        pf.setFileErrInfs(tfileErrInfs);
//        pf.settApsTxnLogs(tApsTxnLogs);
//        pf.setFileInf(tfileInf);
//
//        pf.insertToDb();
//
//    }

}
