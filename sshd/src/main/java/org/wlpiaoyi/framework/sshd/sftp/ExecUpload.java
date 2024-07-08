package org.wlpiaoyi.framework.sshd.sftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.wlpiaoyi.framework.sshd.UploadListener;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ExecUpload implements Runnable {

    private final WeakReference<ClientSession> clientSessionWeakReference;
    private final String localFilePath;
    private final String remoteFilePath;
    private final String remoteFileName;
    private UploadListener uploadListener;

    public static ExecUpload build(ClientSession clientSession, String localFilePath, String remoteFilePath, String remoteFileName){
        return new ExecUpload(clientSession, localFilePath, remoteFilePath, remoteFileName);
    }

    private ExecUpload(ClientSession clientSession, String localFilePath, String remoteFilePath, String remoteFileName){
        this.clientSessionWeakReference = new WeakReference<>(clientSession);
        this.localFilePath = localFilePath;
        this.remoteFilePath = remoteFilePath;
        this.remoteFileName = remoteFileName;
    }

    public ExecUpload setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
        return this;
    }

    @Override
    public void run() {

        FileInputStream localFileIo = null;
        SftpClient sftpClient = null;
        SftpClient.CloseableHandle handle = null;
        try {
            File localFile = new File(localFilePath);
            if(!localFile.exists()){
                throw new BusinessException("not fund local file");
            }
            ClientSession clientSession = this.clientSessionWeakReference.get();
            if(clientSession == null){
                throw new BusinessException("Session can't be empty");
            }
            uploadCheck(remoteFilePath, remoteFileName, clientSession);
            sftpClient = SftpClientFactory.instance().createSftpClient(clientSession);
            handle = sftpClient.open(remoteFilePath + "/" + remoteFileName , SftpClient.OpenMode.Write, SftpClient.OpenMode.Create);

            localFileIo = new FileInputStream(localFile);
            int buffer_size = 1024;
            byte[] buffer = new byte[buffer_size];
            int buffer_length;
            long buffer_offset = 0L;
            long fileSize = DataUtils.getSize(localFilePath);
            uploadListener.uploadBegin(localFilePath, sftpClient, fileSize);
            while ((buffer_length = localFileIo.read(buffer)) != -1) {
                sftpClient.write(handle, buffer_offset, buffer, 0, buffer_length);
                buffer_offset += buffer_length;
                uploadListener.uploadDoing(localFilePath, sftpClient, fileSize, buffer_offset);
            }
            uploadListener.uploadEnd(localFilePath, sftpClient, fileSize);
        } catch (Exception e) {
            log.error("upload error!", e);
            throw new BusinessException(e);
        }finally {
            try {
                if(sftpClient != null){
                    if(handle != null){
                        sftpClient.close(handle);
                    }
                    sftpClient.close();
                }
            } catch (IOException e) {
                log.error("sftp client close failed", e);
            }
            if(localFileIo != null){
                try {
                    localFileIo.close();
                } catch (IOException e) {
                    log.error("local file io close failed", e);
                }
            }
        }
    }

    public static Path uploadCheck(String remoteFilePath, String remoteFileName, ClientSession session){
        try {
            SftpFileSystem sftpFileSystem = SftpClientFactory.instance().createSftpFileSystem(session);
            Path remoteRoot = sftpFileSystem.getDefaultDir().resolve(remoteFilePath);
            // create remote path
            if(!Files.exists(remoteRoot)){
                Files.createDirectories(remoteRoot);
            }
            remoteRoot = sftpFileSystem.getDefaultDir().resolve(remoteFilePath + "/" + remoteFileName);
            // delete remote file
            if(Files.exists(remoteRoot)){
                Files.deleteIfExists(remoteRoot);
            }
            return remoteRoot;
        } catch (Exception e) {
            log.error("upload check error!", e);
            throw new BusinessException(e);
        }
    }
}
