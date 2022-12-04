package org.wlpiaoyi.framework.generator.plugin;

import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class PluginClass {

    private static final Map<String, String> columnTypeDict = new HashMap(){{
        put("BIGINT", "Long");
        put("VARCHAR", "String");
        put("INT", "Integer");
        put("DATETIME", "Date");
    }};

    private static final Map<String, String> implTypeDict = new HashMap(){{
//        put("BIGINT", "java.lang.Long");
//        put("VARCHAR", "java.lang.String");
//        put("INT", "java.lang.Integer");
        put("DATETIME", "java.util.Date");
    }};


    private static final Map<String, String> implDecorateDict = new HashMap(){{
        put("BIGINT", "com.fasterxml.jackson.databind.annotation.JsonSerialize," +
                "com.fasterxml.jackson.databind.ser.std.ToStringSerializer");
        put("DATETIME", "com.fasterxml.jackson.annotation.JsonFormat," +
                "org.springframework.format.annotation.DateTimeFormat");
    }};
    private static final Map<String, String> fieldDecorateDict = new HashMap(){{
        put("BIGINT", "@JsonSerialize(using = ToStringSerializer.class)");
        put("DATETIME", "@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n" +
                "\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
    }};

    private static final Map<String, String> implValidDict = new HashMap(){{
        put("BIGINT", "javax.validation.constraints.NotNull");
        put("VARCHAR", "javax.validation.constraints.NotBlank");
        put("INT", "javax.validation.constraints.NotNull");
        put("DATETIME", "javax.validation.constraints.NotNull");
    }};
    private static final Map<String, String> msgValidDict = new HashMap(){{
        put("BIGINT", "@NotNull(message = \"__comment__不能为空\")");
        put("VARCHAR", "@NotBlank(message = \"__comment__不能为空\")");
        put("INT", "@NotNull(message = \"__comment__不能为空\")");
        put("DATETIME", "@NotNull(message = \"__comment__不能为空\")");
    }};

    private final PluginTable pluginTable;
    private final String templatePath;
    private final String name;
    private final String packagePath;
    private final List<String> excludeColumn;
    private final List<Map<String, String>> templateList;

    public PluginClass(PluginTable pluginTable, String templatePath, String name, String packagePath, List<String> excludeColumn){
        this.templatePath = templatePath + "\\__package__";
        this.packagePath = packagePath;
        this.name = name;
        this.pluginTable = pluginTable;
        this.excludeColumn = excludeColumn;
        File file = new File(this.templatePath);
        if(!file.exists())
            throw new RuntimeException(this.templatePath + "is not exists");
        if(!file.isDirectory())
            throw new RuntimeException(this.templatePath + "is not directory");
        File subFiles[] = file.listFiles();
        if(subFiles == null || subFiles.length == 0)
            throw new RuntimeException(this.templatePath + "is not has subFile");

        List<Map<String, String>> templateList = new ArrayList<>();
        this.templateList = templateList;
        this.iteratorInitTemplateList(templateList, subFiles, "");
    }

    private void iteratorInitTemplateList(List<Map<String, String>> templateList, File subFiles[], String dirName){
        if(subFiles == null || subFiles.length == 0)
            return;

        for (File subFile : subFiles) {
            if(subFile.isDirectory()){
                File subFile_subs[] = subFile.listFiles();
                String subDirName = dirName;
                if(ValueUtils.isBlank(subDirName))
                    subDirName = subFile.getName();
                else{
                    subDirName += "\\" + subFile.getName();
                }
                this.iteratorInitTemplateList(templateList, subFile_subs, subDirName);
            }else{
                String fileName = subFile.getName();
                String text = DataUtils.readFile(subFile.getAbsolutePath());
                templateList.add(new HashMap(){{
                    put("fileName", fileName);
                    put("dirName", dirName);
                    put("text", text);
                }});
            }
        }
    }

    private String getClassText(Map<String, String> templateDict, Map<String, String> table){
        String packageStr = this.packagePath;
        String text = templateDict.get("text");
        String classText = text.replace("__table_name__", table.get("tableName"));
        classText = classText.replace("__table_comment__", table.get("comment"));
        classText = classText.replace("__object_name__", table.get("suffixName"));
        classText = classText.replace("__class_name__", table.get("className"));
        classText = classText.replace("__class_var_name__", table.get("classVarName"));
        classText = classText.replace("__package__", packageStr);
        return classText;
    }

    private String getFieldsText(List<Map<String, Object>> columns, List<String> imports){
        StringBuffer fieldsText = new StringBuffer();

        for (Map<String, Object> colMap : columns) {

            String columnName = (String) colMap.get("columnName");
            String propertyName = (String) colMap.get("propertyName");
            String comment = (String) colMap.get("comment");
            String columnType = (String) colMap.get("columnType");
            if(ValueUtils.isBlank(comment)){
                comment = propertyName;
            }
            String implType = implTypeDict.get(columnType);

            if(this.excludeColumn.contains(columnName))
                continue;

            if(!ValueUtils.isBlank(implType)){
                if(!imports.contains(implType))
                    imports.add(implType);
            }

            fieldsText.append("\n\t/**");
            fieldsText.append("\n\t * ");
            fieldsText.append(comment);
            fieldsText.append("\n\t */");

            fieldsText.append("\n\t@ApiModelProperty(value = \"" + comment + "\")");

            Integer nullable = (Integer) colMap.get("nullable");
            if(nullable == 0){
                String anStr = msgValidDict.get(columnType);
                fieldsText.append("\n\t");
                fieldsText.append(anStr.replace("__comment__", comment));
                String importStr = implValidDict.get(columnType);
                if(!imports.contains(importStr)){
                    imports.add(importStr);
                }
            }

            String implDec = implDecorateDict.get(columnType);
            if(!ValueUtils.isBlank(implDec)){
                for (String arg :
                        implDec.split(",")) {
                    if(!imports.contains(arg)){
                        imports.add(arg);
                    }
                }
            }

            String fieldDec = fieldDecorateDict.get(columnType);
            if(!ValueUtils.isBlank(fieldDec)){
                for (String arg :
                        fieldDec.split(",")) {
                    fieldsText.append("\n\t");
                    fieldsText.append(arg);
                }
            }

            fieldsText.append("\n\tprivate ");
            fieldsText.append(columnTypeDict.get(columnType));
            fieldsText.append(" ");
            fieldsText.append(propertyName);
            fieldsText.append(";");
        }
        fieldsText.append("\n");

        return fieldsText.toString();
    }



    private String getResultText(List<Map<String, Object>> columns){
        StringBuffer resultText = new StringBuffer();

        for (Map<String, Object> colMap : columns) {
            String columnName = (String) colMap.get("columnName");
            String propertyName = (String) colMap.get("propertyName");
            if(propertyName.equals("id")){
                resultText.append("\n\t\t<id column=\"" + columnName + "\" property=\"" + propertyName + "\"/>");
            }else {
                resultText.append("\n\t\t<result column=\"" + columnName + "\" property=\"" + propertyName + "\"/>");
            }
        }
        return resultText.toString();
    }

    public void run() throws SQLException {
        Map<String, Map<String, Object>> resDict = this.pluginTable.run();

        for (String key : resDict.keySet()) {
            Map<String, Object> res = resDict.get(key);
            List<Map<String, Object>> columns = (List<Map<String, Object>>) res.get("columns");
            Map<String, String> table = (Map<String, String>) res.get("table");
            for (Map<String, String> templateDict : this.templateList) {
                String classText = getClassText(templateDict, table);
                String packageStr = this.packagePath;

                List<String> imports = new ArrayList<>();
                String fieldsText = this.getFieldsText(columns, imports);
                String resultText = this.getResultText(columns);

                String importsStr = "";
                for (String impStr : imports){
                    importsStr += "import " + impStr + ";\n";
                }

                classText = classText.replace("<!--<result_map_items/>-->", resultText);
                classText = classText.replace("/*__import__*/", importsStr);
                classText = classText.replace("/*__fields__*/", fieldsText);
                classText = classText.replace("__create_time__", DateUtils.formatLocalDateTime(LocalDateTime.now()));
                Map<String, String> map = System.getenv();
                String pcUserName = map.get("USERNAME");// 获取// 用户名
                if(ValueUtils.isBlank(pcUserName))
                    pcUserName = "unkown";

                classText = classText.replace("__author__", pcUserName);


                String fileName = templateDict.get("fileName").replace("__class_name__", table.get("className"));
                String filePackage = packageStr + "." + templateDict.get("dirName");
                String oname = this.name;
                if(!ValueUtils.isBlank(oname)){
                    oname += "\\";
                }
                String filePath = DataUtils.USER_DIR + "\\target\\generator\\output\\" + oname + filePackage.replace(".", "\\");
                DataUtils.makeDir(filePath);
                DataUtils.writeFile(classText, filePath + "\\" + fileName);

                System.out.println(fileName);
                System.out.println(filePackage);
                System.out.println(filePath);
                System.out.println(classText);
            }
            System.out.println();
        }
    }




    public static void main(String[] args) throws SQLException {

        final String url = "jdbc:mysql://36.138.30.68:3306/bladex?characterEncoding=utf8&&useInformationSchema=true";
        final String userName = "root";
        final String password = "zrgj@2022*";
        final String tablePrefix = "dym";
        final String tableNamePattern = "dym_%";
        PluginTable plugin = new PluginTable(url, userName, password, tablePrefix, tableNamePattern);
//        Map<String, Map<String, Object>> resDict = plugin.run();

        String templatePath = "C:\\Users\\wlpia\\Documents\\Develop\\Java\\framework\\generator-plugin\\src\\main\\resources\\template\\zhzf";
        String packagePath = "org.springblade.online";
        List<String> excludeColumn = new ArrayList(){{
            add("create_user");
            add("create_time");
            add("update_time");
            add("update_user");
            add("status");
            add("create_dept");
            add("id");
            add("is_deleted");
        }};
        PluginClass pluginClass = new PluginClass(plugin, templatePath, "zhzf", packagePath, excludeColumn);
        pluginClass.run();

        System.out.println();
    }
}
