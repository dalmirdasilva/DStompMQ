
package com.br.dstompmq;

/**
 *
 * @author dalmir
 */
public class Message {
    
    private static int counter = 0;
    
    private int id;
    private boolean visible;
    private Object content;

    public Message(Object content) {
        this.content = content;
        id = counter++;
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
}
