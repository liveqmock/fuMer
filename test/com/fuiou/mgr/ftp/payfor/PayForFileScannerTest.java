package com.fuiou.mgr.ftp.payfor;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fuiou.mgr.ftp.FileError;
import com.fuiou.mgr.ftp.FileProcessor;
import com.fuiou.mgr.ftp.FileRowError;
import com.fuiou.mgr.ftp.FileSumError;
import com.fuiou.mgr.ftp.RowColumnError;

public class PayForFileScannerTest{
    PayForFileScanner payForFileScanner;

    @Before
    public void setUp() throws Exception{
        payForFileScanner = new PayForFileScanner();
    }

    @Test
    public void testGetFiles(){
        List<File> files = payForFileScanner.getFiles();
        assertNotNull(files);
    }
    @Test
    public void testCreateRejectFile(){
    	PayForFileProcessor fp=new PayForFileProcessor();
    	File file=new File("D:\\aaa\\upload\\wjm.txt");
    	
    	FileError fileError=new FileError();
    	List <FileRowError>FileRowErrors=new ArrayList();
    	FileRowError fileError1=new FileRowError();
    	FileRowError fileError2=new FileRowError();
    	FileRowError fileError3=new FileRowError();
    	
    	List <RowColumnError>RowColumnErrors=new ArrayList();
    	RowColumnError rowColumnError1=new RowColumnError();
    	RowColumnError rowColumnError2=new RowColumnError();
    	
    	rowColumnError1.setErrCode("010");
    	rowColumnError1.setErrMemo("北京");
    	
    	rowColumnError2.setErrCode("021");
    	rowColumnError2.setErrMemo("上海");
    	
    	RowColumnErrors.add(rowColumnError1);
    	RowColumnErrors.add(rowColumnError2);
    	
    	fileError1.setRowNo(2);
    
    	fileError1.setRow("fsadf|fsadf");
    	fileError1.setColumnErrors(RowColumnErrors);
    	
    	
    	fileError2.setRowNo(3);
    
    	fileError2.setRow("fsadf|fsadf");
    	fileError2.setColumnErrors(RowColumnErrors);
    	
    	fileError3.setRowNo(5);
    
    	fileError3.setRow("fsadf|fsadf");
    	fileError3.setColumnErrors(RowColumnErrors);
    
    	FileRowErrors.add(fileError1);
    	FileRowErrors.add(fileError2);
    	FileRowErrors.add(fileError3);
    	
    	FileSumError fileSumError =new FileSumError();
    	fileSumError.setErrCode("111");
    	fileSumError.setErrMemo("严重错");
    	fileSumError.setRow("dfasdfa|fdsaf|sdfsa");
    	
    	fileError.setFileRowErrors(FileRowErrors);
    	fileError.setFileSumError(fileSumError);
    	
    	fp.setFile(file);
//    	fp.setFileError(fileError);
    	//fp.setMerchantId("");

//    	fp.createRejectFile();
    	
    }

}
