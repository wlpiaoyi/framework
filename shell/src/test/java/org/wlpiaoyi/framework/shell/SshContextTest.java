package org.wlpiaoyi.framework.shell;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.List;

@Slf4j
public class SshContextTest {

    private SshContext sshContext;

    @Before
    public void setUp() throws Exception {
        this.sshContext = new SshContext("172.16.22.201", 22, "root", "000000");
        this.sshContext.connectSession(30000);
    }

    @Test
    public void exec() throws Exception {
        List<String> res = this.sshContext.exec("pwd");
        log.info("cmd res:\n{}", ValueUtils.toStrings(res, "\n"));
        this.sshContext.exec(new ExecShell() {
            @SneakyThrows
            @Override
            public String execCmd(int cmdIndex, List<String> historyCmd, List<String> historyRes) {
                Thread.sleep(1000);
                return switch (cmdIndex) {
                    case 0 -> "pwd";
                    case 1 -> "ls";
                    case 2 -> "cd ../";
                    case 3 -> "ls -al";
                    default -> "exit";
                };
            }

            @Override
            public void response(String curRes ,List<String> historyCmd, List<String> historyRes) {
                System.out.printf(curRes);
            }
        });
    }


    @After
    public void tearDown() throws Exception {
        this.sshContext.disconnectSession();
    }
}
