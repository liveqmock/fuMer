package com.fuiou.mgr.http.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketServer implements Runnable {
	ServerSocket socket;
	int port;
	boolean isStop;
	// 线程池
	private ExecutorService executorService; 
	
	public SocketServer(int port) {
		this.port = port;
		this.isStop = false;
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1, Executors.defaultThreadFactory());
		
        Thread aThread = new Thread(this);
        aThread.setName("Listen thread listen at port: " + port);
        aThread.start();
	}

	@Override
	public void run() {
        try
        {
            socket = new ServerSocket(port);
        }
        catch(Exception e)
        {
            return;
        }
        while(!isStop) 
        {
            try
            {
                java.net.Socket aSocket = socket.accept();
                if(aSocket != null)
                {
                	executorService.execute(new OrderPayWorker(aSocket));
                }
                continue;
            }
            catch(IOException e)
            {
            }
            break;
        }
	}

    public void stop()
    {
        isStop = true;
        try
        {
            socket.close();
        }
        catch(Exception exception) { }
    }

}
