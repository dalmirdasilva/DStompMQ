package com.br.simplemq;

/**
 *
 * @author dalmir
 */
public class JStompMQ {

    public static void main(String[] args) {
        Server server = new Server(31313);
        server.listen();
        //JStompMQUI.main(args);
    }
}
