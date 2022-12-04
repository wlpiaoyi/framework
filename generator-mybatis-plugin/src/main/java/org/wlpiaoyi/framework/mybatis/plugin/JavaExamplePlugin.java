package org.wlpiaoyi.framework.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.wlpiaoyi.framework.mybatis.utils.StringUtils;
import org.wlpiaoyi.framework.mybatis.utils.ValueUtils;

import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/3/9 5:21 PM
 * @Version 1.0
 */
public class JavaExamplePlugin extends PluginAdapter {

    private boolean delPrefix = true;
    private String suffix = "example";
    private String packagePath = null;

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

        String packagePath = properties.getProperty("packagePath");
        if(!ValueUtils.isBlank(packagePath)){
            this.packagePath = properties.getProperty("packagePath");
        }
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String oldType = introspectedTable.getExampleType();
        String packagePath = this.packagePath;
        if(ValueUtils.isBlank(packagePath)){
            packagePath = oldType.substring(0, oldType.lastIndexOf("."));
        }
        String name = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        if(this.delPrefix){
            name = name.substring(name.indexOf("_") + 1);
        }
        name = StringUtils.toHump(name + "_" + this.suffix);
        introspectedTable.setExampleType(packagePath + "." + name);


        oldType = introspectedTable.getBaseRecordType();
        packagePath = oldType.substring(0, oldType.lastIndexOf("."));
        name = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        if(this.delPrefix){
            name = name.substring(name.indexOf("_") + 1);
        }
        name = StringUtils.toHump(name);
        introspectedTable.setBaseRecordType(packagePath + "." + name);
    }
}