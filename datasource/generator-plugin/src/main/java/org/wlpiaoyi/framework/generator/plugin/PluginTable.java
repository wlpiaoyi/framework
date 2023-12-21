package org.wlpiaoyi.framework.generator.plugin;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import org.wlpiaoyi.framework.generator.plugin.model.ConfigModel;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.sql.*;
import java.util.*;

public class PluginTable {


    private final ConfigModel configModel;
    private Connection connection;

    private DatabaseMetaData metaData;

    public PluginTable(ConfigModel configModel) {
        this.configModel = configModel;
    }

    public void start() throws SQLException {
        this.connection = DriverManager.getConnection(this.configModel.getUrl(),
                this.configModel.getUserName(), this.configModel.getPassword());
        this.metaData = this.connection.getMetaData();
    }

    public void end() throws SQLException {
        this.connection.close();
    }
    private static List<Map<String, String>> iteratorTable(String tablePrefix, ResultSet tableRet) throws SQLException {
        List<Map<String, String>> res = new ArrayList<>();
        while (tableRet.next()) {
            final String tableName = tableRet.getString("TABLE_NAME");
            final String suffixName = tableName.startsWith(tablePrefix) ?
                    tableName.substring(tablePrefix.length() + 1) : tableName;
            final String className = parseStringToHump(suffixName, true);
            final String varClassName = parseStringToHump(suffixName, false);
            final String tableComment = tableRet.getString("REMARKS");
            res.add(new HashMap(){{
                put("suffixName", suffixName);
                put("tableName", tableName);
                put("className", className);
                put("varClassName", varClassName);
                put("comment", tableComment);
            }});
        }
        return res;
    }

    public static List<Map<String, String>> iteratorColumn(ResultSet columnRet) throws SQLException {
        List<Map<String, String>> res = new ArrayList<>();
        Set<String> keySet = new HashSet<>();
        while (columnRet.next()) {
            final String columnName = columnRet.getString("COLUMN_NAME");
            if(keySet.contains(columnName)){ continue; }
            keySet.add(columnName);
            final String propertyName = parseStringToHump(columnName, false);
            final String columnType = columnRet.getString("TYPE_NAME");
            final String columnComment = columnRet.getString("REMARKS");
            final int columnSize = columnRet.getInt("COLUMN_SIZE");
            final int nullable = columnRet.getInt("NULLABLE");
            final int precision = columnRet.getInt("DECIMAL_DIGITS");
            res.add(new HashMap(){{
                put("columnName", columnName);
                put("propertyName", propertyName);
                put("columnSize", columnSize);
                put("columnType", columnType);
                put("nullable", nullable);
                put("precision", precision);
                put("comment", columnComment);
            }});
        }
        return res;
    }
    private static String parseStringToHump(String value, boolean isUpFirst){
        StringBuilder res = new StringBuilder();
        int index = 0;
        for (String arg : value.split("_")) {
            String first = arg.substring(0, 1);
            if(!isUpFirst && index == 0){
                res.append(first);
            } else {
                res.append(first.toUpperCase(Locale.ROOT));
            }
            String end = arg.substring(1);
            res.append(end);
            index ++;
        }
        return res.toString();
    }

    private static final String TABLE_NAME_PATTERN_SPLIT = ",";
    public Map<String, Map<String, Object>> run() throws SQLException {
        Map<String, Map<String, Object>> resDict = new HashMap<>(10);
        this.start();
        try{
            List<Map<String, String>> tableDicts = new ArrayList<>();
            if(this.configModel.getTableNamePattern().contains(TABLE_NAME_PATTERN_SPLIT)){
                for (String name : this.configModel.getTableNamePattern().split(TABLE_NAME_PATTERN_SPLIT)) {
                    ResultSet tableRet = metaData.getTables(this.configModel.getDatabaseName(), null, name,
                            new String[]{"TABLE"});
                    tableDicts.addAll(PluginTable.iteratorTable(this.configModel.getTablePrefix(), tableRet));
                }
            }else{
                ResultSet tableRet = metaData.getTables(this.configModel.getDatabaseName(), null, this.configModel.getTableNamePattern(),
                        new String[]{"TABLE"});
                tableDicts.addAll(PluginTable.iteratorTable(this.configModel.getTablePrefix(), tableRet));
            }
            for (Map<String, String> tableDict : tableDicts) {
                String tableName = tableDict.get("tableName");
                ResultSet columnRet = metaData.getColumns(null, this.configModel.getDatabaseName(), tableName, "%");
                List<Map<String, String>> columnDict = PluginTable.iteratorColumn(columnRet);
                resDict.put(tableName,
                        new HashMap(2){{
                            put("table", tableDict);
                            put("columns", columnDict);
                        }});
            }
        }finally {
            this.end();
        }
        return resDict;
    }

    public static void main(String[] args) throws SQLException {
        ConfigModel configModel = new ConfigModel();
        configModel.setUrl("jdbc:mysql://36.138.30.68:13306/filling_dev?useSSL=false&useUnicode=true&characterEncoding=utf-8&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8");
        configModel.setDatabaseName("filling_dev");
        configModel.setUserName("root");
        configModel.setPassword("zrgj@2022*");
        configModel.setTablePrefix("poi");
        configModel.setTableNamePattern("poi_form_data_statistics");
        configModel.setPackagePath("com.icss.poie.biz");
        configModel.setProjectName("poie");
        configModel.setExcludeColumns("id,create_user,create_time,update_time,update_user,status,create_dept,is_deleted");
        PluginTable plugin = new PluginTable(configModel);
        Map<String, Map<String, Object>> resDict = plugin.run();
        System.out.println(resDict);
    }

}


