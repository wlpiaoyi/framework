package org.wlpiaoyi.framework.shell;

import java.util.List;

public interface ExecShell {

    String execCmd(int cmdIndex, List<String> historyCmd, List<String> historyRes);

    void response(String curRes, List<String> historyCmd, List<String> historyRes);

}
