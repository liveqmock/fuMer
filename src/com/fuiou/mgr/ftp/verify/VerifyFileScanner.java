package com.fuiou.mgr.ftp.verify;

import java.util.regex.Pattern;

import com.fuiou.mgr.ftp.FileScanner;

public class VerifyFileScanner extends FileScanner{
    private static Pattern pattern = Pattern.compile("^YZ01_\\d{8}_[a-zA-z0-9]+\\.txt$");
    
    public VerifyFileScanner(){
        setPattern(pattern);
    }

}
