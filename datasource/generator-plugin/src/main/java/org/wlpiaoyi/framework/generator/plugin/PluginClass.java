package org.wlpiaoyi.framework.generator.plugin;

import lombok.Data;
import org.wlpiaoyi.framework.generator.plugin.model.ConfigModel;
import org.wlpiaoyi.framework.generator.plugin.utils.StructureConstant;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;


public class PluginClass {

    private static final Map<String, String> columnTypeDict = new HashMap(){{
        put("CHAR", "String");
        put("VARCHAR", "String");
        put("LONGTEXT", "String");
        put("BLOB", "String");
        put("TINYINT", "Byte");
        put("TINYINT UNSIGNED", "Byte");
        put("SMALLINT", "Short");
        put("SMALLINT UNSIGNED", "Short");
        put("MEDIUMINT", "Short");
        put("MEDIUMINT UNSIGNED", "Short");
        put("INT", "Integer");
        put("INT UNSIGNED", "Integer");
        put("BIGINT", "Long");
        put("BIGINT UNSIGNED", "Long");
        put("DECIMAL", "Double");
        put("DATETIME", "Date");
    }};

    private static final Map<String, String> implTypeDict = new HashMap(){{
        put("DATETIME", "java.util.Date");
    }};


    private static final Map<String, String> implDecorateDict = new HashMap(){{
        put("BIGINT", "com.fasterxml.jackson.databind.annotation.JsonSerialize," +
                "com.fasterxml.jackson.databind.ser.std.ToStringSerializer");
        put("BIGINT UNSIGNED", "com.fasterxml.jackson.databind.annotation.JsonSerialize," +
                "com.fasterxml.jackson.databind.ser.std.ToStringSerializer");
        put("DATETIME", "com.fasterxml.jackson.annotation.JsonFormat," +
                "org.springframework.format.annotation.DateTimeFormat");
    }};
    private static final Map<String, String> fieldDecorateDict = new HashMap(){{
        put("BIGINT", "@JsonSerialize(using = ToStringSerializer.class)");
        put("BIGINT UNSIGNED", "@JsonSerialize(using = ToStringSerializer.class)");
        put("DATETIME", "@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")" +
                "##tabArgs##@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
    }};

    private static final Map<String, String> implValidDict = new HashMap(){{
        put("CHAR", "javax.validation.constraints.NotBlank");
        put("VARCHAR", "javax.validation.constraints.NotBlank");
        put("BLOB", "javax.validation.constraints.NotBlank");
        put("LONGTEXT", "javax.validation.constraints.NotBlank");
        put("TINYINT", "javax.validation.constraints.NotNull");
        put("TINYINT UNSIGNED", "javax.validation.constraints.NotNull");
        put("SMALLINT", "javax.validation.constraints.NotNull");
        put("SMALLINT UNSIGNED", "javax.validation.constraints.NotNull");
        put("MEDIUMINT", "javax.validation.constraints.NotNull");
        put("MEDIUMINT UNSIGNED", "javax.validation.constraints.NotNull");
        put("INT", "javax.validation.constraints.NotNull");
        put("INT UNSIGNED", "javax.validation.constraints.NotNull");
        put("BIGINT", "javax.validation.constraints.NotNull");
        put("BIGINT UNSIGNED", "javax.validation.constraints.NotNull");
        put("DECIMAL", "javax.validation.constraints.NotNull");
        put("DATETIME", "javax.validation.constraints.NotNull");
    }};
    private static final Map<String, String> msgValidDict = new HashMap(){{
        put("CHAR", "@NotBlank(message = \"__comment__不能为空\")");
        put("VARCHAR", "@NotBlank(message = \"__comment__不能为空\")");
        put("BLOB", "@NotBlank(message = \"__comment__不能为空\")");
        put("LONGTEXT", "@NotBlank(message = \"__comment__不能为空\")");
        put("TINYINT", "@NotNull(message = \"__comment__不能为空\")");
        put("TINYINT UNSIGNED", "@NotNull(message = \"__comment__不能为空\")");
        put("SMALLINT", "@NotNull(message = \"__comment__不能为空\"");
        put("SMALLINT UNSIGNED", "@NotNull(message = \"__comment__不能为空\"");
        put("MEDIUMINT", "@NotNull(message = \"__comment__不能为空\"");
        put("MEDIUMINT UNSIGNED", "@NotNull(message = \"__comment__不能为空\"");
        put("INT", "@NotNull(message = \"__comment__不能为空\")");
        put("INT UNSIGNED", "@NotNull(message = \"__comment__不能为空\")");
        put("BIGINT", "@NotNull(message = \"__comment__不能为空\")");
        put("BIGINT UNSIGNED", "@NotNull(message = \"__comment__不能为空\")");
        put("DECIMAL", "@NotNull(message = \"__comment__不能为空\")");
        put("DATETIME", "@NotNull(message = \"__comment__不能为空\")");
    }};

