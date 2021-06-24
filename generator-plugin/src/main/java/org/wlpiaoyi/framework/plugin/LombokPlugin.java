package org.wlpiaoyi.framework.plugin;


import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LombokPlugin extends PluginAdapter {

    public LombokPlugin() {
    }

    public boolean validate(List<String> list) {
        return true;
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        //topLevelClass.addImportedType("lombok.Getter");
        //topLevelClass.addImportedType("lombok.Setter");
        //topLevelClass.addImportedType("lombok.ToString");
        //topLevelClass.addAnnotation("@Getter");
        //topLevelClass.addAnnotation("@Setter");
        //topLevelClass.addAnnotation("@ToString");

        Map<String, String> map = System.getenv();
        String userName = map.get("USER");// 获取// 用户名

        topLevelClass.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append("@author by ");
        sb.append(userName);
        sb.append(".");
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));

        sb = new StringBuilder();
        sb.append(" * @describe ");
        sb.append(introspectedTable.getRemarks());
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));

        sb = new StringBuilder();
        sb.append(" * ");
        sb.append("@date ");
        sb.append(this.dateStr(new Date()));
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));

        sb = new StringBuilder();
        sb.append(" * ");
        sb.append("@version 1.0");
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));

        topLevelClass.addJavaDocLine(" */");

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

    private String dateStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }
}