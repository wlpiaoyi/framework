package org.wlpiaoyi.framework.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/3/9 6:21 PM
 * @Version 1.0
 */
public class XmlMapperPlugin  extends PluginAdapter {

    private boolean delPrefix = true;
    private String suffix = "Mapper.xml";

    @Override
    public boolean validate(List<String> warnings) {
        String delPrefix = properties.getProperty("delPrefix");
        if(!ValueUtils.isBlank(delPrefix)){
            this.delPrefix = new Boolean(properties.getProperty("delPrefix"));
        }
        String suffix = properties.getProperty("suffix");
        if(!ValueUtils.isBlank(suffix)){
            this.suffix = properties.getProperty("suffix");
        }
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String name = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        if(this.delPrefix){
            name = name.substring(name.indexOf("_") + 1);
        }
        name = StringUtils.toHump(name);
        introspectedTable.setMyBatis3XmlMapperFileName(name + this.suffix);
    }
}
