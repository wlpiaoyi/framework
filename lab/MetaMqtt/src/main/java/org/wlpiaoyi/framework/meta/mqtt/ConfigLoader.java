package org.wlpiaoyi.framework.meta.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * <p<b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b> </p>
 * <p><b>{@code @date:}</b>2025/8/27 10:44</p>
 * <p><b>{@code @version:}</b>1.0</p>
 */
public class ConfigLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AppConfig loadConfig(String configPath) throws Exception {
        try{
            InputStream inputStream = new FileInputStream(DataUtils.loadFile(configPath));
            return objectMapper.readValue(inputStream, AppConfig.class);
        }catch (Exception e){
            throw new RuntimeException("配置文件未找到: " + configPath, e);
        }


    }

    public static AppConfig loadConfig() throws Exception {
        return loadConfig("data.json");
    }
}
