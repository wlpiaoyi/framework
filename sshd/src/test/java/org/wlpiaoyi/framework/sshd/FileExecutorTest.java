package org.wlpiaoyi.framework.sshd;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.sshd.nio.FileExecutor;
import org.wlpiaoyi.framework.sshd.sftp.UploadExecutor;

@Slf4j
public class FileExecutorTest {

    private FileExecutor fileExecutor;

    @Before
    public void setUp() throws Exception {
        this.fileExecutor = FileExecutor.build("172.16.22.23", 22, "root");
        this.fileExecutor.connectSession("000000");
    }

    @Test
    public void test() throws InterruptedException {
        String localFilePath = "/Users/piaoyiwl/Desktop/1.mp4";
        String remoteFilePath = "/root/upload";
        String remoteFileName = "1.up.nio.mp4";
        this.fileExecutor.upload(localFilePath, remoteFilePath, remoteFileName, new UploadListener() {
            @Override
            public void uploadBegin(String localFilePath, Object curTag, long fileSize) {

            }

            @Override
            public void uploadDoing(String localFilePath, Object curTag, long fileSize, long bufferOffset) {

            }

            @Override
            public void uploadEnd(String localFilePath, Object curTag, long fileSize) {

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
