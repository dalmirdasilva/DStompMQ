package com.br.dstompmq;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author dalmir
 */
public class ConnectionListener extends Thread {

    private ServerSocket serverSocket;
    private int port;
    private List<SocketHandler> handlers;

    ConnectionListener(int port) {
        this.port = port;
        handlers = new ArrayList<SocketHandler>();
    }

    @Override
    public void run() {
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        while (!isInterrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();
                SocketHandler handler = new SocketHandler(clientSocket);
                handler.start();
                handlers.add(handler);
            } catch (SocketException e) {
                e.printStackTrace(System.err);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        try {
            shutdown();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void shutdown() throws IOException {
        interrupt();
        serverSocket.close();
        for (Iterator i = handlers.iterator(); i.hasNext();) {
            try {
                shutdownSocketHandler((SocketHandler) i.next());
            } catch (Exception e) {
            }
        }
    }
    
    private void shutdownSocketHandler(SocketHandler handler) throws IOException {
        handler.shutdown();
        synchronized (handlers) {
            handlers.remove(handler);
        }
    }

    public List<SocketHandler> getHandlers() {
        return handlers;
    }
}
