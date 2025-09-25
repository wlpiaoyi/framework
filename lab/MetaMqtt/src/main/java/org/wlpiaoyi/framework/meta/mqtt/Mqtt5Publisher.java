package org.wlpiaoyi.framework.meta.mqtt;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p<b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b><br/>
 * MQTT 5.0消息发布和订阅类 <br/>
 * 提供连接管理、消息发布、消息订阅和资源清理功能
 * </p>
 * <p><b>{@code @date:}</b>2025/8/27 15:44</p>
 * <p><b>{@code @version:}</b>1.0</p>
 */
public class Mqtt5Publisher implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(Mqtt5Publisher.class.getName());

    private final String broker;
    private final String clientId;
    private final String username;
    private final char[] password;
    private final MqttConnectionOptions connectionOptions;

    private MqttAsyncClient client;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final Map<String, Integer> subscribedTopics = new ConcurrentHashMap<>();

    // 回调函数
    private Consumer<Object[]> messageCallback;
    private Consumer<Throwable> errorCallback;
    private Consumer<Boolean> connectionCallback;

    /**
     * 构造函数
     * @param broker MQTT服务器地址，格式为tcp://host:port
     * @param clientId 客户端ID
     * @param username 用户名
     * @param password 密码
     */
    public Mqtt5Publisher(String broker, String clientId, String username, String password) {
        this.broker = broker;
        this.clientId = clientId;
        this.username = username;
        this.password = password != null ? password.toCharArray() : null;
        this.connectionOptions = createConnectionOptions();
    }

    /**
     * 连接到MQTT服务器
     * @throws MqttException 连接异常
     * @throws InterruptedException 线程中断异常
     */
    public synchronized void connect() throws MqttException, InterruptedException {
        if (isConnected.get()) {
            LOGGER.log(Level.INFO, "已连接到MQTT服务器，无需重复连接");
            return;
        }

        try {
            // 创建客户端实例
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttAsyncClient(broker, clientId, persistence);

            // 设置默认回调
            client.setCallback(new InternalMqttCallback());

            LOGGER.log(Level.INFO, "连接到MQTT服务器: {0}", broker);
            IMqttToken token = client.connect(connectionOptions);
            token.waitForCompletion(5000); // 等待连接完成，超时5秒

            isConnected.set(true);
            LOGGER.info("连接成功");

            // 触发连接回调
            notifyConnectionCallback(true);
        } catch (MqttException e) {
            isConnected.set(false);
            LOGGER.log(Level.SEVERE, "连接MQTT服务器失败: " + e.getMessage(), e);

            // 触发错误回调
            notifyErrorCallback(e);
            throw e;
        }
    }

    /**
     * 订阅主题
     * @param topic 要订阅的主题
     * @param qos QoS级别
     * @throws MqttException MQTT异常
     * @throws InterruptedException 线程中断异常
     */
    public void subscribe(String topic, int qos) throws MqttException, InterruptedException {
        checkConnection();
        try {
            LOGGER.log(Level.INFO, "订阅主题: {0}, QoS: {1}", new Object[]{topic, qos});
            IMqttToken token = client.subscribe(topic, qos);
            token.waitForCompletion(3000);
            subscribedTopics.put(topic, qos);
            LOGGER.log(Level.INFO, "订阅成功");
        } catch (MqttException e) {
            LOGGER.log(Level.SEVERE, "订阅主题失败: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 批量订阅主题
     * @param topics 主题和QoS级别的映射
     * @throws MqttException MQTT异常
     * @throws InterruptedException 线程中断异常
     */
    public void subscribe(Map<String, Integer> topics) throws MqttException, InterruptedException {
        checkConnection();

        if (topics == null || topics.isEmpty()) {
            return;
        }

        try {
            String[] topicArray = topics.keySet().toArray(new String[0]);
            int[] qosArray = topics.values().stream().mapToInt(Integer::intValue).toArray();

            LOGGER.log(Level.INFO, "批量订阅主题: {0}", topics.keySet());
            IMqttToken token = client.subscribe(topicArray, qosArray);
            token.waitForCompletion(3000);
            subscribedTopics.putAll(topics);
            LOGGER.log(Level.INFO, "批量订阅成功");
        } catch (MqttException e) {
            LOGGER.log(Level.SEVERE, "批量订阅主题失败: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 取消订阅主题
     * @param topic 要取消订阅的主题
     * @throws MqttException MQTT异常
     * @throws InterruptedException 线程中断异常
     */
    public void unsubscribe(String topic) throws MqttException, InterruptedException {
        checkConnection();
        try {
            LOGGER.log(Level.INFO, "取消订阅主题: {0}", topic);
            IMqttToken token = client.unsubscribe(topic);
            token.waitForCompletion(3000);
            subscribedTopics.remove(topic);
            LOGGER.log(Level.INFO, "取消订阅成功");
        } catch (MqttException e) {
            LOGGER.log(Level.SEVERE, "取消订阅主题失败: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发布消息
     * @param topic 消息主题
     * @param content 消息内容
     * @param qos QoS级别
     * @param retained 是否保留消息
     * @param messageExpiryInterval 消息过期时间(秒)，null表示不使用
     * @throws MqttException MQTT异常
     * @throws InterruptedException 线程中断异常
     */
    public void publish(String topic, String content, int qos, boolean retained, Long messageExpiryInterval)
            throws MqttException, InterruptedException {
        checkConnection();

        try {
            // 创建消息
            MqttMessage message = createMqttMessage(content, qos, retained, messageExpiryInterval);

            LOGGER.log(Level.INFO, "发布消息到主题: {0}", topic);
            LOGGER.log(Level.FINE, "消息内容: {0}", content);

            // 发布消息
            IMqttToken token = client.publish(topic, message);
            token.waitForCompletion(3000); // 等待发布完成，超时3秒
            LOGGER.log(Level.INFO, "消息发布成功，消息ID: {0}", token.getMessageId());
        } catch (MqttException e) {
            LOGGER.log(Level.SEVERE, "发布消息失败: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发布消息（简化版）
     */
    public void publish(String topic, String content, int qos, boolean retained)
            throws MqttException, InterruptedException {
        publish(topic, content, qos, retained, 3600L);
    }

    /**
     * 断开与服务器的连接
     */
    public synchronized void disconnect() {
        if (!isConnected.get() || client == null) {
            return;
        }

        try {
            LOGGER.info("断开与MQTT服务器的连接");
            client.disconnect().waitForCompletion(2000);
            client.close();
            LOGGER.info("已断开连接");

            // 触发连接回调
            notifyConnectionCallback(false);
        } catch (MqttException e) {
            LOGGER.log(Level.WARNING, "断开连接时发生错误: " + e.getMessage(), e);
            notifyErrorCallback(e);
        } finally {
            isConnected.set(false);
            client = null;
            subscribedTopics.clear();
        }
    }

    /**
     * 重新连接
     * @throws MqttException MQTT异常
     * @throws InterruptedException 线程中断异常
     */
    public synchronized void reconnect() throws MqttException, InterruptedException {
        disconnect();
        connect();

        // 重新订阅之前的主题
        if (!subscribedTopics.isEmpty()) {
            subscribe(subscribedTopics);
        }
    }

    /**
     * 设置消息接收回调
     * @param callback 消息接收回调函数
     */
    public void setMessageCallback(Consumer<Object[]> callback) {
        this.messageCallback = callback;
    }

    /**
     * 设置错误回调
     * @param callback 错误回调函数
     */
    public void setErrorCallback(Consumer<Throwable> callback) {
        this.errorCallback = callback;
    }

    /**
     * 设置连接状态回调
     * @param callback 连接状态回调函数
     */
    public void setConnectionCallback(Consumer<Boolean> callback) {
        this.connectionCallback = callback;
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return isConnected.get();
    }

    /**
     * 获取客户端实例
     */
    public MqttAsyncClient getClient() {
        return client;
    }

    /**
     * 获取已订阅的主题
     */
    public Map<String, Integer> getSubscribedTopics() {
        return new ConcurrentHashMap<>(subscribedTopics);
    }

    /**
     * 创建MQTT消息
     */
    private MqttMessage createMqttMessage(String content, int qos, boolean retained, Long messageExpiryInterval) {
        MqttMessage message = new MqttMessage(content.getBytes(StandardCharsets.UTF_8));
        message.setQos(qos);
        message.setRetained(retained);

        // 添加MQTT 5.0特有的属性
        if (messageExpiryInterval != null) {
            MqttProperties properties = new MqttProperties();
            properties.setMessageExpiryInterval(messageExpiryInterval);
            message.setProperties(properties);
        }

        return message;
    }

    /**
     * 创建连接选项
     */
    private MqttConnectionOptions createConnectionOptions() {
        MqttConnectionOptions connOpts = new MqttConnectionOptions();
        connOpts.setCleanStart(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password != null ? new String(password).getBytes(StandardCharsets.UTF_8) : null);
        connOpts.setConnectionTimeout(10); // 连接超时时间(秒)
        connOpts.setKeepAliveInterval(30); // 心跳间隔(秒)

        // 设置自动重连
        connOpts.setAutomaticReconnect(true);
        connOpts.setMaxReconnectDelay(30); // 最大重连延迟30秒

        return connOpts;
    }

    /**
     * 检查连接状态
     */
    private void checkConnection() {
        if (!isConnected.get()) {
            throw new IllegalStateException("尚未连接到MQTT服务器，请先调用connect()方法");
        }
    }

    /**
     * 通知连接回调
     */
    private void notifyConnectionCallback(boolean connected) {
        if (connectionCallback != null) {
            try {
                connectionCallback.accept(connected);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "连接回调执行失败", e);
            }
        }
    }

    /**
     * 通知错误回调
     */
    private void notifyErrorCallback(Throwable error) {
        if (errorCallback != null) {
            try {
                errorCallback.accept(error);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "错误回调执行失败", e);
            }
        }
    }

    /**
     * 通知消息回调
     */
    private void notifyMessageCallback(Object[] message) {
        if (messageCallback != null) {
            try {
                messageCallback.accept(message);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "消息回调执行失败", e);
            }
        }
    }

    /**
     * 内部MQTT回调类
     */
    private class InternalMqttCallback implements MqttCallback {

        @Override
        public void disconnected(MqttDisconnectResponse disconnectResponse) {
            String reason = disconnectResponse != null ?
                    disconnectResponse.getException() != null ?
                            disconnectResponse.getException().getMessage() :
                            "原因码: " + disconnectResponse.getReturnCode() :
                    "未知原因";

            LOGGER.log(Level.WARNING, "与MQTT服务器断开连接: {0}", reason);
            isConnected.set(false);

            // 触发连接回调
            notifyConnectionCallback(false);

            // 触发错误回调
            if (disconnectResponse != null && disconnectResponse.getException() != null) {
                notifyErrorCallback(disconnectResponse.getException());
            }
        }

        @Override
        public void mqttErrorOccurred(org.eclipse.paho.mqttv5.common.MqttException exception) {
            LOGGER.log(Level.SEVERE, "MQTT错误发生: {0}", exception.getMessage());
            notifyErrorCallback(exception);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            LOGGER.log(Level.INFO, "收到消息 - 主题: {0}, QoS: {1}", new Object[]{topic, message.getQos()});
            LOGGER.log(Level.FINE, "消息内容: {0}", new String(message.getPayload(), StandardCharsets.UTF_8));

            // 触发消息接收回调
            notifyMessageCallback(new Object[]{topic, message});
        }

        @Override
        public void deliveryComplete(IMqttToken token) {
            try {
                LOGGER.log(Level.INFO, "消息投递完成，消息ID: {0}", token.getMessageId());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "获取消息ID失败", e);
            }
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            LOGGER.log(Level.INFO, "连接完成 - 重连: {0}, 服务器: {1}", new Object[]{reconnect, serverURI});
            isConnected.set(true);
            notifyConnectionCallback(true);
        }

        @Override
        public void authPacketArrived(int reasonCode, MqttProperties properties) {
            LOGGER.log(Level.INFO, "认证包到达 - 原因码: {0}", reasonCode);
        }
    }

    /**
     * 资源清理
     */
    @Override
    public void close() {
        disconnect();
    }

}