package org.wlpiaoyi.framework.sshd.sftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.wlpiaoyi.framework.sshd.SshdClient;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class SftpExecutor extends SshdClient {

    public static SftpExecutor build(String ip, Integer port, String user){
        return new SftpExecutor(ip, port, user);
    }

    protected SftpExecutor(String ip, Integer port, String user) {
        super(ip, port, user);
    }

    public void upload(String localFilePath, String remoteFilePath, String remoteFileName, UploadListener uploadListener){
        SshdClient.threadRun(ExecSftp.build(this.getSession(), localFilePath, remoteFilePath, remoteFileName).setUploadListener(uploadListener));
    }

    public void upload(String localFilePath, String remoteFilePath, String remoteFileName){
        ClientSession session = this.getSession();
        try {
            if (!session.auth().verify(10 * 1000).isSuccess()) {
                throw new BusinessException("auth failed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path remoteRoot = ExecSftp.uploadCheck(remoteFilePath, remoteFileName, session);
        try {
            Files.copy(Paths.get(localFilePath), remoteRoot, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("upload error!", e);
            throw new BusinessException(e);
        }
    }


    public void download(String remoteFilePath, String remoteFileName, String localFile){
        try {
            if (!this.getSession().auth().verify(10 * 1000).isSuccess()) {
                throw new BusinessException("auth failed");
            }
            Path localFilePath = Paths.get(localFile);
            Files.deleteIfExists(localFilePath);
            // create location file path
            Path localPath = Paths.get(localFile.substring(0, localFile.lastIndexOf(File.separator)));
            Files.createDirectories(localPath);

            SftpFileSystem sftpFileSystem = SftpClientFactory.instance().createSftpFileSystem(this.getSession());
            Path remotePath = sftpFileSystem.getDefaultDir().resolve(remoteFilePath).resolve(remoteFileName);
            Files.copy(remotePath, localFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("channel error!", e);
            throw new BusinessException(e);
        }
    }
}
