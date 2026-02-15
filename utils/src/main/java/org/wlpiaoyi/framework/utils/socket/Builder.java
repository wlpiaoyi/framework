package org.wlpiaoyi.framework.utils.socket;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.thread.ThreadPoolExecutor;
import org.wlpiaoyi.framework.utils.thread.ThreadPoolExecutorBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='border-radius: 12px;  padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * TODO
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2026/2/15 23:31</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public class Builder {

    // Thread pool used to handle client connections concurrently
    private static ThreadPoolExecutor threadPool = null;

    private static final Object lock = new Object();

    public static ThreadPoolExecutor getThreadPool() {
        if (threadPool != null) return threadPool;
        synchronized (lock) {
            if (threadPool != null) return threadPool;
            threadPool = ThreadPoolExecutorBuilder.newBuilder()
                    .corePoolSize(100)
                    .maximumPoolSize(1000)
                    .keepAliveTime(300, TimeUnit.SECONDS)
                    .workQueueCount(1000)
                    .threadNamePrefix("socket")
                    .callerRunsPolicy()
                    .build();
        }
        return threadPool;
    }


    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Implements the IClientWriter interface to send data to the client.
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/2/15 21:49</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @Slf4j
    public static class ClientWriter implements IWriter {

        // Output stream for sending data to the client
        private final OutputStream out;

        public ClientWriter(OutputStream out) {
            this.out = out;
        }

        @Override
        public void write(int clientId, byte[] writeBytes, int len) {
            try {
                log.debug("ClientWriter.write. Sending data to Client ID: {}, Length: {}", clientId, len);
                this.out.write(writeBytes, 0, len);
                this.out.flush();
                log.debug("ClientWriter.write. Data sent to Client ID: {}, Length: {}", clientId, len);
            } catch (IOException e) {
                log.error("ClientWriter.write. Error occurred while sending data to Client ID: {}", clientId, e);
            }
        }
    }

}
