package org.wlpiaoyi.framework.generator.plugin;

import java.sql.*;
import java.util.*;

public class PluginTable {


    private final String url;
    private final String userName;
    private final String password;
    private final String tablePrefix;

    private final String tableNamePattern;

    private Connection connection;

    private DatabaseMetaData metaData;

    public PluginTable(String url, String userName, String password, String tablePrefix, String tableNamePattern) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.tablePrefix = tablePrefix;
        this.tableNamePattern = tableNamePattern;
    }

    public void start() throws SQLException {
        this.connection = DriverManager.getConnection(url,
                userName, password);
        this.metaData = this.connection.getMetaData();
    }

    public void end() throws SQLException {
        this.connection.close();
    }

    public List<Map<String, String>> iteratorTable() throws SQLException {
        ResultSet tableRet = metaData.getTables(null, "%", this.tableNamePattern,
                new String[]{"TABLE"});
        List<Map<String, String>> res = new ArrayList<>();
        while (tableRet.next()) {
            final String tableName = tableRet.getString("TABLE_NAME");
            final String suffixName = tableName.substring(this.tablePrefix.length() + 1);
            final String className = parseStringToHump(suffixName, true);
            final String classVarName = parseStringToHump(suffixName, false);
            final String tableComment = tableRet.getString("REMARKS");
            res.add(new HashMap(){{
                put("suffixName", suffixName);
                put("tableName", tableName);
                put("className", className);
                put("classVarName", classVarName);
                put("comment", tableComment);
            }});
        }
        return res;
    }
    private static final String parseStringToHump(String value, boolean isUpFirst){
        String res = isUpFirst ? "" : null;
        for (String arg : value.split("_")) {
            if(res == null){
                res = arg;
            }else{
                res += arg.substring(0, 1).toUpperCase(Locale.ROOT) + arg.substring(1);
            }
        }
        return res;
    }

    public List<Map<String, String>> iteratorColumn(Map<String, String> tableDict) throws SQLException {
        String tableName = tableDict.get("tableName");
        ResultSet columnRet = metaData.getColumns(null, "%", tableName, "%");

        List<Map<String, String>> res = new ArrayList<>();
        while (columnRet.next()) {
            final String columnName = columnRet.getString("COLUMN_NAME");
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

    public Map<String, Map<String, Object>> run() throws SQLException {
        Map<String, Map<String, Object>> resDict = new HashMap<>();
        this.start();
        try{
            List<Map<String, String>> tableDict = this.iteratorTable();
            for (Map<String, String> dict : tableDict) {
                List<Map<String, String>> columnDict = this.iteratorColumn(dict);
                resDict.put(dict.get("tableName"),
                        new HashMap(){{
                            put("table", dict);
                            put("columns", columnDict);
                        }});
            }
        }finally {
            this.end();
        }
        return resDict;
    }

//    public static void main(String[] args) throws SQLException {
//
//        final String url = "jdbc:mysql://36.138.30.68:3306/bladex?characterEncoding=utf8&&useInformationSchema=true";
//        final String userName = "root";
//        final String password = "zrgj@2022*";
//        final String tablePrefix = "dym";
//        PluginTable plugin = new PluginTable(url, userName, password, tablePrefix);
//        Map<String, Map<String, List<Map<String, String>>>> resDict = plugin.run();
//        System.out.println();
//    }

}
