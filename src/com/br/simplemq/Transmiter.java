package com.br.simplemq;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author dalmir
 */
public class Transmiter {

    private OutputStream out;

    public Transmiter(OutputStream out) {
        this.out = out;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public void transmit(Frame frame) throws IOException {
        
        StringBuilder message = new StringBuilder();
        Map<String, String> header = null;
        String body = frame.getBody();
        
        if (frame.getHeader() != null) {
            header = frame.getHeader().getMap();
        }
        
        message.append(frame.getCommand().toString());
        message.append("\n");

        if (header != null) {
            for (Iterator keys = header.keySet().iterator(); keys.hasNext();) {
                String key = (String) keys.next();
                String value = (String) header.get(key);
                message.append(key);
                message.append(":");
                message.append(value);
                message.append("\n");
            }
        }
        message.append("\n");

        if (body != null) {
            message.append(body);
        }

        message.append("\000");
        out.write(message.toString().getBytes(Command.ENCODING));
        out.flush();
    }
    
    public void close() throws IOException {
        out.close();
    }
}
