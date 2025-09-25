package org.wlpiaoyi.framework.meta.mqtt;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-08-27 14:48:28</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class Mqtt5Utils {

    /**
     * <p><b>{@code @description:}</b>
     * 获取订阅主题
     * </p>
     *
     * <p><b>@param</b> <b>superTemplateId</b>
     * {@link String}
     * 超级模板Id
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * 超级设备Id
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 14:29</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String GetSubscribeTopic(String superTemplateId, String superDeviceId){
        return "/" + superTemplateId + "/" + superDeviceId + "/#";
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取推送属性值主题T
     * </p>
     *
     * <p><b>@param</b> <b>superTemplateId</b>
     * {@link String}
     * 超级模板Id
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * 超级设备Id
     * </p>
     *
     * <p><b>@param</b> <b>abilityCode</b>
     * {@link String}
     * 能力值
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 14:37</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String GetPublishPropertiesReportTopic(String superTemplateId, String superDeviceId, String abilityCode){
        return "/" + superTemplateId + "/" + superDeviceId + "/" + abilityCode + "/properties/report";
    }

    public static String GetPublishEventReportTopic(String superTemplateId, String superDeviceId, String abilityCode){
        return "/" + superTemplateId + "/" + superDeviceId + "/" + abilityCode + "/event";
    }

    /**
     * <p><b>{@code @description:}</b>
     * 属性上报内容构建器
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>abilityCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>deviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>nodeCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>properties</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 14:47</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String BuildPropertiesReportContent(String superDeviceId, String abilityCode, String deviceId, String nodeCode, String properties) {
        return """
        {
            "headers": {
                "version": "3.0"
            },
            "deviceId": "%s",
            "abilities": [
                {
                    "uri": {
                        "abilityCode": "%s",
                        "nodes": [
                            {
                                "deviceId": "%s",
                                "instanceName": "",
                                "nodeCode": "%s"
                            }
                        ]
                    },
                    properties:%s
                }
            ]
        }
        """.formatted(superDeviceId, abilityCode, deviceId, nodeCode, properties);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 事件上报内容构建器
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>abilityCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>deviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>nodeCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>eventCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>eventData</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 15:16</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String BuildEventReportContent(String superDeviceId, String abilityCode, String deviceId, String nodeCode, String eventCode, String eventData) {
        return """
        {
            "headers": {
                "version": "3.0"
            },
            "deviceId": "%s",
            "abilities": [
                {
                    "uri": {
                        "abilityCode": "%s",
                        "nodes": [
                            {
                                "deviceId": "%s",
                                "instanceName": "",
                                "nodeCode": "%s"
                            }
                        ]
                    },
                    "event":"%s",
                    "data": %s
                }
            ]
        }
        """.formatted(superDeviceId, abilityCode, deviceId, nodeCode, eventCode, eventData);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 事件上报
     * </p>
     *
     * <p><b>@param</b> <b>LOGGER</b>
     * {@link Logger}
     * </p>
     *
     * <p><b>@param</b> <b>publisher</b>
     * {@link Mqtt5Publisher}
     * </p>
     *
     * <p><b>@param</b> <b>deviceConfig</b>
     * {@link AppConfig.DeviceConfig}
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>superTemplateId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>deviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>nodeCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>gson</b>
     * {@link Gson}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/28 9:59</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void ReportEvents(Logger LOGGER,
                                    Mqtt5Publisher publisher,
                                    AppConfig.DeviceConfig deviceConfig,
                                    String superDeviceId,
                                    String superTemplateId,
                                    String deviceId,
                                    String nodeCode,
                                    Gson gson) {
        deviceConfig.getModels().forEach(modelConfig -> {
            String abilityCode = modelConfig.getAbilityCode();
            if(ValueUtils.isNotBlank(modelConfig.getReportEvents())) modelConfig.getReportEvents().forEach(reportEvent -> {
                String eventCode = MapUtils.getValueByKeyPath((Map) reportEvent, "code", null, String.class);
                Map eventData = MapUtils.getValueByKeyPath((Map) reportEvent, "data", null, Map.class);
                if(ValueUtils.isNotBlank(eventCode) && ValueUtils.isNotBlank(eventData)){
                    String content = Mqtt5Utils.BuildEventReportContent(
                            superDeviceId,
                            abilityCode,
                            deviceId,
                            nodeCode,
                            eventCode,
                            gson.toJson(eventData)
                    );

                    try {
                        publisher.publish(
                                Mqtt5Utils.GetPublishEventReportTopic(superTemplateId, superDeviceId, abilityCode),
                                content, 1, false);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.WARNING, "虚拟线程被中断", e);
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "MQTT Device Event 操作失败", e);
                    }
                    LOGGER.log(Level.INFO, "已发送事件:{0}", content);
                };
            });
        });
    }

    /**
     * <p><b>{@code @description:}</b>
     * 属性上报
     * </p>
     *
     * <p><b>@param</b> <b>LOGGER</b>
     * {@link Logger}
     * </p>
     *
     * <p><b>@param</b> <b>publisher</b>
     * {@link Mqtt5Publisher}
     * </p>
     *
     * <p><b>@param</b> <b>deviceConfig</b>
     * {@link AppConfig.DeviceConfig}
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>superTemplateId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>deviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>nodeCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>gson</b>
     * {@link Gson}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/28 10:00</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void ReportProperties(Logger LOGGER,
                                        Mqtt5Publisher publisher,
                                        AppConfig.DeviceConfig deviceConfig,
                                        String superDeviceId,
                                        String superTemplateId,
                                        String deviceId,
                                        String nodeCode,
                                        Gson gson) {

        deviceConfig.getModels().forEach(modelConfig -> {
            String abilityCode = modelConfig.getAbilityCode();
            String content = Mqtt5Utils.BuildPropertiesReportContent(
                    superDeviceId,
                    abilityCode,
                    deviceId,
                    nodeCode,
                    gson.toJson(modelConfig.getReportProperties())
            );

            try {
                publisher.publish(
                        Mqtt5Utils.GetPublishPropertiesReportTopic(superTemplateId, superDeviceId, abilityCode),
                        content, 1, false);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "虚拟线程被中断", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "MQTT Device Model 操作失败", e);
            }
            LOGGER.log(Level.INFO, "已发送消息:{0}", content);
        });
    }
}
