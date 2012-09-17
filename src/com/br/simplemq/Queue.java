
package com.br.simplemq;

import java.util.Iterator;

/**
 *
 * @author dalmir
 */
public interface Queue<T> {
    public void offer(T element);
    public T poll();
    public int size();
    public Iterator<T> iterator();
}
