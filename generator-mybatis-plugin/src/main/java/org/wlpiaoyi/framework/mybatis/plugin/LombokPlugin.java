package org.wlpiaoyi.framework.mybatis.plugin;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;


import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class LombokPlugin extends PluginAdapter {

    private boolean defaultLombokAnnotation = true;

    private Map classAnMap;

    public boolean validate(List<String> list) {
        new RuntimeException("test path").printStackTrace();
        String flag = this.properties.getProperty("defLombok");
        if(!ValueUtils.isBlank(flag)) this.defaultLombokAnnotation = flag.equals("true");

        try {
            String resourceClass = this.properties.getProperty("resourceClass");
            String path = DataUtils.USER_DIR + "/config/generator/model.json";;
//            if(!ValueUtils.isBlank(resourceClass) && Class.forName(resourceClass) != null){
//                URL url = ReaderUtils.getResource(Class.forName(resourceClass),"generator/model.json");
//                if(url != null){
//                    path = url.getPath();
//                }
//            }
//            if(ValueUtils.isBlank(path)){
//                path = DataUtils.USER_DIR + "/config/generator/model.json";
//            }
            this.classAnMap = ReaderUtils.loadMap(
                    path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            this.classAnMap = null;
        }
        return true;
    }


    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if(ValueUtils.isBlank(this.classAnMap)) return true;
        List<String> imports = MapUtils.getList(this.classAnMap, "imports", String.class);
        for (String impStr : imports){
            topLevelClass.addImportedType(impStr);
        }
        List<String> classAns = MapUtils.getList(this.classAnMap, "classAns", String.class);
        for (String classAn : classAns){
            if(classAn.contains("${TableNameValue}")){
                classAn = classAn.replace("${TableNameValue}", "\"" +
                        introspectedTable.getFullyQualifiedTable().getIntrospectedTableName()  + "\"");
            }
            topLevelClass.addAnnotation(classAn);
        }
        if(this.defaultLombokAnnotation){
            topLevelClass.addImportedType("lombok.Builder");
            topLevelClass.addImportedType("lombok.Data");
            topLevelClass.addAnnotation("@Data");
            topLevelClass.addAnnotation("@Builder");
        }
        return true;
    }
    @Override
    public boolean modelFieldGenerated(Field field,
                                       TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       Plugin.ModelClassType modelClassType) {
        if(ValueUtils.isBlank(this.classAnMap)) return true;
        Map models = MapUtils.get(this.classAnMap, "fieldsAns");
        if(ValueUtils.isBlank(models)) return true;
        List<String> anStrs = MapUtils.getList(models, field.getName(), String.class);
        if(!ValueUtils.isBlank(anStrs)){
            for (String anStr : anStrs){
                if(anStr.contains("${TableIdValue}")){
                    anStr = anStr.replace("${TableIdValue}", "\"" +
                            field.getName()  + "\"");

                }
                field.addAnnotation(anStr);
            }
        }
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