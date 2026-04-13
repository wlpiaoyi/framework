package org.wlpiaoyi.framework.meta.mqtt;

import com.google.gson.Gson;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-08-27 10:14:43</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class AppRun {

    private static final Logger LOGGER = Logger.getLogger(AppRun.class.getName());


    /**
     * 示例用法
     */
    public static void main(String[] args) {
        try {
            // 从配置文件加载配置
            AppConfig config = ConfigLoader.loadConfig(DataUtils.USER_DIR + "/fw_config/mqtt/data.json");

            AppConfig.MqttConfig mqttConfig = config.getMqtt();
            List<AppConfig.DeviceConfig> deviceConfigs = config.getDevices();
            String superTemplateId = config.getSuperTemplateId();
            String superDeviceId = config.getSuperDeviceId();

            try (Mqtt5Publisher publisher = new Mqtt5Publisher(
                    mqttConfig.getBroker(),
                    mqttConfig.getClientId(),
                    mqttConfig.getUsername(),
                    mqttConfig.getPassword()
            )) {
                Gson gson = GsonBuilder.gsonDefault();
                // 设置回调函数
                publisher.setMessageCallback(message -> {
                    String topic = (String) message[0];
                    MqttMessage msg = (MqttMessage) message[1];
                    LOGGER.log(Level.INFO, "收到消息: {0}", gson.fromJson(new String(msg.getPayload(), StandardCharsets.UTF_8), Map.class));
                    LOGGER.log(Level.INFO, "QoS: {0}", msg.getQos());
                    // 回复消息
                    String replyTopic = topic + "/reply";
                    try {
                        publisher.publish(replyTopic, new String(msg.getPayload(), StandardCharsets.UTF_8), 1, false);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "回复消息失败", e);
                    }
                });

                publisher.setErrorCallback(error -> {
                    LOGGER.log(Level.WARNING, "发生错误", error);
                });

                publisher.setConnectionCallback(connected -> {
                    LOGGER.log(Level.INFO, connected ? "已连接到MQTT服务器" : "与MQTT服务器断开连接");
                });

                // 连接到服务器
                publisher.connect();
                TimeUnit.SECONDS.sleep(1);
                // 订阅主题
                String subscribeTopic = Mqtt5Utils.GetSubscribeTopic(superTemplateId, superDeviceId);
//                publisher.subscribe(subscribeTopic, 1);
                LOGGER.log(Level.INFO, "成功订阅主题: {0}", subscribeTopic);
                List<ExecutorService> executorServices = new ArrayList<>();
                deviceConfigs.forEach(deviceConfig -> {
                    String nodeCode = deviceConfig.getNodeCode();
                    String deviceId = deviceConfig.getDeviceId();
                    var executorProperties = Executors.newVirtualThreadPerTaskExecutor();
                    executorProperties.submit(() -> {
                        try {
                            Mqtt5Utils.ReportEvents(LOGGER, publisher, deviceConfig, superTemplateId, superDeviceId, deviceId, nodeCode, gson);
                            // 发布测试消息
                            while (true) {
                                Mqtt5Utils.ReportProperties(LOGGER, publisher, deviceConfig, superTemplateId, superDeviceId, deviceId, nodeCode, gson);
                                // 间隔25秒发送下一条
                                TimeUnit.SECONDS.sleep(25);
                            }
                        } catch(Exception e) {
                            LOGGER.log(Level.SEVERE, "MQTT Device操作失败", e);
                        }
                    });
                    executorServices.add(executorProperties);
                });
                executorServices.forEach(executorService -> {
                    // 等待任务完成（可选）
                    executorService.shutdown();
                    try {
                        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.WARNING, "主线程被中断", e);
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "MQTT操作失败", e);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "加载配置文件失败", e);
        }
    }
}