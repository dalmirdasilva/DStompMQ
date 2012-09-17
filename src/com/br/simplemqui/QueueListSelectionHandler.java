package com.br.simplemqui;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author dalmir
 */
class QueueListSelectionHandler implements ListSelectionListener {

    SimpleMQUI ui;
    int currentIndex;

    public QueueListSelectionHandler(SimpleMQUI ui) {
        this.ui = ui;
        currentIndex = 0;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        DefaultListSelectionModel lsm = (DefaultListSelectionModel) e.getSource();
        DefaultListModel lm = (DefaultListModel) ui.getQueueList().getModel();

        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        
        boolean isAdjusting = e.getValueIsAdjusting();

        if (lsm.isSelectionEmpty()) {
        } else {

            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            
            if (currentIndex != minIndex) {
                if (lsm.isSelectedIndex(minIndex)) {
                    String queueName = null;
                    if (!lsm.isSelectionEmpty()) {
                        queueName = (String) lm.get(minIndex);
                        ui.refreshQueueData(queueName);
                    }
                }
                currentIndex = minIndex;
            }
        }
    }
}
