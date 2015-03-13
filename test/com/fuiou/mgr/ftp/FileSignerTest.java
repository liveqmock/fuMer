package com.fuiou.mgr.ftp;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.Before;
import org.junit.Test;

import com.fuiou.mer.util.TDataDictConst;

public class FileSignerTest {
	File file = new File("D:/fuiou/ftp/00011200M000000/upload/AP01_20110817_0003.txt");
	String mchntCd = "00011200M000000";
	String pwd = "123456";
	String path = "D:/fuiou/ftp/";
	Pattern pattern = Pattern.compile("^[0-9a-zA-Z]{15}$");
	Pattern appattern = Pattern.compile("^AP01_\\d{8}_\\d{4}\\.txt$");

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSign() {
		FileSigner.sign(file, mchntCd, pwd);
	}

	@Test
	public void testVerifySign() {
		boolean verified = FileSigner.verifySign(file, mchntCd, pwd);
		assertTrue(verified);
	}

	@Test
	public void testVerifySignList() {
		File ftpRootDir = new File(path);
        if(!ftpRootDir.exists() || ftpRootDir.isFile()){
            return ;
        }
        IOFileFilter dirFileFilter = DirectoryFileFilter.DIRECTORY;
        IOFileFilter regexFileFilter = new RegexFileFilter(pattern);
        FilenameFilter filenameFilter = new AndFileFilter(dirFileFilter, regexFileFilter);
        File[] mchntDirs = ftpRootDir.listFiles(filenameFilter);
        if(mchntDirs == null || mchntDirs.length == 0){
        }
        for(int i=0; i<mchntDirs.length; i++){
            String mchntCd = mchntDirs[i].getName();
            //上传文件在商户目录的upload目录下
            File uploadDir = new File(mchntDirs[i], TDataDictConst.FTP_DIR_UPLOAD);
            if(!uploadDir.exists() || uploadDir.isFile()){
                continue;
            }
            IOFileFilter fileFileFilter = FileFileFilter.FILE;
            IOFileFilter regexFileFilter2 = new RegexFileFilter(appattern);
            FilenameFilter filenameFilter2 = new AndFileFilter(fileFileFilter, regexFileFilter2);
            File[] files = uploadDir.listFiles(filenameFilter2);
            for(File file :files){
            	 FileSigner.sign(file, mchntCd, pwd);
            }
        }
	}
}