    private ConfigModel configModel;
    private final PluginTable pluginTable;
    private final String templatePath;
//    private final String name;
//    private final String packagePath;
//    private final List<String> excludeColumn;
    private final List<Map<String, String>> templateList;

    private static final String SLASH_ARG = "\\";
//    private final String classVersion;

    public PluginClass(PluginTable pluginTable, String templatePath, ConfigModel configModel){
        this.configModel = configModel;
        this.templatePath = templatePath + SLASH_ARG + "/##package##";
        this.pluginTable = pluginTable;
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
                    subDirName += SLASH_ARG + subFile.getName();
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
        String text = templateDict.get("text");
        String classText = text.replaceAll(StructureConstant.TABLE_NAME, table.get("tableName"));
        classText = classText.replaceAll(StructureConstant.TABLE_COMMENT, table.get("comment"));
        classText = classText.replaceAll(StructureConstant.OBJECT_NAME, table.get("suffixName"));
        classText = classText.replaceAll(StructureConstant.CLASS_NAME, table.get("className"));
        classText = classText.replaceAll(StructureConstant.VAR_CLASS_NAME, table.get("varClassName"));
        classText = classText.replaceAll(StructureConstant.PACKAGE, this.configModel.getPackagePath());
        classText = classText.replaceAll(StructureConstant.BIZ_TAG, this.configModel.getTablePrefix());
        return classText;
    }

    @Data
    private class Range{
        boolean excludeFlag = true;
        private int startIndex;
        private int endIndex;
        private List<String> lines = new ArrayList<>();
    }


    private String replaceForeachColumn(List<Map<String, Object>> columns, List<String> imports, int tabNum, String classText){
        List<String> lines = new ArrayList(){{
            addAll(Arrays.asList(classText.split("\n")));
        }};
        List<Range> ranges = new ArrayList<>();
        Range range = null;
        for (int i = 0; i < lines.size(); i ++){
            String line = lines.get(i);
            if(lines.indexOf(line) > 40){
                System.out.println();
            }
            if(line.contains("<foreach-column>") || line.contains("<foreach-column ")){
                if(range != null){
                    throw new BusinessException("foreach-column 格式错误");
                }
                range = new Range();
                range.setStartIndex(i);
                if(line.contains("excludeFlag=false")){
                    range.excludeFlag = false;
                }
                continue;
            }
            if(line.contains("</foreach-column>")){
                range.setEndIndex(i);
                ranges.add(0, range);
                range = null;
                continue;
            }
            if(range != null){
                range.getLines().add(line);
            }
        }
        List<String> excludeColumn = ValueUtils.toStringList(this.configModel.getExcludeColumns());
        for (Range r : ranges){
            lines.remove(r.getEndIndex());

            for (String line : r.getLines()){
                lines.remove(r.getStartIndex() + 1);
            }
            int cIndex = 0;
            for (Map<String, Object> colMap : columns) {
                String columnName = (String) colMap.get("columnName");
                if(columnName.equals("id")){
                    continue;
                }
                if(r.excludeFlag && excludeColumn.contains(columnName))
                    continue;
                for (String line : r.getLines()){
                    String v = getFieldText(colMap, imports, line);
                    if(v == null){
                        continue;
                    }
                    cIndex ++;
                    lines.add(r.getStartIndex() + cIndex, v);
                }

            }
            lines.remove(r.getStartIndex());
        }
        StringBuffer res = new StringBuffer();
        for (String line : lines){
            res.append('\n');
            res.append(line);
        }
        return res.substring(1);
    }


