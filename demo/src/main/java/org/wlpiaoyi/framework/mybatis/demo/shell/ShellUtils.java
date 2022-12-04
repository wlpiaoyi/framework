package org.wlpiaoyi.framework.mybatis.demo.shell;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShellUtils {
    public static void executeOneMore(List<String> commands) {
        Runtime run = Runtime.getRuntime();
        try {
            Process proc = run.exec("/bin/bash", null, null);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            for (String line : commands) {
                out.println(line);
            }
            out.println("exit");// 结束命令
            String rspLine = "";
            while ((rspLine = in.readLine()) != null) {
                System.out.println(rspLine);
            }
            proc.waitFor();
            in.close();
            out.close();
            proc.destroy();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ShellUtils.executeOneMore(new ArrayList(){{
            add("cd /c/Users/wlpia/Documents/Develop/zhzf-cloud-pro/shell/reploy");
        }});
    }
}
