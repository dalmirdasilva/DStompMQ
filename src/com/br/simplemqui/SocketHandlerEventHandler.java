package com.br.simplemqui;

import com.br.simplemq.SocketHandler;
import com.br.simplemq.SocketHandlerEventListener;
import javax.swing.DefaultListModel;

/**
 *
 * @author dalmir
 */
class SocketHandlerEventHandler implements SocketHandlerEventListener {

    SimpleMQUI ui;

    SocketHandlerEventHandler(SimpleMQUI ui) {
        this.ui = ui;
    }

    @Override
    public void onConnect(SocketHandler socket) {
        DefaultListModel lm = (DefaultListModel) ui.getConnectionList().getModel();
        lm.addElement(socket);
    }

    @Override
    public void onDisconnect(SocketHandler socket) {
        DefaultListModel lm = (DefaultListModel) ui.getConnectionList().getModel();
        lm.removeElement(socket);
    }
}
