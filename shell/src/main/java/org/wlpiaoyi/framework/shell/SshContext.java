package org.wlpiaoyi.framework.shell;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@Slf4j
public class SshContext {

    private final Session session;

    public SshContext(String host, int port, String userName, String password){
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(userName, host, port);
            // 设置密码
            session.setPassword(password);
            // 创建一个session配置类
            Properties sshConfig = new Properties();
            // 跳过公钥检测
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            this.session = session;
        } catch (JSchException e) {
            throw new BusinessException(e);
        }
    }
    public void connectSession(int timeOut){
        try {
            // 我们还可以设置timeout时间
            session.setTimeout(timeOut);
            this.session.connect();
        } catch (JSchException e) {
            throw new BusinessException(e);
        }
    }

    public void disconnectSession(){
        this.session.disconnect();
    }

    public List<String> exec(String cmd){
        List<String> resultLines = new ArrayList<>();
        ChannelExec channelExec = null;
        InputStream inputStream = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            // 将shell传入command
            channelExec.setCommand(cmd);
            channelExec.setErrStream(System.err);
            // 开始执行
            channelExec.connect();
            // 获取执行结果的输入流
            inputStream = channelExec.getInputStream();
            String result;
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            while ((result = in.readLine()) != null) {
                resultLines.add(result);
            }
            if(ValueUtils.isNotBlank(resultLines)){
                resultLines.addFirst("0");
            }
            return resultLines;
        } catch (Exception e) {
            ArrayList<String> errorMsg = new ArrayList<>();
            errorMsg.add("1");
            errorMsg.add(e.getMessage());
            Throwable throwable = e;
            while ((throwable = throwable.getCause()) != null){
                errorMsg.add(throwable.getCause().getMessage());
            }
            return errorMsg;
        }finally {
            if (channelExec != null) {
                try {
                    channelExec.disconnect();
                } catch (Exception e) {
                    log.error("JSch channel disconnect error:", e);
                }
            }
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("inputStream close error:", e);
                }
            }
        }
    }

    public void exec(ExecShell execShell){
        ChannelShell channelShell = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try{
            //尝试解决 远程ssh只能执行一句命令的情况
            channelShell = (ChannelShell) session.openChannel("shell");
            inputStream = channelShell.getInputStream();//从远端到达的数据  都能从这个流读取到
            channelShell.setPty(true);
            channelShell.connect();
            outputStream = channelShell.getOutputStream();//写入该流的数据  都将发送到远程端

            //使用PrintWriter 就是为了使用println 这个方法
            //好处就是不需要每次手动给字符加\n
            List<String> historyCmd = new ArrayList<>();
            List<String> historyRes = new ArrayList<>();
            AtomicInteger cmdIndex = new AtomicInteger(-1);
            final OutputStream finalOutputStream = outputStream;
            new Thread(() -> {
                PrintWriter printWriter = new PrintWriter(finalOutputStream);
                try{
                    while (true){
                        cmdIndex.getAndIncrement();
                        String cmd = execShell.execCmd(cmdIndex.get(), new ArrayList(){{
                            addAll(historyCmd);
                        }}, new ArrayList(){{
                            addAll(historyRes);
                        }});
                        if(ValueUtils.isBlank(cmd)){
                            break;
                        }
                        historyCmd.add(cmd);
                        printWriter.println(cmd);
                        printWriter.flush();
                    }
                }catch (Exception e){
                    log.error("send cmd error", e);
                }finally {
                    try{
                        printWriter.println("exit");
                        printWriter.flush();
                    }catch (Exception e){
                        printWriter.close();
                    }
                }
            }).start();

        /*
         shell管道本身就是交互模式的。要想停止，有两种方式：
         一、人为的发送一个exit命令，告诉程序本次交互结束
         二、使用字节流中的available方法，来获取数据的总大小，然后循环去读。
         为了避免阻塞
         */
            byte[] tmp = new byte[2048];
            while(true){
                while(inputStream.available() > 0){
                    int i = inputStream.read(tmp, 0, 2048);
                    if(i < 0) break;
                    String s = new String(tmp, 0, i);
                    historyRes.add(s);
                    if(s.contains("--More--")){
                        outputStream.write((" ").getBytes());
                        outputStream.flush();
                    }
                    execShell.response(s, new ArrayList(){{
                        addAll(historyCmd);
                    }}, new ArrayList(){{
                        addAll(historyRes);
                    }});
                }
                if(channelShell.isClosed()){
                    System.out.println("exit-status:"+channelShell.getExitStatus());
                    break;
                }
                try{sleep(1000);}catch(Exception e){}

            }
        }catch (Exception e){
            throw new BusinessException(e);
        }finally {
            if (channelShell != null) {
                try {
                    channelShell.disconnect();
                } catch (Exception e) {
                    log.error("JSch channel disconnect error:", e);
                }
            }
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("inputStream close error:", e);
                }
            }
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error("outputStream close error:", e);
                }
            }
        }
    }

    /**
     * 上传文件
     * @param directory     上传的目录
     * @param fileName      上传的文件名
     * @param uploadFile    要上传的文件
     */
    public boolean upload(String directory, String fileName, File uploadFile) {
        FileInputStream fileInputStream=null;
        ChannelSftp channelSftp=null;
        try {
            channelSftp= (ChannelSftp) this.session.openChannel("sftp");
            channelSftp.connect();
            log.info("start upload channel file!");
            channelSftp.cd(directory);
            fileInputStream = new FileInputStream(uploadFile);
            channelSftp.put(fileInputStream, fileName);
            return true;
        } catch (Exception e) {
            log.error("SFTPClient upload file failed, {}", e.getMessage(), e);
            return false;
        }finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    log.error("File output stream close error, ", e);
                }
            }
            if (channelSftp!=null){
                channelSftp.disconnect();
            }
        }
    }

    /**
     * 下载文件
     * @param directory     下载目录
     * @param fileName      下载的文件名
     * @param saveFile      存在本地的路径
     */
    public File download(String directory, String fileName, String saveFile) {
        ChannelSftp channelSftp = null;
        FileOutputStream fileOutputStream = null;
        try {
            channelSftp= (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd(directory);
            File file = new File(saveFile);
            if (file.exists()) {
                file.delete();
            }
            fileOutputStream = new FileOutputStream(file);
            channelSftp.get(fileName, fileOutputStream);
            return file;
        } catch (Exception e) {
            log.error("SFTPClient download file failed, {}", e.getMessage(), e);
            return null;
        }finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    log.error("File output stream close error, ", e);
                }
            }
            if (channelSftp!=null){
                channelSftp.disconnect();
            }
        }
    }
}
