
package com.br.dstompmq;

import java.io.IOException;

/**
 *
 * @author dalmir
 */
public interface QueueListener {
    
    public void message(Message message) throws IOException;
}
