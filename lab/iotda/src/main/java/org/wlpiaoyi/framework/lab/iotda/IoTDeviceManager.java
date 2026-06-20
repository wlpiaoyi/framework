package org.wlpiaoyi.framework.lab.iotda;

import com.huaweicloud.sdk.iot.device.IoTDevice;
import com.huaweicloud.sdk.iot.device.client.CustomOptions;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 华为云 IoTDA 设备管理器：维护接入配置、在线设备缓存与上下线。
 * <p>
 * 命名说明：本类并非典型 Factory（不每次 new 实例），而是管理设备连接生命周期，
 * 故使用 Manager。若仅需表达「配置 + 运行时状态容器」，也可考虑 {@code IoTDeviceContext}。
 * <p>
 * 参考官方 demo {@code SecretAuthConnect} / {@code ReConnect}：
 * <ul>
 *   <li>使用密钥 + TLS（8883）创建设备并调用 {@link IoTDevice#init()} 上线</li>
 *   <li>同一 {@code deviceId} 在进程内复用已连接实例（双重检查锁）</li>
 *   <li>关闭 SDK 默认自动重连，改用自定义退避重连（仅在建链失败时由 SDK 调用）</li>
 * </ul>
 * 使用前必须先配置接入地址与 CA 证书文件路径，例如：
 * <pre>
 * IoTDeviceManager.init("ssl://xxx.st1.iotda-device.cn-north-4.myhuaweicloud.com:8883",
 *         "C:/path/to/mqtts_ca_cert.jks");
 * IoTDevice device = IoTDeviceManager.getAndOnlineDevice(deviceId, deviceSecret);
 * IoTDeviceManager.offlineDevice(deviceId);
 * </pre>
 *
 * @author wlpiaoyi
 * @since 2026-05-22
 */
@Slf4j
public class IoTDeviceManager {

    /** 退避重连：前 N 次间隔 5s */
    private static final long BACKOFF_INTERVAL_MS = 5_000L;

    private static final int BACKOFF_LONG_AFTER_RETRY = 3;

    /**
     * IoTDA 设备接入地址，例如：
     * {@code ssl://{id}.st1.iotda-device.cn-north-4.myhuaweicloud.com:8883}
     */
    @Setter
    private static volatile String iotServerUri;

    /**
     * CA 证书文件路径（磁盘路径，由调用方指定）。
     * TLS（ssl://...:8883）时必须配置且文件存在；非 TLS 可置空。
     */
    @Setter
    private static volatile String iotRootCAPath;

    /** 已上线设备缓存：deviceId -> IoTDevice */
    private static final Map<String, IoTDevice> ONLINE_DEVICES = new ConcurrentHashMap<>();

    /** 按 deviceId 细粒度锁，防止同一设备并发重复建链 */
    private static final Map<String, Object> DEVICE_LOCKS = new ConcurrentHashMap<>();

    /** 复用的连接选项 */
    private static volatile CustomOptions customOptions;

    private IoTDeviceManager() {
    }

    /**
     * 一次性初始化接入地址与 CA 文件路径。
     *
     * @param serverUri MQTT 接入地址（ssl://...:8883 或 tcp://...:1883）
     * @param caPath    CA 证书 jks 磁盘路径，非 TLS 可传 null
     */
    public static void init(String serverUri, String caPath) {
        setIotServerUri(serverUri);
        setIotRootCAPath(caPath);
        validateServerUri(serverUri);
        log.info("IoTDeviceManager initialized, serverUri={}, caPath={}", iotServerUri, iotRootCAPath);
    }

    /**
     * SSL 接入必须使用控制台「设备接入地址」中的域名，不能使用 IP。
     * 对 IP 建链时无法正确携带 SNI，平台会在 TLS 握手阶段直接断开（Remote host terminated the handshake）。
     */
    private static void validateServerUri(String serverUri) {
        if (ValueUtils.isBlank(serverUri) || !serverUri.startsWith("ssl:")) {
            return;
        }
        try {
            String normalized = serverUri.replace("ssl://", "https://");
            URI uri = URI.create(normalized);
            String host = uri.getHost();
            if (host != null && isIpAddress(host)) {
                log.error(
                        "SSL serverUri must not use IP address [{}]. Use IoTDA console device access domain "
                                + "(e.g. ssl://{{instanceId}}.st1.iotda-device.cn-north-4.myhuaweicloud.com:8883). "
                                + "TCP port may be open but TLS handshake will fail without SNI.",
                        host);
            }
        } catch (Exception e) {
            log.warn("failed to parse serverUri for validation: {}", serverUri, e);
        }
    }

    private static boolean isIpAddress(String host) {
        if (host.matches("^\\d{1,3}(\\.\\d{1,3}){3}$")) {
            return true;
        }
        try {
            InetAddress address = InetAddress.getByName(host);
            return host.equals(address.getHostAddress());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取已上线设备；缓存命中则直接返回，否则创建连接并缓存。
     *
     * @param deviceId     平台侧设备 ID
     * @param deviceSecret 设备密钥
     * @return 已连接设备；参数非法、建链失败或 CA 不存在时返回 {@code null}
     */
    public static IoTDevice getAndOnlineDevice(String deviceId, String deviceSecret) {
        if (ValueUtils.isBlank(deviceId)) {
            log.warn("getAndOnlineDevice rejected: deviceId is blank");
            return null;
        }
        if (ValueUtils.isBlank(deviceSecret)) {
            log.warn("getAndOnlineDevice rejected: deviceSecret is blank, deviceId={}", deviceId);
            return null;
        }
        if (ValueUtils.isBlank(iotServerUri)) {
            log.error("getAndOnlineDevice rejected: iotServerUri not configured, deviceId={}", deviceId);
            return null;
        }

        IoTDevice cached = ONLINE_DEVICES.get(deviceId);
        if (cached != null) {
            log.debug("device cache hit, deviceId={}", deviceId);
            return cached;
        }

        synchronized (deviceLock(deviceId)) {
            cached = ONLINE_DEVICES.get(deviceId);
            if (cached != null) {
                log.debug("device cache hit after lock, deviceId={}", deviceId);
                return cached;
            }
            log.info("creating and connecting device, deviceId={}, serverUri={}", deviceId, iotServerUri);
            try {
                IoTDevice device = createAndConnect(deviceId, deviceSecret);
                if (device != null) {
                    ONLINE_DEVICES.put(deviceId, device);
                    log.info("device cached as online, deviceId={}, onlineCount={}", deviceId, ONLINE_DEVICES.size());
                } else {
                    log.warn("device connect failed, not cached, deviceId={}", deviceId);
                }
                return device;
            } catch (IOException e) {
                log.error("create device failed, deviceId={}, serverUri={}", deviceId, iotServerUri, e);
                return null;
            }
        }
    }

    /**
     * 下线设备：从缓存移除并关闭 MQTT 连接。
     *
     * @param deviceId 设备 ID
     * @return {@code true} 表示此前在线且已成功关闭；{@code false} 表示设备未在缓存中
     */
    public static boolean offlineDevice(String deviceId) {
        if (ValueUtils.isBlank(deviceId)) {
            log.warn("offlineDevice rejected: deviceId is blank");
            return false;
        }
        IoTDevice device = ONLINE_DEVICES.remove(deviceId);
        if (device == null) {
            log.debug("offlineDevice skipped: device not online, deviceId={}", deviceId);
            return false;
        }
        try {
            device.getClient().close();
            log.info("device offline success, deviceId={}, remainingOnline={}", deviceId, ONLINE_DEVICES.size());
        } catch (Exception e) {
            log.error("device close failed, deviceId={}", deviceId, e);
            return false;
        } finally {
            DEVICE_LOCKS.remove(deviceId);
        }
        return true;
    }

    /**
     * @deprecated 请使用 {@link #offlineDevice(String)}
     */
    @Deprecated
    public static boolean offLineDevice(String deviceId) {
        return offlineDevice(deviceId);
    }

    /**
     * 判断设备是否在本地缓存中（已调用过 {@link #getAndOnlineDevice} 且未下线）。
     */
    public static boolean isOnline(String deviceId) {
        return deviceId != null && ONLINE_DEVICES.containsKey(deviceId);
    }

    /**
     * 当前缓存的在线设备数量（仅进程内统计，不代表平台侧真实状态）。
     */
    public static int onlineDeviceCount() {
        return ONLINE_DEVICES.size();
    }

    private static IoTDevice createAndConnect(String deviceId, String deviceSecret) throws IOException {
        File caFile = resolveCaCertFile();
        if (caFile != null) {
            log.debug("building IoTDevice, deviceId={}, caFile={}", deviceId, caFile.getAbsolutePath());
        } else {
            log.debug("building IoTDevice without CA, deviceId={}", deviceId);
        }

        IoTDevice device = new IoTDevice(iotServerUri, deviceId, deviceSecret, caFile);
        device.getClient().setCustomOptions(buildCustomOptions());

        int initResult = device.init();
        if (initResult != 0) {
            log.error("device.init() failed, deviceId={}, serverUri={}, initResult={}",
                    deviceId, iotServerUri, initResult);
            return null;
        }
        log.info("device online, deviceId={}, serverUri={}", deviceId, iotServerUri);
        return device;
    }

    /**
     * 连接选项：关闭 SDK 默认重连，使用自定义退避（参考官方 {@code ReConnect} demo）。
     */
    private static @NotNull CustomOptions buildCustomOptions() {
        CustomOptions options = customOptions;
        if (options != null) {
            return options;
        }
        synchronized (IoTDeviceManager.class) {
            options = customOptions;
            if (options != null) {
                return options;
            }
            options = new CustomOptions();
            options.setReConnect(false);
            options.setCustomBackoffHandler(connection -> {
                int ret = -1;
                int attempt = 0;
                long intervalMs = BACKOFF_INTERVAL_MS;
                log.warn("MQTT connect failed, starting custom backoff reconnect");
                while (ret != 0) {
                    if (attempt > BACKOFF_LONG_AFTER_RETRY) {
                        return ret;
                    }
                    log.info("backoff reconnect attempt={}, sleepMs={}", attempt + 1, intervalMs);
                    try {
                        Thread.sleep(intervalMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("backoff sleep interrupted", e);
                        return -1;
                    }
                    attempt++;
                    ret = connection.connect();
                    if (ret != 0) {
                        log.warn("backoff reconnect failed, attempt={}, ret={}", attempt, ret);
                    }
                }
                log.info("backoff reconnect success after {} attempt(s)", attempt);
                return 0;
            });
            customOptions = options;
            return options;
        }
    }

    /**
     * 解析 CA 证书文件。{@code iotRootCAPath} 为磁盘路径，不存在时抛异常。
     * 未配置时返回 {@code null}（适用于无 TLS 的 tcp 接入）。
     */
    private static File resolveCaCertFile() throws IOException {
        if (ValueUtils.isBlank(iotRootCAPath)) {
            return null;
        }
        File caFile = new File(iotRootCAPath);
        if (!caFile.isFile()) {
            throw new IOException("CA cert file not found: " + caFile.getAbsolutePath());
        }
        validateCaCertFile(caFile);
        return caFile;
    }

    /**
     * 建链前预检 CA，提前暴露常见 SSL 握手失败原因（SDK 对 JKS 固定使用 null 密码加载）。
     */
    private static void validateCaCertFile(File caFile) throws IOException {
        String name = caFile.getName().toLowerCase();
        if (name.endsWith(".jks")) {
            assertRealJksMagic(caFile);
            try (FileInputStream in = new FileInputStream(caFile)) {
                KeyStore ks = KeyStore.getInstance("JKS");
                try {
                    ks.load(in, null);
                } catch (IOException e) {
                    throw new IOException(
                            "JKS 无法以空密码加载（华为 SDK 仅支持 storepass 为空）。"
                                    + "请用: keytool -importcert -file mqtts_ca_cert.pem -keystore mqtts_ca_cert.jks"
                                    + " -storetype JKS -storepass \"\" -noprompt 重新生成。原因: " + e.getMessage(),
                            e);
                }
                int entries = ks.size();
                if (entries == 0) {
                    throw new IOException(
                            "CA JKS contains 0 entries: " + caFile.getAbsolutePath()
                                    + "。SDK 会信任空集合 → TLS 握手必失败。请重建 JKS 并保证至少有 1 个 trustedCertEntry。");
                }
                log.info("CA JKS validated, path={}, entries={}", caFile.getAbsolutePath(), entries);
            } catch (CertificateException | java.security.NoSuchAlgorithmException | java.security.KeyStoreException e) {
                throw new IOException("invalid CA JKS: " + caFile.getAbsolutePath(), e);
            }
            return;
        }
        if (name.endsWith(".pem") || name.endsWith(".crt")) {
            String content = Files.readString(caFile.toPath(), StandardCharsets.UTF_8);
            int certCount = countPemCertificates(content);
            if (certCount > 1) {
                log.warn("PEM contains {} certificates but Huawei SDK only loads the first one; "
                                + "SSL handshake may fail. Prefer official ca.jks or import all certs into JKS.",
                        certCount);
            }
            log.info("CA PEM validated, path={}, pemBlocks={}", caFile.getAbsolutePath(), certCount);
        }
    }

    /**
     * 校验扩展名 .jks 的文件确实是 JKS 格式（魔术字节 0xFEEDFEED）。
     * <p>JDK9+ 的 {@code keytool} 默认输出 PKCS12（魔术 0x3082），即便文件名写成 {@code .jks}；
     * 华为 SDK 强制按 JKS 解析，PKCS12 文件会被当成空信任库，造成 TLS 握手失败。
     */
    private static void assertRealJksMagic(File caFile) throws IOException {
        try (FileInputStream in = new FileInputStream(caFile)) {
            byte[] head = in.readNBytes(4);
            if (head.length < 4) {
                throw new IOException("CA file too small to be a keystore: " + caFile.getAbsolutePath());
            }
            int magic = ((head[0] & 0xFF) << 24) | ((head[1] & 0xFF) << 16)
                    | ((head[2] & 0xFF) << 8) | (head[3] & 0xFF);
            if (magic == 0xFEEDFEED) {
                return;
            }
            if ((head[0] & 0xFF) == 0x30 && (head[1] & 0xFF) == 0x82) {
                throw new IOException(
                        "文件实际是 PKCS12 格式（magic 0x3082），不是 JKS。SDK 用 JKS 解析会得到 0 条目，"
                                + "TLS 握手必失败。请用 keytool 重建并加 -storetype JKS：\n"
                                + "  keytool -importcert -alias huawei-iot -file mqtts_ca_cert.pem "
                                + "-keystore mqtts_ca_cert.jks -storetype JKS -storepass \"\" -noprompt\n"
                                + "或改用华为官方多区域 ca.jks。文件: " + caFile.getAbsolutePath());
            }
            throw new IOException(String.format(
                    "unknown keystore magic %08X for %s, expect JKS=0xFEEDFEED",
                    magic, caFile.getAbsolutePath()));
        }
    }

    private static int countPemCertificates(String pemContent) {
        int count = 0;
        int idx = 0;
        while ((idx = pemContent.indexOf("-----BEGIN CERTIFICATE-----", idx)) >= 0) {
            count++;
            idx++;
        }
        return count;
    }

    private static Object deviceLock(String deviceId) {
        return DEVICE_LOCKS.computeIfAbsent(deviceId, key -> new Object());
    }
}
