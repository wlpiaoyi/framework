package org.wlpiaoyi.framework.generator.plugin;

import lombok.Data;
import org.wlpiaoyi.framework.generator.plugin.model.ConfigModel;
import org.wlpiaoyi.framework.generator.plugin.utils.CommentEnumParse;
import org.wlpiaoyi.framework.generator.plugin.utils.PluginUtils;
import org.wlpiaoyi.framework.generator.plugin.utils.StructureConstant;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.wlpiaoyi.framework.generator.plugin.utils.PluginUtils.*;


public class PluginEnums {

    private ConfigModel configModel;
    private final String templatePath;
    private final List<Map<String, String>> templateList;

    private static final String SLASH_ARG = "\\";
//    private final String classVersion;

    public PluginEnums(String templatePath, ConfigModel configModel){
        this.configModel = configModel;
        this.templatePath = templatePath + SLASH_ARG + "/##package##";
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
                if(!fileName.endsWith(".en")){
                    continue;
                }
                String text = DataUtils.readFile(subFile.getAbsolutePath());
                templateList.add(new HashMap(){{
                    put("fileName", fileName);
                    put("dirName", dirName);
                    put("text", text);
                }});
            }
        }
    }
    private final Set<String> runedSet = new HashSet<>();

    public void run(CommentEnumParse.ParseResult parseResult){
        for (Map<String, String> templateDict : this.templateList){

            String fileName = templateDict.get("fileName").replace("##className##", parseResult.getName());
            fileName = fileName.substring(0, fileName.length() - 3);
            String className = fileName.substring(0, fileName.lastIndexOf("."));
            String classText = templateDict.get("text");
            String oname = this.configModel.getProjectName();
            if(!ValueUtils.isBlank(oname)){
                oname += SLASH_ARG;
            }
            String filePath = DataUtils.USER_DIR +
                    SLASH_ARG + "target" + SLASH_ARG + "generator" + SLASH_ARG + "output" + SLASH_ARG +
                    oname + (this.configModel.getBizPackagePath() + "." + templateDict.get("dirName")).replace(".", SLASH_ARG);
            DataUtils.makeDir(filePath);
            if(runedSet.contains(className)){
                continue;
            }
            runedSet.add(className);
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
            String enumType = parseResult.isBooleanType() ? "Boolean" : parseResult.isIntType() ? "Integer" : "String";
            String enumName = parseResult.getName();

            classText = classText.replace(StructureConstant.AUTHOR, pcUserName + ":" + pcComputerName);
            classText = classText.replace(StructureConstant.VERSION, this.configModel.getClassVersion());
            classText = classText.replaceAll(StructureConstant.PACKAGE, this.configModel.getPackagePath());
            classText = classText.replaceAll(StructureConstant.BIZ_TAG, this.configModel.getBusinessTag());
            classText = classText.replaceAll(StructureConstant.ENUM_NAME, enumName);
            classText = classText.replaceAll(StructureConstant.ENUM_TYPE, enumType);
            classText = classText.replaceAll(StructureConstant.ENUM_COMMENT, parseResult.getDesc());
            classText = replaceForeachEnum(parseResult, classText);
            DataUtils.writeFile(classText, filePath + SLASH_ARG + fileName);


        }
    }


    @Data
    private class Range{
        private int startIndex;
        private int endIndex;
        String separator;
        private List<String> lines = new ArrayList<>();
    }

    private static final Pattern FOREACH_COLUMN_ATTRIBUTE_SEPARATOR_PATTERN = Pattern.compile(
            "separator=\"[0-9a-zA-Z,.:;%&*(){}\\[\\]=+\\-\\\"\\' ]*");
    private String replaceForeachEnum(CommentEnumParse.ParseResult parseResult, String classText){
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
            if(line.contains("<foreach-enum>") || line.contains("<foreach-enum ")){
                if(range != null){
                    throw new BusinessException("foreach-enum 格式错误");
                }
                range = new Range();
                range.setStartIndex(i);
                range.separator = null;
                if(line.contains("separator=\"")){
                    Matcher matcher = FOREACH_COLUMN_ATTRIBUTE_SEPARATOR_PATTERN.matcher(line);
                    while (matcher.find()) {
                        int start = matcher.start() + 11;
                        int end = matcher.end() - 1;
                        range.separator = line.substring(start, end);
                        break;
                    }
                }
                continue;
            }
            if(line.contains("</foreach-enum>")){
                range.setEndIndex(i);
                ranges.add(0, range);
                range = null;
                continue;
            }
            if(range != null){
                range.getLines().add(line);
            }
        }
        int cIndex = 0;
        for (Range r : ranges){
            lines.remove(r.getEndIndex());
            r.getLines().forEach(line -> {
                lines.remove(r.getStartIndex() + 1);
            });
            for (Map.Entry<String, String> entry : parseResult.getStringCodePairs().entrySet()){
                for (String line : r.getLines()){
                    String vLine = line.trim();
                    if(r.getLines().getLast() == line){
                        if(ValueUtils.isNotBlank(r.getSeparator()))
                            line = line.replace(vLine, vLine + r.getSeparator());
                    }
                    String v = line.replace(StructureConstant.ENUM_INS_CODE, entry.getValue());
                    v = v.replace(StructureConstant.ENUM_INS_VALUE, entry.getKey());
                    v = v.replace(StructureConstant.ENUM_INS_DESC, "\"" + parseResult.getStringDescPairs().get(entry.getKey()) + "\"");
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
