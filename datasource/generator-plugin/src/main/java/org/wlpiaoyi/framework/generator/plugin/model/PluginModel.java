package org.wlpiaoyi.framework.generator.plugin.model;

import lombok.Getter;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-10-11 12:02:00</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Getter
public class PluginModel {

    // 字段类型
    private Map<String, String> columnTypeDict = new HashMap();
    // 引入类型
    private Map<String, String> implTypeDict = new HashMap();

    private Map<String, String> implDecorateDict = new HashMap();
    private Map<String, String> fieldDecorateDict = new HashMap();

    private Map<String, String> implValidDict = new HashMap();
    private Map<String, String> msgValidDict = new HashMap();

    private static final PluginModel instance = new PluginModel();

    private PluginModel() {}

    public static PluginModel getInstance() {
        return instance;
    }

    public static void loadData(String pluginJson) throws NoSuchFieldException, IllegalAccessException {
        if(ValueUtils.isNotBlank(pluginJson)){
            Map pluginMap = GsonBuilder.gsonDefault().fromJson(pluginJson, Map.class);
            for(Object key : pluginMap.keySet()){
                Field field = PluginModel.class.getDeclaredField(key.toString());
                Map value = MapUtils.get(pluginMap, key);
                field.setAccessible(true);
                field.set(PluginModel.getInstance(), value);
                field.setAccessible(false);
            }
        }else throw new NoSuchFieldException("插件Json配置数据为空");
    }

}
