package org.wlpiaoyi.framework.utils.socket;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.thread.ThreadPoolExecutor;
import org.wlpiaoyi.framework.utils.thread.ThreadPoolExecutorBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
@Slf4j
public class Builder {

    // Thread pool used to handle client connections concurrently
    private static ThreadPoolExecutor threadPool = null;

    // Lock used to synchronize access to the thread pool
    private static final Object lockThreadPool = new Object();

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Reloads the thread pool configuration and returns the updated thread pool.
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/16 10:46</p>
     * <p><b>{@code @return:}</b>{@link ThreadPoolExecutor}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static ThreadPoolExecutor reloadThreadPool() {
        synchronized (lockThreadPool){
            threadPool = null;
            return getThreadPool();
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Returns the thread pool used to handle client connections concurrently.
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/16 10:46</p>
     * <p><b>{@code @return:}</b>{@link ThreadPoolExecutor}
     * The thread pool used to handle client connections concurrently.
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static ThreadPoolExecutor getThreadPool() {
        if (threadPool != null) return threadPool;
        synchronized (lockThreadPool) {
            if (threadPool != null) return threadPool;
            Map<String, Object> configMap = null;
            try {
                String loadPath = System.getenv().get("st_thread_config_path");
                if (ValueUtils.isBlank(loadPath)) {
                    configMap = ReaderUtils.loadMap(loadPath, StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                log.warn("Builder.getThreadPool. Failed to load thread pool configuration. loadPath: {}", System.getenv().get("st_thread_config_path"));
            }
            int corePoolSize = 100;
            int maximumPoolSize = 1000;
            long keepAliveTime = 300;
            int workQueueCount = 1000;
            String threadNamePrefix = "st_thread";
            if (configMap != null) {
                corePoolSize = MapUtils.getInteger(configMap, "corePoolSize", corePoolSize);
                maximumPoolSize = MapUtils.getInteger(configMap, "maximumPoolSize", maximumPoolSize);
                keepAliveTime = MapUtils.getLong(configMap, "keepAliveTime", keepAliveTime);
                workQueueCount = MapUtils.getInteger(configMap, "workQueueCount", workQueueCount);
                threadNamePrefix = MapUtils.getString(configMap, "threadNamePrefix", threadNamePrefix);
            }
            threadPool = ThreadPoolExecutorBuilder.newBuilder()
                    .corePoolSize(corePoolSize)
                    .maximumPoolSize(maximumPoolSize)
                    .keepAliveTime(keepAliveTime, TimeUnit.SECONDS)
                    .workQueueCount(workQueueCount)
                    .threadNamePrefix(threadNamePrefix)
                    .callerRunsPolicy()
                    .build();
        }
        return threadPool;
    }

    public static IWriter getWriter(OutputStream out){
        return new ClientWriter(out);
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
    static class ClientWriter implements IWriter {

        // Output stream for sending data to the client
        private final OutputStream out;

        ClientWriter(OutputStream out) {
            this.out = out;
        }

        @Override
        public void write(int clientId, byte[] writeBytes, int len) {
            try {
                log.debug("ClientWriter.write. Sending data to Client ID: {}, Length: {} bytesLen: {}", clientId, len, writeBytes.length);
                if(writeBytes.length < len){
                    throw new IOException("writeBytes.length < len");
                }
                this.out.write(writeBytes, 0, len);
                this.out.flush();
                log.debug("ClientWriter.write. Data sent to Client ID: {}, Length: {}", clientId, len);
            } catch (IOException e) {
                log.error("ClientWriter.write. Error occurred while sending data to Client ID: {}", clientId, e);
            }
        }
    }

}
