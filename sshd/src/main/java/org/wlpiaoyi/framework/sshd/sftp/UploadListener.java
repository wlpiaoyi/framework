package org.wlpiaoyi.framework.sshd.sftp;

import org.apache.sshd.sftp.client.SftpClient;

public interface UploadListener {

    void uploadBegin(String localFilePath, SftpClient sftpClient, long fileSize);

    void uploadDoing(String localFilePath, SftpClient sftpClient, long fileSize, long bufferOffset);

    void uploadEnd(String localFilePath, SftpClient sftpClient, long fileSize);

}
