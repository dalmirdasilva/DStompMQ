
package com.br.dstompmq;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author dalmir
 */
public class MemoryQueue<T> implements Queue<T> {

    private ArrayBlockingQueue<T> queue;

    public MemoryQueue() {
        this.queue = new ArrayBlockingQueue<T>(100);
    }
    
    @Override
    public void offer(T element) {
        System.out.println("A element was offered to the queue:" + element);
        queue.offer(element);
    }
    
    @Override
    public T poll() {
        T element = queue.poll();
        System.out.println("A element was polled from the queue:" + element);
        return element;
    }

    @Override
    public int size() {
        return queue.size();
    }
}
