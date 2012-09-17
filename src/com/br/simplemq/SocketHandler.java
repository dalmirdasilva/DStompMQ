package com.br.simplemq;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.LoginException;

public class SocketHandler extends AbstractReceiver implements QueueListener, Authenticatable {

    private Transmiter transmiter;
    private Object clientToken;
    private boolean authenticated = false;
    Socket clientSocket;
    private static List<SocketHandlerEventListener> eventListeners;
    
    static {
        eventListeners = new ArrayList<SocketHandlerEventListener>();
    }
    
    public SocketHandler(Socket clientSocket) throws IOException {
        super(clientSocket.getInputStream());
        this.transmiter = new Transmiter(clientSocket.getOutputStream());
        this.clientSocket = clientSocket;
    }
    
    public static void addEventListener(SocketHandlerEventListener listener) {
        eventListeners.add(listener);
    }
    
    private static void sendConnectEvent(SocketHandler instance) {
        for (SocketHandlerEventListener listener : eventListeners) {
            listener.onConnect(instance);
        }
    }
    
    private static void sendDisconnectEvent(SocketHandler instance) {
        for (SocketHandlerEventListener listener : eventListeners) {
            listener.onDisconnect(instance);
        }
    }

    public void close() throws IOException {
        in.close();
        transmiter.close();
        clientSocket.close();
    }

    private void connect(FrameHeader headers) throws IOException {
        String login = (String) headers.get(FrameHeader.LOGIN_ENTRY_NAME);
        String passcode = (String) headers.get(FrameHeader.PASSCODE_ENTRY_NAME);
        try {
            clientToken = Authenticator.connect(login, passcode);
            FrameHeader header = new FrameHeader();
            header.put(FrameHeader.SESSION_ENTRY_NAME, String.valueOf(this.hashCode()));
            transmit(Command.CONNECTED, header, null);
            setAuthenticated(true);
            sendConnectEvent(this);
        } catch (LoginException e) {
            transmit(Command.ERROR, null, e.getMessage());
        }
    }

    private void disconnect(FrameHeader headers) throws IOException {
        if (headers != null) {
            String receipt = (String) headers.get(FrameHeader.RECEIPT_ENTRY_NAME);
            if (receipt != null) {
                FrameHeader header = new FrameHeader();
                header.put(FrameHeader.RECEIPT_ID_ENTRY_NAME, receipt);
                transmit(Command.RECEIPT, header, null);
            }
            sendDisconnectEvent(this);
        }
    }

    private void handleFrame(Frame frame) throws IOException {

        QueueManager queueManager = QueueManager.getInstance();
        Command command = frame.getCommand();
        FrameHeader headers = frame.getHeader();

        try {
            if (command == Command.COMMIT || command == Command.ABORT || command == Command.BEGIN) {

                transmit(Command.ERROR, null, "Transactions are not supported yet.");
            } else if (command == Command.SEND) {

                String destination = (String) headers.get(FrameHeader.DESTINATION_ENTRY_NAME);
                Message message = makeMessageFromFrame(frame);
                queueManager.addMessage(destination, message);
            } else if (command == Command.SUBSCRIBE) {

                String queueName = (String) headers.get(FrameHeader.DESTINATION_ENTRY_NAME);
                String subscriptionId = (String) headers.get(FrameHeader.ID_ENTRY_NAME);
                queueManager.addSubscription(makeSubscriptionUniqueId(subscriptionId), queueName, this);
            } else if (command == Command.UNSUBSCRIBE) {

                String subscriptionId = (String) headers.get(FrameHeader.ID_ENTRY_NAME);
                queueManager.removeSubscription(makeSubscriptionUniqueId(subscriptionId));
            } else if (command == Command.ACK) {

                String subscriptionId = (String) headers.get(FrameHeader.SUBSCRIPTIONS_ENTRY_NAME);
                queueManager.ackMessage(makeSubscriptionUniqueId(subscriptionId));
            } else if (command == Command.NACK) {

                String subscriptionId = (String) headers.get(FrameHeader.SUBSCRIPTIONS_ENTRY_NAME);
                queueManager.nakcMessage(makeSubscriptionUniqueId(subscriptionId));
            } else {
                transmit(Command.ERROR, null, "Cannot understand the command.");
            }
        } catch (QueueOperationException ex) {
            transmit(Command.ERROR, null, ex.getMessage());
        }
    }

    @Override
    protected void receive(Frame frame) throws IOException {
        Command command = frame.getCommand();
        FrameHeader headers = frame.getHeader();
        if (command == Command.CONNECT) {
            connect(headers);
        } else {
            if (!authenticated) {
                transmit(new Frame(Command.ERROR, null, "Not connected, or not authorized"));
                return;
            }
            if (command == Command.DISCONNECT) {
                disconnect(headers);
                shutdown();
            } else if (command == Command.ERROR) {
                transmit(Command.ERROR, headers, null);
            } else {
                handleFrame(frame);
            }
        }
    }

    private Message makeMessageFromFrame(Frame frame) {
        int maxRedeliveries;
        try {
            String maxRedeliveriesEntry = frame.getHeader().get(FrameHeader.MAX_REDELIVERIES_ENTRY_NAME);
            maxRedeliveries = Integer.parseInt(maxRedeliveriesEntry);
        } catch (Exception e) {
            maxRedeliveries = 0;
        }
        return new Message(frame.getBody(), maxRedeliveries);
    }

    private String makeSubscriptionUniqueId(String subscriptionId) {
        if (subscriptionId == null) {
            subscriptionId = "0";
        }
        return this.hashCode() + ":" + subscriptionId;
    }

    private void transmit(Frame frame) throws IOException {
        transmiter.transmit(frame);
    }

    private void transmit(Command command, FrameHeader header, String body) throws IOException {
        transmit(new Frame(command, header, body));
    }

    @Override
    public void message(Message message) throws IOException {
        FrameHeader headers = new FrameHeader();
        headers.put(FrameHeader.DELIVERIES_COUNT_ENTRY_NAME, message.getDeliveryCount());
        transmit(Command.MESSAGE, headers, (String) message.getContent());
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void shutdown() throws IOException {
        interrupt();
        close();
    }

    @Override
    public String toString() {
        InetAddress ia = clientSocket.getInetAddress();
        return ia.toString() + ":" + clientSocket.getPort();
    }
}