    private String getFieldText(Map<String, Object> colMap , List<String> imports, String line){
        String tabArgs = "\n";
        for (char c: line.toCharArray()){
            if(c != '\t' && c != ' ' && c != '\r'){
                break;
            }
            tabArgs += c;
        }
        StringBuffer fieldsText = new StringBuffer();
        String columnName = (String) colMap.get("columnName");
        String propertyName = (String) colMap.get("propertyName");
        String comment = (String) colMap.get("comment");
        String columnType = ((String) colMap.get("columnType")).toUpperCase();
        String propertyType = columnTypeDict.get(columnType);
        if(ValueUtils.isBlank(comment)){
            comment = propertyName;
        }
        String implType = implTypeDict.get(columnType);
//        if(this.excludeColumn.contains(columnName))
//            return null;

        if(!ValueUtils.isBlank(implType)){
            if(!imports.contains(implType))
                imports.add(implType);
        }


        fieldsText.append("@Schema(description = \"" + comment + "\")");

        Integer nullable = (Integer) colMap.get("nullable");
        if(nullable == 0){
            String anStr = msgValidDict.get(columnType);
            if(ValueUtils.isBlank(anStr)) {
                throw new BusinessException("msgValidDict不支持的类型:" + columnType);
            }
            fieldsText.append(tabArgs);
            fieldsText.append(anStr.replace("__comment__", comment));
            String importStr = implValidDict.get(columnType);
            if(ValueUtils.isBlank(importStr))
                throw new BusinessException("implValidDict不支持的类型:" + columnType);
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
            fieldDec = fieldDec.replaceAll(StructureConstant.TAB_ARGS, tabArgs);
            for (String arg :
                    fieldDec.split(",")) {
                fieldsText.append(tabArgs);
                fieldsText.append(arg);
            }
        }
        String res = new String(line);
        res = res.replaceAll(StructureConstant.PROPERTY_TYPE, propertyType);
        res = res.replaceAll(StructureConstant.PROPERTY_NAME, propertyName);
        res = res.replaceAll(StructureConstant.COLUMN_COMMENT, comment);
        res = res.replaceAll(StructureConstant.PROPERTY_ANNOTATIONS, fieldsText.toString());
        res = res.replaceAll(StructureConstant.COLUMN_NAME, columnName);

        return res;
    }

    public void run() throws SQLException {
        Map<String, Map<String, Object>> resDict = this.pluginTable.run();

        for (String key : resDict.keySet()) {
            Map<String, Object> res = resDict.get(key);
            List<Map<String, Object>> columns = (List<Map<String, Object>>) res.get("columns");
            Map<String, String> table = (Map<String, String>) res.get("table");
            for (Map<String, String> templateDict : this.templateList) {
                String classText = getClassText(templateDict, table);
                String packageStr = this.configModel.getBizPackagePath();

                List<String> imports = new ArrayList<>();
                classText = this.replaceForeachColumn(columns, imports, 1, classText);

                String importsStr = "";
                for (String impStr : imports){
                    importsStr += "import " + impStr + ";\n";
                }

                classText = classText.replace(StructureConstant.IMPORT, importsStr);
                classText = classText.replace(StructureConstant.CREATE_TIME, DateUtils.formatLocalDateTime(LocalDateTime.now()));
                Map<String, String> map = System.getenv();
                String pcUserName = map.get("USERNAME");
                String pcComputerName = map.get("COMPUTERNAME");
                if(ValueUtils.isBlank(pcUserName)){
                    pcUserName = "unkown";
                }
                if(ValueUtils.isBlank(pcUserName)){
                    pcUserName = "unkown";
                }

                classText = classText.replace(StructureConstant.AUTHOR, pcUserName + ":" + pcComputerName);
                classText = classText.replace(StructureConstant.VERSION, this.configModel.getClassVersion());


                String fileName = templateDict.get("fileName").replace("##className##", table.get("className"));
                fileName = fileName.substring(0, fileName.length() - 3);
                String filePackage = packageStr + "." + templateDict.get("dirName");
                String oname = this.configModel.getProjectName();
                if(!ValueUtils.isBlank(oname)){
                    oname += SLASH_ARG;
                }
                String filePath = DataUtils.USER_DIR +
                        SLASH_ARG + "target" + SLASH_ARG + "generator" + SLASH_ARG + "output" + SLASH_ARG +
                        oname + filePackage.replace(".", SLASH_ARG);
                DataUtils.makeDir(filePath);
                DataUtils.writeFile(classText, filePath + SLASH_ARG + fileName);

                System.out.println(fileName);
                System.out.println(filePackage);
                System.out.println(filePath);
                System.out.println(classText);
            }
            System.out.println();
        }
    }

//    public static void main(String[] args) throws SQLException {
//
//        final String url = "jdbc:mysql://127.0.0.1:3306/jfr_dev?characterEncoding=utf8&&useInformationSchema=true";
//        final String databaseName = "jfr_dev";
//        final String userName = "root";
//        final String password = "00000000";
//        final String tablePrefix = "biz";
//        final String tableNamePattern = "biz_%";
//        PluginTable plugin = new PluginTable(url, userName, password, databaseName, tablePrefix, tableNamePattern);
//        String templatePath = DataUtils.USER_DIR + "/generator-plugin/src/main/resources/template/jfr";
//        String packagePath = "org.out.work.jfr.biz";
//        List<String> excludeColumn = new ArrayList(){{
//            add("create_user");
//            add("create_time");
//            add("update_time");
//            add("update_user");
//            add("status");
//            add("create_dept");
//            add("id");
//            add("is_deleted");
//        }};
//        PluginClass pluginClass = new PluginClass(plugin, templatePath, "jfr", packagePath, excludeColumn, "1.1");
//        pluginClass.run();
//
//        System.out.println();
//    }
}
