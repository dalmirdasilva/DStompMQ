package com.br.dstompmq;

/**
 *
 * @author dalmir
 */
public class Frame {

    private Command command;
    private FrameHeader header;
    private String body;

    public Frame(Command command, FrameHeader header, String body) {
        this.command = command;
        this.header = header;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public FrameHeader getHeader() {
        return header;
    }

    public void setHeader(FrameHeader header) {
        this.header = header;
    }
}
