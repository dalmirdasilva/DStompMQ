
package com.br.simplemq;

import java.util.Date;

/**
 *
 * @author dalmir
 */
public class Message {
    
    private static final long CONSUMING_TIMEOUT = (60 * 1000) / 6;
    private static int idSequencer = 0;
    
    private int id;
    private boolean visible;
    private Object content;
    private int deliveryCount;
    private int maxRedeliveries;
    private long lastDeliveryTime;
    private long consumingTimeout;
    
    public Message(Object content, int maxRedeliveries, long consumingTimeout) {
        this.content = content;
        this.deliveryCount = 0;
        this.maxRedeliveries = maxRedeliveries;
        this.consumingTimeout = consumingTimeout;
        this.id = getNextId();
    }

    public Message(Object content) {
        this(content, 0, CONSUMING_TIMEOUT);
    }

    public Message(Object content, int maxRedeliveries) {
        this(content, maxRedeliveries, CONSUMING_TIMEOUT);
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getDeliveryCount() {
        return deliveryCount;
    }

    public int getMaxRedeliveries() {
        return maxRedeliveries;
    }

    public long getLastDeliveryTime() {
        return lastDeliveryTime;
    }
    
    public boolean isMaxRedeliveriesReached() {
        return maxRedeliveries > 0 && deliveryCount >= maxRedeliveries;
    }
    
    public void increaseRedeliveryCount() {
        deliveryCount++;
    }
    
    public void justDelivered() {
        lastDeliveryTime = new Date().getTime();
        increaseRedeliveryCount();
    }
    
    public boolean isConsumingTimeouted() {
        long now = new Date().getTime();
        if (lastDeliveryTime < (now - consumingTimeout)) {
            return true;
        }
        return false;
    }
    
    private static int getNextId() {
        return idSequencer++;
    }
}
