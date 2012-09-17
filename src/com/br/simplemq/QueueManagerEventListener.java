
package com.br.simplemq;

/**
 *
 * @author dalmir
 */
public interface QueueManagerEventListener {
    
    public void onAddQueue(String name);
    
    public void onRemoveQueue(String name);
}
