package com.fuiou.mgr.util;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorThread extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(MonitorThread.class);
	public String name;

	public MonitorThread(String name) {
		super();
		this.name = name;
	}

	@Override
	public void run() {
		try {
			while(true){
				Map<String, AtomicInteger> map = HttpThreadConfigUtil.getLockMap();
				int sum = 0;
				if(map.size()>0){
					for(String entry:map.keySet()){
						sum = sum + map.get(entry).get();
						logger.info("==="+entry+" current http thread number is "+map.get(entry).get()+"===");
					}
				}
				logger.info("=== current http thread sum number is "+sum+"===");
				currentThread().sleep(2*60*1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
}
