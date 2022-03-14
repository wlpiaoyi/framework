package org.wlpiaoyi.framework.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.wlpiaoyi.framework.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * document describe for version username date time and so on
 * @Author wlpiaoyi
 * @Date 2022/3/9 3:02 PM
 * @Version 1.0
 */
public class DocPlugin  extends PluginAdapter{

    private String version = "1.0";
    private String userName = null;

    @Override
    public void setProperties(Properties properties) {
        Map<String,String> map = System.getenv();
        this.userName = map.get("USERNAME");
        super.setProperties(properties);
        if(properties.getProperty("version") != null){
            this.version = String.valueOf(properties.getProperty("version")); //$NON-NLS-1$
        }
        if(properties.getProperty("userName") != null){
            this.userName = String.valueOf(properties.getProperty("userName")); //$NON-NLS-1$
        }
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        Map<String, String> map = System.getenv();
        String userName = this.userName == null ? map.get("USER") : this.userName;// 获取// 用户名
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
        sb.append(DateUtils.formatLocalDate(LocalDate.now()));
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));

        sb = new StringBuilder();
        sb.append(" * ");
        sb.append("@time ");
        sb.append(DateUtils.formatLocalTime(LocalTime.now()));
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));

        sb = new StringBuilder();
        sb.append(" * ");
        sb.append("@version " + this.version);
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));

        topLevelClass.addJavaDocLine(" */");
        return true;
    }


    private String dateStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }

}
