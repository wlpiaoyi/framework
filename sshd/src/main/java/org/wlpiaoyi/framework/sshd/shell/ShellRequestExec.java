package org.wlpiaoyi.framework.sshd.shell;

import org.apache.sshd.client.channel.ClientChannel;

import java.io.OutputStream;

public interface ShellRequestExec {

    void exec(ClientChannel channel, OutputStream cmdOs);

}
