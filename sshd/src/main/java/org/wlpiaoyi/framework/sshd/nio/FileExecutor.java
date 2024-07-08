package org.wlpiaoyi.framework.sshd.nio;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.wlpiaoyi.framework.sshd.SshdClient;
import org.wlpiaoyi.framework.sshd.sftp.ExecUpload;
import org.wlpiaoyi.framework.sshd.UploadListener;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class FileExecutor extends SshdClient {

    public static FileExecutor build(String ip, Integer port, String user){
        return new FileExecutor(ip, port, user);
    }

    protected FileExecutor(String ip, Integer port, String user) {
        super(ip, port, user);
    }


    public void upload(String localFilePath, String remoteFilePath, String remoteFileName, UploadListener uploadListener){
        ClientSession session = this.getSession();
        try {
            if (!session.auth().verify(10 * 1000).isSuccess()) {
                throw new BusinessException("auth failed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileChannel localFileChannel = null;
        AsynchronousFileChannel remoteFileChannel = null;
        try {
            Path remoteRoot = ExecUpload.uploadCheck(remoteFilePath, remoteFileName, session);
            if(Files.deleteIfExists(remoteRoot)){
                throw new BusinessException("delete remote file failed");
            }
            try (FileInputStream localFileIo = new FileInputStream(localFilePath)) {
                localFileChannel = localFileIo.getChannel();
            }
            //Direct Buffer的效率会更高。
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
            remoteFileChannel =
                    AsynchronousFileChannel.open(remoteRoot, StandardOpenOption.APPEND);
            Object[] attachment = new Object[2];
            attachment[0] = -1L;
            final Lock bufferLock = new ReentrantLock();
            final long fileSize = DataUtils.getSize(localFilePath);
            final CompletionHandler<Integer, Object[]> bufferHandler = new CompletionHandler<>() {
                @Override
                public void completed(Integer result, Object[] attachment) {
                    log.debug("buffer lock try unlock {}", attachment);
                    long bufferOffset = ((Long)attachment[0]) + result;
                    if(uploadListener != null) uploadListener.uploadDoing(localFilePath, remoteFilePath, fileSize, bufferOffset);
                    attachment[0] = bufferOffset;
                    bufferLock.unlock();
                    log.debug("buffer lock unlock {}", attachment);
                }

                @Override
                public void failed(Throwable exc, Object[] attachment) {
                    log.error("channel write failed", exc);
                    attachment[0] = -1;
                    attachment[1] = exc;
                    bufferLock.unlock();
                }
            };
            if(uploadListener != null) uploadListener.uploadBegin(localFilePath, remoteFilePath, fileSize);
            while(true)
            {
                log.debug("buffer lock try lock {}", attachment);
                bufferLock.lock();
                log.debug("buffer lock in {}", attachment);
                int eof = localFileChannel.read(byteBuffer);
                if(eof == -1 ) break;
                byteBuffer.flip();
                remoteFileChannel.write(byteBuffer, eof, attachment, bufferHandler);
                byteBuffer.clear();
                if(attachment[1] != null){
                    throw ((Exception) attachment[1]);
                }
            }
            if(uploadListener != null) uploadListener.uploadEnd(localFilePath, remoteFilePath, fileSize);
        } catch (Exception e) {
            if(uploadListener != null) uploadListener.uploadError(localFilePath, remoteFilePath, e);
            log.error("upload error!", e);
            throw new BusinessException(e);
        }finally {
            try {
                if(localFileChannel != null) localFileChannel.close();
            } catch (IOException e) {
                log.error("localFileChannel close failed", e);
            }
            try {
                if(remoteFileChannel != null) remoteFileChannel.close();
            } catch (IOException e) {
                log.error("remoteFileChannel close failed", e);
            }
        }
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
        Path remoteRoot = ExecUpload.uploadCheck(remoteFilePath, remoteFileName, session);
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
