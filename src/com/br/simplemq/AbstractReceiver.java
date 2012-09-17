package com.br.simplemq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dalmir
 */
public abstract class AbstractReceiver extends Thread {

    protected InputStream in;

    public AbstractReceiver(InputStream in) {
        this.in = in;
    }

    protected abstract void receive(Frame frame) throws IOException;

    @Override
    public void run() {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while (!isInterrupted()) {
            try {
                if (reader.ready()) {
                    String commandName = reader.readLine();
                    if (commandName.length() > 0) {
                        try {
                            Command command = Command.fromName(commandName);
                            FrameHeader header = new FrameHeader();
                            Frame frame = new Frame(command, header, null);
                            Map<String, String> headers = new HashMap<String, String>();
                            String line;
                            while ((line = reader.readLine()).length() > 0) {
                                int separator = line.indexOf(':');
                                String key = line.substring(0, separator);
                                String value = line.substring(separator + 1, line.length());
                                headers.put(key.trim(), value.trim());
                            }
                            header.setMap(headers);
                            int b;
                            StringBuilder body = new StringBuilder();
                            while ((b = reader.read()) != 0) {
                                body.append((char) b);
                            }
                            frame.setBody(body.toString());
                            try {
                                receive(frame);
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                            }
                        } catch (UnrecognisedCommandException e) {
                            e.printStackTrace(System.err);
                        } catch (Exception e) {
                            try {
                                receive(new Frame(Command.ERROR, null, e.getMessage() + "\n"));
                            } catch (Exception ex) {
                                e.printStackTrace(System.err);
                            }
                        }
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
