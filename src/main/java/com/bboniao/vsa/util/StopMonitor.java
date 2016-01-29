
package com.bboniao.vsa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class StopMonitor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(StopMonitor.class);
    private ServerSocket socket;

    public StopMonitor(int port) {
        setDaemon(true);
        setName("StopMonitor");
        try {
            socket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        logger.info("stop monitor thread listening on: {}", socket);
        Socket accept;
        try {
            accept = socket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            reader.readLine();
            logger.info("stop signal received, stopping server");
            accept.close();
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
