package org.wlpiaoyi.framework.generator.plugin.model;

import lombok.Builder;
import lombok.Data;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.Properties;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/8 16:01
 * {@code @version:}:       1.0
 */
@Data
public class ConfigModel {

    private String url;
    private String userName;
    private String password;
    private String databaseName;
    private String tablePrefix;
    private String tableNamePattern;
    private String packagePath;
    private String projectName;
    private String excludeColumns;
    private String classVersion;

    public String getBizPackagePath(){
        if(ValueUtils.isBlank(this.getTablePrefix())){
            return this.getPackagePath();
        }
        return this.getPackagePath() + "." + this.getTablePrefix();
    }

    public ConfigModel(Properties properties){
        this.url = properties.getProperty("url");
        this.userName = properties.getProperty("userName");
        this.password = properties.getProperty("password");
        this.databaseName = properties.getProperty("databaseName");
        this.tablePrefix = properties.getProperty("tablePrefix");
        this.tableNamePattern = properties.getProperty("tableNamePattern");
        this.packagePath = properties.getProperty("packagePath");
        this.projectName = properties.getProperty("projectName");
        this.excludeColumns = properties.getProperty("excludeColumns");
        this.classVersion = properties.getProperty("classVersion", "1.0");
    }

}
