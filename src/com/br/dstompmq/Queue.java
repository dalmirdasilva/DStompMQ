
package com.br.dstompmq;

/**
 *
 * @author dalmir
 */
public interface Queue<T> {
    public void offer(T element);
    public T poll();
    public int size();
}
