package com.br.simplemqui;

import com.br.simplemq.QueueManagerEventListener;
import javax.swing.DefaultListModel;

/**
 *
 * @author dalmir
 */
class QueueManagerEventHandler implements QueueManagerEventListener {

    SimpleMQUI ui;

    public QueueManagerEventHandler(SimpleMQUI ui) {
        this.ui = ui;
    }

    @Override
    public void onAddQueue(String name) {
        DefaultListModel lm = (DefaultListModel) ui.getQueueList().getModel();
        lm.addElement(name);
    }

    @Override
    public void onRemoveQueue(String name) {
        DefaultListModel lm = (DefaultListModel) ui.getQueueList().getModel();
        lm.removeElement(name);
    }
}
