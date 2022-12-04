package org.wlpiaoyi.framework.mybatis.generator;

import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;


public class CommentGenerator extends DefaultCommentGenerator {
    private Properties properties;
    private Properties systemPro;
    private boolean suppressDate;
    private boolean suppressAllComments;
    private String currentDateStr;

    public CommentGenerator() {
        super();
        properties = new Properties();
        systemPro = System.getProperties();
        suppressDate = false;
        suppressAllComments = false;
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }


    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        if(introspectedColumn.getRemarks() == null || introspectedColumn.getRemarks().length() == 0){
            return;
        }
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * @describe ");
        sb.append(introspectedColumn.getRemarks());
        field.addJavaDocLine(sb.toString().replace("\n", " "));
        field.addJavaDocLine(" */");
    }

    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * @describe ");
        if(method.getName().startsWith("count")){
            sb.append("统计");
        }else if(method.getName().startsWith("delete")){
            sb.append("删除");
        }else if(method.getName().startsWith("insert")){
            sb.append("新增");
        }else if(method.getName().startsWith("update")){
            sb.append("修改");
        }else if(method.getName().startsWith("select")){
            sb.append("查询");
        }
//        sb.append(introspectedTable.getFullyQualifiedTable());
        method.addJavaDocLine(sb.toString().replace("\n", " "));
        method.addJavaDocLine(" */");

    }

    // 设置实体类 getter注释
    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
                method.addJavaDocLine("//获取:" + introspectedColumn.getRemarks());
            }
        }else {
            super.addGetterComment(method, introspectedTable, introspectedColumn);
        }
    }

    // 设置实体类 setter注释
    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
                method.addJavaDocLine("//设置:" + introspectedColumn.getRemarks());
            }
        }else {
            super.addSetterComment(method, introspectedTable, introspectedColumn);
        }
    }

    // 去掉mapping原始注释
    @Override
    public void addComment(XmlElement xmlElement) {
        if (suppressAllComments) {
            return;
        } else {
            super.addComment(xmlElement);
        }
    }


}