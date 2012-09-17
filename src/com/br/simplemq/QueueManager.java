package com.br.simplemq;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author dalmir
 */
public class QueueManager extends Thread {

    private static QueueManager instance;
    private Map<String, Subscription> subscriptions;
    private Map<String, Queue<Message>> queues;
    private static List<QueueManagerEventListener> eventListeners;
    
    static {
        eventListeners = new ArrayList<QueueManagerEventListener>();
    }
    
    public static void addEventListener(QueueManagerEventListener listener) {
        eventListeners.add(listener);
    }
    
    private static void sendAddQueueEvent(String name) {
        for (QueueManagerEventListener listener : eventListeners) {
            listener.onAddQueue(name);
        }
    }
    
    private static void sendRemoveQueueEvent(String name) {
        for (QueueManagerEventListener listener : eventListeners) {
            listener.onRemoveQueue(name);
        }
    }    
    
    private QueueManager() {
        subscriptions = new HashMap<String, Subscription>();
        queues = new HashMap<String, Queue<Message>>();
    }

    public static QueueManager getInstance() {
        if (instance == null) {
            instance = new QueueManager();
            instance.start();
        }
        return instance;
    }

    @Override
    public void run() {

        while (!isInterrupted()) {

            try {
                monitorQueues();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void addMessage(String queueName, Message message) throws QueueOperationException {
        Queue<Message> queue = queues.get(queueName);
        if (queue == null) {
            queue = createQueue();
            addQueue(queueName, queue);
        }
        synchronized (queues) {
            queue.offer(message);
        }
    }

    public void ackMessage(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            subscription.dispose();
        }
    }

    public void nakcMessage(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            Queue queue = subscription.getQueue();
            Message message = subscription.getLastMessage();
            if (queue != null && message != null) {
                if (!message.isMaxRedeliveriesReached()) {
                    queue.offer(message);
                }
                subscription.dispose();
            }
        }
    }

    public void addSubscription(String subscriptionId, String queueName, QueueListener subscriber) throws QueueOperationException {
        if (subscriptionId == null || queueName == null || subscriber == null) {
            throw new QueueOperationException("Null value not acceptable.");
        }
        Subscription subscription = subscriptions.get(subscriptionId);
        Queue<Message> queue = queues.get(queueName);
        if (subscription == null) {
            if (queue == null) {
                queue = createQueue();
                addQueue(queueName, queue);
            }
            subscription = new Subscription(subscriber, queue);
            subscriptions.put(subscriptionId, subscription);
        } else {
            throw new QueueOperationException("Subscription ID already exists.");
        }
    }

    public void removeSubscription(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            synchronized (subscriptions) {
                subscriptions.remove(subscriptionId);
            }
        }
    }

    private void addQueue(String name, Queue<Message> queue) throws QueueOperationException {
        if (name == null || queue == null) {
            throw new QueueOperationException("Null value not acceptable.");
        }
        synchronized (queues) {
            queues.put(name, queue);
        }
        sendAddQueueEvent(name);
    }

    private Queue<Message> createQueue() {
        return new MemoryQueue<Message>();
    }

    private void monitorQueues() throws IOException {
        for (Iterator<Entry<String, Subscription>> it = subscriptions.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Subscription> entry = it.next();
            Subscription subscription = entry.getValue();
            if (subscription.isIsConsuming()) {
                verifyTimeout(subscription);
            } else {
                tryDeliver(subscription);
            }
        }
    }

    public void removeQueue(String name) {
        Queue queue = queues.get(name);
        if (queue != null) {
            removeQueueSubscriptions(queue);
            synchronized (queues) {
                queues.remove(name);
            }
            sendRemoveQueueEvent(name);
        }
    }

    private void removeQueueSubscriptions(Queue<Message> queue) {
        // TODO
    }

    public void stopManaging() {
        interrupt();
        subscriptions = new HashMap<String, Subscription>();
        queues = new HashMap<String, Queue<Message>>();
    }

    private void verifyTimeout(Subscription subscription) {
        if (subscription.isIsConsuming()) {
            Message message = subscription.getLastMessage();
            if (message.isConsumingTimeouted()) {
                if (!message.isMaxRedeliveriesReached()) {
                    Queue<Message> queue = subscription.getQueue();
                    if (queue != null) {
                        queue.offer(message);
                    }
                }
                subscription.dispose();
            }
        }
    }

    private void tryDeliver(Subscription subscription) throws IOException {
        Queue<Message> queue = subscription.getQueue();
        if (queue != null && queue.size() > 0) {
            QueueListener listener = subscription.getListener();
            if (listener != null) {
                Message message = queue.poll();
                if (message != null) {
                    listener.message(message);
                    subscription.startConsumingMessage(message);
                    message.justDelivered();
                }
            }
        }
    }

    public Map<String, Queue<Message>> getQueues() {
        return queues;
    }

    public Map<String, Subscription> getSubscriptions() {
        return subscriptions;
    }
}
