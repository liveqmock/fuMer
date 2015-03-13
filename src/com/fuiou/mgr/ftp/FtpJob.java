package com.fuiou.mgr.ftp;

import java.io.File;
import java.util.List;

public abstract class FtpJob{
    
    public void doJob(){
        FileScanner fileScanner = createFileScanner();
        List<File> files = fileScanner.getFiles();
        if(files!=null){
            for(File file: files){
                FileProcessor fileProcessor = createFileProcessor();
                fileProcessor.setFile(file);
                fileProcessor.setBusiCd();
                fileProcessor.process();
            }
        }
    }
    
    public abstract FileScanner createFileScanner();
    
    public abstract FileProcessor createFileProcessor();
    
}
