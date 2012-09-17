
package com.br.simplemq;

/**
 *
 * @author dalmir
 */
public interface SocketHandlerEventListener {
    
    public void onConnect(SocketHandler socket);
    
    public void onDisconnect(SocketHandler socket);
}
