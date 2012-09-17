
package com.br.simplemq;

import java.io.IOException;

/**
 *
 * @author dalmir
 */
public class Server {

    private int port;
    ConnectionListener connectionListener;
    private boolean isListening = false;

    public Server(int port) {
        this.port = port;
    }

    public void listen() {
        listen(port);
    }

    public void listen(int port) {
        System.out.println("Listening on port: " + port);
        connectionListener = new ConnectionListener(port);
        connectionListener.start();
        isListening = true;
    }
    
    public void stop() {
        if (connectionListener != null) {
            try {
                connectionListener.shutdown();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            connectionListener = null;
            isListening = false;
        }
    }

    public boolean isIsListening() {
        return isListening;
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }
}