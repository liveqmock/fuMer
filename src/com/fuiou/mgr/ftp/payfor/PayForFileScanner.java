package com.fuiou.mgr.ftp.payfor;

import java.util.regex.Pattern;

import com.fuiou.mgr.ftp.FileScanner;

public class PayForFileScanner extends FileScanner{
    private static Pattern pattern = Pattern.compile("^AP01_\\d{8}_[a-zA-z0-9]+\\.txt$");
    
    public PayForFileScanner(){
        setPattern(pattern);
    }
}
