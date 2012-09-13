/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.br.dstompmq;

/**
 *
 * @author dalmir
 */
public class Subscription {

    private Queue queue;
    private QueueListener listener;
    private Message lastMessage;
    private boolean isConsuming;

    public Subscription(QueueListener listener, Queue queue) {
        this.listener = listener;
        this.queue = queue;
        isConsuming = false;
    }

    public QueueListener getListener() {
        return listener;
    }

    public void setListener(QueueListener listener) {
        this.listener = listener;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public boolean isIsConsuming() {
        return isConsuming;
    }

    public void setIsConsuming(boolean isConsuming) {
        this.isConsuming = isConsuming;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public void dispose() {
        isConsuming = false;
        lastMessage = null;
    }

    void startConsumingMessage(Message message) {
        isConsuming = true;
        lastMessage = message;
    }
}