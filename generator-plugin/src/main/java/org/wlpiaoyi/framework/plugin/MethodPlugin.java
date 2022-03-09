package org.wlpiaoyi.framework.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/3/9 2:54 PM
 * @Version 1.0
 */
public class MethodPlugin extends PluginAdapter {

    private final static String valueUtilsPackage = "org.wlpiaoyi.framework.utils.ValueUtils";
    private final static FullyQualifiedJavaType booleanReturnJavaType = new FullyQualifiedJavaType(
            "boolean");

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(valueUtilsPackage);
        this.addValueBlank(topLevelClass, introspectedTable);
        this.addEquals(topLevelClass, introspectedTable);
        return true;
    }

    private final static FullyQualifiedJavaType equalsParameterJavaType = new FullyQualifiedJavaType(
            "Object");
    private void addEquals(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("equals");
        method.setReturnType(booleanReturnJavaType);
        method.addParameter(new Parameter(equalsParameterJavaType, "obj"));

        StringBuilder sb = new StringBuilder();
        sb.append("if(ValueUtils.isBlank(this.getId())){");
        sb.append("\n\t\t\treturn super.equals(obj);");
        sb.append("\n\t\t}");

        sb.append("\n\t\tif(obj == null){");
        sb.append("\n\t\t\treturn false;");
        sb.append("\n\t\t}");

        sb.append("\n\t\tif(!(obj instanceof " + topLevelClass.getType().getShortName() + ")){");
        sb.append("\n\t\t\treturn super.equals(obj);");
        sb.append("\n\t\t}");

        sb.append("\n\t\tif(ValueUtils.isBlank(((" + topLevelClass.getType().getShortName() + ") obj).getId())){");
        sb.append("\n\t\t\treturn super.equals(obj);");
        sb.append("\n\t\t}");


        sb.append("\n\t\treturn this.getId().equals(((Rule) obj).getId());");
        method.addBodyLine(sb.toString());
        topLevelClass.addMethod(method);
    }

    private final static FullyQualifiedJavaType valueBlankInterfaceJavaType = new FullyQualifiedJavaType(
            "ValueUtils.ValueBlank");
    private void addValueBlank(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addSuperInterface(valueBlankInterfaceJavaType);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("isBlank");
        method.setReturnType(booleanReturnJavaType);
        StringBuilder sb = new StringBuilder();
        sb.append("boolean isNull;\n");
        for (Field field : topLevelClass.getFields()){
            if(field.isStatic() == true) continue;
            String name = field.getName();
            String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            sb.append("\t\tisNull = ValueUtils.isBlank(this." + methodName + "());\n");
            sb.append("\t\tif(!isNull) return false;\n");
        }
        sb.append("\t\treturn true;");
        method.addBodyLine(sb.toString());
        sb = new StringBuilder();
        sb.append("/**");
        sb.append("\n\t * @describe if all value is null or empty then should be return true otherwise return false ");
        sb.append("\n\t **/");
        method.addJavaDocLine(sb.toString());
        topLevelClass.addMethod(method);
    }
}
