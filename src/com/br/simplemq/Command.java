package com.br.simplemq;

/**
 *
 * @author dalmir
 */
public class Command {

    public final static String ENCODING = "UTF8";
    private String name;
    public static Command SEND = new Command("SEND"),
            SUBSCRIBE = new Command("SUBSCRIBE"),
            UNSUBSCRIBE = new Command("UNSUBSCRIBE"),
            BEGIN = new Command("BEGIN"),
            COMMIT = new Command("COMMIT"),
            ABORT = new Command("ABORT"),
            DISCONNECT = new Command("DISCONNECT"),
            CONNECT = new Command("CONNECT"),
            ACK = new Command("ACK"),
            NACK = new Command("NACK");
    public static Command MESSAGE = new Command("MESSAGE"),
            RECEIPT = new Command("RECEIPT"),
            CONNECTED = new Command("CONNECTED"),
            ERROR = new Command("ERROR");

    private Command(String name) {
        this.name = name;
    }

    public static Command fromName(String name) throws UnrecognisedCommandException {
        name = name.trim();
        if (name.equals("SEND")) {
            return SEND;
        } else if (name.equals("SUBSCRIBE")) {
            return SUBSCRIBE;
        } else if (name.equals("UNSUBSCRIBE")) {
            return UNSUBSCRIBE;
        } else if (name.equals("BEGIN")) {
            return BEGIN;
        } else if (name.equals("COMMIT")) {
            return COMMIT;
        } else if (name.equals("ABORT")) {
            return ABORT;
        } else if (name.equals("CONNECT")) {
            return CONNECT;
        } else if (name.equals("MESSAGE")) {
            return MESSAGE;
        } else if (name.equals("RECEIPT")) {
            return RECEIPT;
        } else if (name.equals("CONNECTED")) {
            return CONNECTED;
        } else if (name.equals("DISCONNECT")) {
            return DISCONNECT;
        } else if (name.equals("ERROR")) {
            return ERROR;
        } else if (name.equals("ACK")) {
            return ACK;
        } else if (name.equals("NACK")) {
            return NACK;
        }
        throw new UnrecognisedCommandException(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
