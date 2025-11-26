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

    //数据库连接信息
    private String url;
    //数据库用户名
    private String userName;
    //数据库密码
    private String password;
    //数据库名称
    private String databaseName;
    //业务标签
    private String businessTag;
    //表前缀
    private String tablePrefix;
    //表名模式
    private String tableNamePattern;
    //包路径
    private String packagePath;
    //项目名称
    private String projectName;
    //排除字段
    private String excludeColumns;
    //类版本
    private String classVersion;

    public String getBizPackagePath(){
        if(ValueUtils.isBlank(this.getBusinessTag())){
            return this.getPackagePath();
        }
        return this.getPackagePath() + "." + this.getBusinessTag();
    }

    public ConfigModel(Properties properties){
        this.url = properties.getProperty("url");
        this.userName = properties.getProperty("userName");
        this.password = properties.getProperty("password");
        this.databaseName = properties.getProperty("databaseName");
        this.businessTag = properties.getProperty("businessTag");
        this.tablePrefix = properties.getProperty("tablePrefix");
        this.tableNamePattern = properties.getProperty("tableNamePattern");
        this.packagePath = properties.getProperty("packagePath");
        this.projectName = properties.getProperty("projectName");
        this.excludeColumns = properties.getProperty("excludeColumns");
        this.classVersion = properties.getProperty("classVersion", "1.0");
    }

}
