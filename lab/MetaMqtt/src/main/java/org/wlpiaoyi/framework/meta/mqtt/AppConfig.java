package org.wlpiaoyi.framework.meta.mqtt;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-08-27 10:43:40</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Data
public class AppConfig {

    private MqttConfig mqtt;
    private List<DeviceConfig> devices;

    @JsonProperty("superTemplateId")
    private String superTemplateId;

    @JsonProperty("superDeviceId")
    private String superDeviceId;



    @Data
    public static class MqttConfig {
        private String broker;
        private String clientId;
        private String username;
        private String password;

    }

    @Data
    public static class DeviceConfig {


        @JsonProperty("nodeCode")
        private String nodeCode;

        @JsonProperty("deviceId")
        private String deviceId;

        private List<ModelConfig> models;


    }


    @Data
    public static class ModelConfig {

        @JsonProperty("abilityCode")
        private String abilityCode;

        @JsonProperty("reportProperties")
        private Object reportProperties;

        @JsonProperty("reportEvents")
        private List<Object> reportEvents;

    }
}