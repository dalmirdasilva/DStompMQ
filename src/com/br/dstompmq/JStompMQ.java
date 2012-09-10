package com.br.dstompmq;

import com.br.dstompmqui.JStompMQUI;

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
