package com.br.dstompmq;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.security.auth.login.LoginException;

public class SocketHandler extends AbstractReceiver implements QueueListener, Authenticatable {

    private Transmiter transmiter;
    private Object clientToken;
    private boolean authenticated = false;
    Socket clientSocket;

    public SocketHandler(Socket clientSocket) throws IOException {
        super(clientSocket.getInputStream());
        transmiter = new Transmiter(clientSocket.getOutputStream());
        this.clientSocket = clientSocket;
    }

    public void close() throws IOException {
        in.close();
        transmiter.close();
        clientSocket.close();
    }

    @Override
    protected void receive(Frame frame) throws IOException {
        Command command = frame.getCommand();
        FrameHeader headers = frame.getHeader();
        System.out.println("Frame headers: " + headers);
        if (command == Command.CONNECT) {
            System.out.println("CONNECT received.");
            String login = (String) headers.get("login");
            String passcode = (String) headers.get("passcode");
            try {
                clientToken = Authenticator.connect(login, passcode);
                FrameHeader header = new FrameHeader();
                header.put("session", String.valueOf(this.hashCode()));
                transmit(Command.CONNECTED, header, null);
                setAuthenticated(true);
                System.out.println("Authenticated.");
            } catch (LoginException e) {
                System.out.println("ERROR on connect.");
                transmit(Command.ERROR, null, "Login failed: " + e.getMessage());
            }
        } else {
            if (!authenticated) {
                System.out.println("Not Connected, or not authorized.");
                transmit(new Frame(Command.ERROR, null, "Not Connected, or not authorized"));
                return;
            }

            if (command == Command.DISCONNECT) {
                System.out.println("DISCONNECT received.");
                if (headers != null) {
                    String receipt = (String) headers.get("receipt");
                    if (receipt != null) {
                        FrameHeader header = new FrameHeader();
                        header.put("receipt-id", receipt);
                        transmit(Command.RECEIPT, header, null);
                    }
                }
                shutdown();
            } else if (command == Command.ERROR) {
                transmit(Command.ERROR, headers, null);
            } else {
                
                QueueManager queueManager = QueueManager.getInstance();

                if (command == Command.COMMIT || command == Command.ABORT || command == Command.BEGIN) {
                    System.out.println("Transactions not supported yet.");
                    transmit(Command.ERROR, null, "Transactions not supported yet.");
                } else if (command == Command.SEND) {
                    String destination = (String) headers.get("destination");
                    Message message = getMessageFromFrame(frame);
                    queueManager.addMessage(destination, message);
                } else if (command == Command.SUBSCRIBE) {
                    System.out.println("SUBSCRIBE received.");
                    String queueName = (String) headers.get("destination");
                    String subscriptionId = (String) headers.get("id");
                    if (!queueManager.addSubscription(makeSubscriptionIdUnique(subscriptionId), queueName, this)) {
                        transmit(Command.ERROR, null, "Cannot subscribe the queue with this headers: " + headers);
                    }
                } else if (command == Command.UNSUBSCRIBE) {
                    System.out.println("UNSUBSCRIBE received.");
                    String subscriptionId = (String) headers.get("id");
                    queueManager.removeSubscription(makeSubscriptionIdUnique(subscriptionId));
                } else if (command == Command.ACK) {
                    System.out.println("ACK received.");
                    String subscriptionId = (String) headers.get("subscription");
                    queueManager.ackMessage(makeSubscriptionIdUnique(subscriptionId));
                } else if (command == Command.NACK) {
                    System.out.println("NACK received.");
                    String subscriptionId = (String) headers.get("subscription");
                    queueManager.nakcMessage(makeSubscriptionIdUnique(subscriptionId));
                } else {
                    transmit(Command.ERROR, null, "Cannot understand the command.");
                }
            }
        }
    }
    
    private Message getMessageFromFrame(Frame frame) {
        int maxRedeliveries = Integer.parseInt(frame.getHeader().get("max-redeliveries"));
        return new Message(frame.getBody(), maxRedeliveries);
    }
    
    private String makeSubscriptionIdUnique(String subscriptionId) {
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
        transmit(Command.MESSAGE, null, (String) message.getContent());
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
