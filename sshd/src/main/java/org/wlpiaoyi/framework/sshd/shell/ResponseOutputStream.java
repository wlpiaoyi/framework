package org.wlpiaoyi.framework.sshd.shell;

import org.apache.sshd.client.channel.ClientChannel;

import java.io.IOException;
import java.io.OutputStream;

class ResponseOutputStream extends OutputStream {

    private int indexLength = 0;
    private final byte[] buffers;
    private final ClientChannel channel;
    private final ShellResponseListener responseListener;

    private static final int WRITE_LINE_END = '\n';

    ResponseOutputStream(int bufferLength, ClientChannel channel, ShellResponseListener responseListener){
        this.buffers = new byte[bufferLength];
        this.channel = channel;
        this.responseListener = responseListener;
    }

    @Override
    public void write(int d) throws IOException {
        byte b = (byte) d;
        responseListener.responseByte(channel, b);
        buffers[indexLength] = b;
        indexLength++;
        if(b == WRITE_LINE_END){
            responseListener.responseLine(channel, buffers, indexLength);
            indexLength = 0;
        }
    }
}
