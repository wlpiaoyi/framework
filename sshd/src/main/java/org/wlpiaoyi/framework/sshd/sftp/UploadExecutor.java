package org.wlpiaoyi.framework.sshd.sftp;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.sshd.SshdClient;
import org.wlpiaoyi.framework.sshd.UploadListener;

@Slf4j
public class UploadExecutor extends SshdClient {

    public static UploadExecutor build(String ip, Integer port, String user){
        return new UploadExecutor(ip, port, user);
    }

    protected UploadExecutor(String ip, Integer port, String user) {
        super(ip, port, user);
    }

    public void upload(String localFilePath, String remoteFilePath, String remoteFileName, UploadListener uploadListener){
        SshdClient.threadRun(ExecUpload.build(this.getSession(), localFilePath, remoteFilePath, remoteFileName).setUploadListener(uploadListener));
    }
}
