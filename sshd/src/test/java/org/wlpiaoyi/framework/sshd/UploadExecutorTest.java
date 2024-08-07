<<<<<<< Updated upstream
package org.wlpiaoyi.framework.sshd;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.sshd.sftp.UploadExecutor;

@Slf4j
public class UploadExecutorTest {

    private UploadExecutor uploadExecutor;

    @Before
    public void setUp() throws Exception {
        this.uploadExecutor = UploadExecutor.build("172.16.23.19", 22, "root");
        this.uploadExecutor.connectSession("000000");
    }

    @SneakyThrows
    @Test
    public void test1(){
        String localFilePath = "/Users/piaoyiwl/Desktop/1.mp4";
        String remoteFilePath = "/root/upload";
        String remoteFileName = "1.up.sftp.mp4";
        this.uploadExecutor.upload(localFilePath, remoteFilePath, remoteFileName, new UploadListener() {
            @Override
            public void uploadBegin(String localFilePath, Object sftpClient, long fileSize) {
                log.info("upload begin for fileSize [{}]", fileSize);
            }

            @Override
            public void uploadDoing(String localFilePath, Object sftpClient, long fileSize, long bufferOffset) {
                log.info("upload doing for fileSize [{}]/ bufferOffset [{}]", fileSize, bufferOffset);
            }

            @Override
            public void uploadEnd(String localFilePath, Object sftpClient, long fileSize) {
                log.info("upload end for fileSize [{}]", fileSize);
            }

            @Override
            public void uploadError(String localFilePath, Object curTag, Throwable error) {

            }
        });

        while (true){
            Thread.sleep(1000);
        }

    }


    @After
    public void tearDown() throws Exception {

    }


}
=======
//package org.wlpiaoyi.framework.sshd;
//
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.sshd.sftp.client.SftpClient;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.wlpiaoyi.framework.sshd.sftp.SftpExecutor;
//import org.wlpiaoyi.framework.sshd.sftp.UploadListener;
//import org.wlpiaoyi.framework.sshd.shell.ShellExecutor;
//
//@Slf4j
//public class SftpExecutorTest {
//
//    private SftpExecutor sftpExecutor;
//
//    @Before
//    public void setUp() throws Exception {
//        this.sftpExecutor = SftpExecutor.build("172.16.22.118", 22, "root");
//        this.sftpExecutor.connectSession("000000");
//    }
//
//    @SneakyThrows
//    @Test
//    public void test1(){
//        String localFilePath = "/Users/piaoyiwl/Desktop/1.mp4";
//        String remoteFilePath = "/root/upload";
//        String remoteFileName = "1.up.mp4";
//        this.sftpExecutor.upload(localFilePath, remoteFilePath, remoteFileName, new UploadListener() {
//            @Override
//            public void uploadBegin(String localFilePath, SftpClient sftpClient, long fileSize) {
//                log.info("upload begin for fileSize [{}]", fileSize);
//            }
//
//            @Override
//            public void uploadDoing(String localFilePath, SftpClient sftpClient, long fileSize, long bufferOffset) {
//                log.info("upload doing for fileSize [{}]/ bufferOffset [{}]", fileSize, bufferOffset);
//            }
//
//            @Override
//            public void uploadEnd(String localFilePath, SftpClient sftpClient, long fileSize) {
//                log.info("upload end for fileSize [{}]", fileSize);
//            }
//        });
//
//    }
//
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//
//}
>>>>>>> Stashed changes
