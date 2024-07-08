package org.wlpiaoyi.framework.sshd;

import org.apache.sshd.sftp.client.SftpClient;

public interface UploadListener {

    void uploadBegin(String localFilePath, Object curTag, long fileSize);

    void uploadDoing(String localFilePath, Object curTag, long fileSize, long bufferOffset);

    void uploadEnd(String localFilePath, Object curTag, long fileSize);

    void uploadError(String localFilePath, Object curTag, Throwable error);

}
