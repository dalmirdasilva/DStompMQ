package com.br.dstompmq;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author dalmir
 */
public class QueueManager extends Thread {

    private static QueueManager instance;
    private Map<String, Subscription> subscriptions;
    private Map<String, Queue<Message>> queues;
    private static final long CONSUMING_TIMEOUT = (60 * 1000) / 6;

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

        System.out.println("Queue manager just starts to run!");
        
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

    public void offer(String queueName, Message message) {
        System.out.println("A message was offered to the manager:" + message.getContent());
        Queue<Message> queue = queues.get(queueName);
        if (queue == null) {
            queue = createQueue();
            addQueue(queueName, queue);
        }
        synchronized (queues) {
            queue.offer(message);
        }
    }

    public void ack(String subscriptionId) {
        System.out.println("Acking a message from subscription: " + subscriptionId);
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            subscription.reset();
        }
    }

    public void nack(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            Queue queue = subscription.getQueue();
            Message message = subscription.getLastMessage();
            if (queue != null && message != null) {
                queue.offer(message);
                subscription.reset();
            }
        }
    }

    public boolean addSubscription(String subscriptionId, String queueName, QueueListener subscriber) {
        System.out.println("Adding a subscriber to the queue: " + queueName + " with id: " + subscriptionId);
        if (subscriptionId == null || queueName == null || subscriber == null) {
            System.out.println("subscriptionId: " + subscriptionId);
            System.out.println("queueName: " + queueName);
            System.out.println("subscriber: " + subscriber);
            
            return false;
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
            return true;
        }
        return false;
    }

    public void removeSubscription(String subscriptionId) {
        System.out.println("Removed a subscriber with id: " + subscriptionId);
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            synchronized (subscriptions) {
                subscriptions.remove(subscriptionId);
            }
        }
    }

    private void addQueue(String name, Queue<Message> queue) {
        System.out.println("A queue was added.");
        synchronized (queues) {
            queues.put(name, queue);
        }
    }

    private Queue<Message> createQueue() {
        return new MemoryQueue<Message>();
    }

    private void monitorQueues() throws IOException {
        System.out.println("Monitoring queues...");
        System.out.println(subscriptions);
        for (Iterator<Entry<String, Subscription>> it = subscriptions.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Subscription> entry = it.next();
            Subscription subscription = entry.getValue();
            System.out.println("subscription: " + subscription);
            if (subscription.isIsConsuming()) {
                System.out.println("is consuming");
                verifySubscriptionTimeout(subscription);
            } else {
                System.out.println("isn't consuming");
                tryDeliverTo(subscription);
            }
        }
    }

    public void removeQueue(String queueName) {
        Queue queue = queues.get(queueName);
        if (queue != null) {
            removeQueueSubscriptions(queue);
            synchronized (queues) {
                queues.remove(queueName);
            }
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

    private void verifySubscriptionTimeout(Subscription subscription) {
        System.out.println("verifying timeouts");
        if (subscription.isIsConsuming()) {
            long lastTransmissionTime = subscription.getLastTransmissionDate().getTime();
            long now = new Date().getTime();
            if (lastTransmissionTime < (now - CONSUMING_TIMEOUT)) {
                System.out.println("TIMEOUT");
                Message message = subscription.getLastMessage();
                if (message == null) {
                    return;
                }
                Queue<Message> queue = subscription.getQueue();
                if (queue != null) {
                    queue.offer(message);
                }
                subscription.reset();
            }
        }
    }

    private void tryDeliverTo(Subscription subscription) throws IOException {
        System.out.println("trying to deliver");
        Queue<Message> queue = subscription.getQueue();
        if (queue != null && queue.size() > 0) {
            System.out.println("queue isn't null and the size is > 0");
            QueueListener listener = subscription.getListener();
            if (listener != null) {
                System.out.println("listener isn't null");
                Message message = queue.poll();
                if (message != null) {
                    System.out.println("message isn't null, SENDING");
                    listener.message(message);
                    subscription.startConsumingMessage(message);
                }
            }
        }
    }
}
