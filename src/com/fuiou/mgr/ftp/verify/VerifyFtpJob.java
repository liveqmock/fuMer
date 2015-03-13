package com.fuiou.mgr.ftp.verify;

import com.fuiou.mgr.ftp.FileProcessor;
import com.fuiou.mgr.ftp.FileScanner;
import com.fuiou.mgr.ftp.FtpJob;

public class VerifyFtpJob extends FtpJob{

    @Override
    public FileScanner createFileScanner(){
        return new VerifyFileScanner();
    }
    
    @Override
    public FileProcessor createFileProcessor(){
        return new VerifyFileProcessor();
    }

}
