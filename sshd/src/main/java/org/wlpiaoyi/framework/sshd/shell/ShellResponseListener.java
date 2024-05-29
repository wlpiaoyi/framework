package org.wlpiaoyi.framework.sshd.shell;

import org.apache.sshd.client.channel.ClientChannel;

public interface ShellResponseListener {

    default void responseByte(ClientChannel channel, byte b){}
    void responseLine(ClientChannel channel, byte[] buffer, int bufferLength);

}
