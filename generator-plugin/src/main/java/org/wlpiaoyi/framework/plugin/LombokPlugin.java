package org.wlpiaoyi.framework.plugin;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.plugins.SerializablePlugin;


import java.text.SimpleDateFormat;
import java.util.*;

public class LombokPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }


    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        return true;
    }

    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        interfaze.addJavaDocLine("/**");
//        interfaze.addJavaDocLine("* Created by Mybatis Generator " + this.date2Str(new Date()));
//        interfaze.addJavaDocLine("*/");
        return true;
    }

    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }
}