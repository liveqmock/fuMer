package com.fuiou.mgr.ftp.payfor;

import com.fuiou.mgr.ftp.FileProcessor;
import com.fuiou.mgr.ftp.FileScanner;
import com.fuiou.mgr.ftp.FtpJob;

public class PayForFtpJob extends FtpJob{

    @Override
    public FileScanner createFileScanner(){
        return new PayForFileScanner();
    }
    
    @Override
    public FileProcessor createFileProcessor(){
        return new PayForFileProcessor();
    }
    
}
