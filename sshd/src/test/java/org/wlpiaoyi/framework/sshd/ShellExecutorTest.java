package org.wlpiaoyi.framework.sshd;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.sshd.shell.ExecShell;
import org.wlpiaoyi.framework.sshd.shell.ShellExecutor;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class ShellExecutorTest {
    private ShellExecutor shellExecutor;

    @Before
    public void setUp() throws Exception {
        this.shellExecutor = ShellExecutor.build("172.16.23.19", 22, "root");
        this.shellExecutor.connectSession("000000");
    }

    @SneakyThrows
    @Test
    public void test1(){
        ShellExecutor shellExecutor = this.shellExecutor;
        log.info("start exec");
        CountDown countDown = shellExecutor.createChannel((channel, cmdOs, cd) -> {
            List<String> shells = new ArrayList<>();
            shells.add("pwd");
            shells.add("cd ../");
            shells.add("pwd");
            shells.add("ls");
            shells.add("cd ~");
            shells.add("pwd");
            shells.add("ls");
            shells.add("date");
            Random random = new Random();
            for (String shell : shells) {
                try {
                    Thread.sleep(Math.abs(random.nextInt() % 1000) + 1000);
                    ShellExecutor.execShell(ExecShell.build(shell, channel, cmdOs, 3000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    Thread.sleep(2000);
                    channel.close();
                    shellExecutor.disconnectSession();
                }
            }).start();
        },
                (channelShell, buffer, bufferLength) -> System.out.printf(new String(buffer, 0, bufferLength)),
                (channelShell, buffer, bufferLength) -> System.out.printf(new String(buffer, 0, bufferLength)),
                1024, 5000);
        log.info("first shell end exec");
        ShellExecutor.await(countDown);
        log.info("end exec");
    }

    @Test
    public void serverTest() throws IOException, InterruptedException
    {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(22);

        //*give host key generator a path, when sshd server restart, the same key will be load and used to authenticate the server
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));

        sshd.setPasswordAuthenticator((username, password, session) -> {
            System.out.println("authen:  user="+username+"  password="+password);
            if("bellring".equals(username) && "123456".equals(password))
                return true;
            return false;
        });

        //use file ~/.ssh/authorized_keys
        sshd.setPublickeyAuthenticator(new DefaultAuthorizedKeysAuthenticator(false));

        //* CommandFactory can be userd in addition to the ShellFactory,
        //*  it can also be used instead of the ShellFactory.
        //*  The CommandFactory is used when direct commands are sent to the SSH server,
        //*  as this is the case when running ssh localhost shutdown or scp xxx
        ScpCommandFactory scpCmdFactory=new ScpCommandFactory();
        scpCmdFactory.setDelegateCommandFactory((channelSession, command) -> {
            System.out.println("command = \"" + command + "\"");
            return new ProcessShellFactory(command).createShell(channelSession);
        });
        sshd.setCommandFactory(scpCmdFactory);


        sshd.start();
    }

    @After
    public void tearDown() throws Exception {
        this.shellExecutor.destroyed();
    }

}
