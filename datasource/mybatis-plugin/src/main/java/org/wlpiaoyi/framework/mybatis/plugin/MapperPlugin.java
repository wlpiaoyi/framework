package org.wlpiaoyi.framework.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.*;


/**
 *
 * @Author wlpiaoyi
 * @Date 2022/3/9 2:55 PM
 * @Version 1.0
 */
public class MapperPlugin extends PluginAdapter {

    private String userName = null;


    /** exclude specified columns from the mapper xml **/
    private List<String> excludeColumns = new ArrayList<String>(){{
        add("create_time");
        add("update_time");
    }};

    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        if(properties.getProperty("excludeColumns") != null){
            String text = String.valueOf(properties.getProperty("excludeColumns")); //$NON-NLS-1$
            this.excludeColumns = Arrays.asList(text.split(",").clone());
        }
        Map<String,String> map = System.getenv();
        this.userName = map.get("USERNAME");
        super.setProperties(properties);
        if(properties.getProperty("userName") != null){
            this.userName = String.valueOf(properties.getProperty("userName")); //$NON-NLS-1$
        }
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element,
                                                IntrospectedTable introspectedTable){
        element.getElements().clear();

        TextElement ele = new TextElement("<!--");
        element.addElement(ele);
        ele = new TextElement("  WARNING - @" + this.userName);
        element.addElement(ele);
        ele = new TextElement("  This element is automatically generated by " + this.userName + ", do not modify.");
        element.addElement(ele);
        ele = new TextElement("-->");
        element.addElement(ele);

        ele = new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + " (");
        element.addElement(ele);

        String tag = null;
        StringBuffer sb = new StringBuffer();
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            if(this.excludeColumns.contains(column.getActualColumnName())) continue;
            if(tag == null) tag = ", ";
            else sb.append(tag);
            sb.append(column.getActualColumnName());
        }
        for (IntrospectedColumn column : introspectedTable.getBaseColumns()) {
            if(this.excludeColumns.contains(column.getActualColumnName())) continue;
            if(tag == null) tag = ", ";
            else sb.append(tag);
            sb.append(column.getActualColumnName());
        }
        ele = new TextElement(sb.toString());
        element.addElement(ele);
        ele = new TextElement(")");
        element.addElement(ele);

        tag = null;
        ele = new TextElement("values (");
        element.addElement(ele);sb = new StringBuffer();
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            if(this.excludeColumns.contains(column.getActualColumnName())) continue;
            if(tag == null) tag = ", ";
            else sb.append(tag);
            sb.append("#{");
            sb.append(column.getJavaProperty());
            sb.append(",jdbcType=");
            sb.append(column.getJdbcTypeName());
            sb.append("}");
        }
        for (IntrospectedColumn column : introspectedTable.getBaseColumns()) {
            if(this.excludeColumns.contains(column.getActualColumnName())) continue;
            if(tag == null) tag = ", ";
            else sb.append(tag);
            sb.append("#{");
            sb.append(column.getJavaProperty());
            sb.append(",jdbcType=");
            sb.append(column.getJdbcTypeName());
            sb.append("}");
        }
        ele = new TextElement(sb.toString());
        element.addElement(ele);
        ele = new TextElement(")");
        element.addElement(ele);
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        element.getElements().clear();

        TextElement ele = new TextElement("<!--");
        element.addElement(ele);
        ele = new TextElement("  WARNING - @wlpiaoyi");
        element.addElement(ele);
        ele = new TextElement("  This element is automatically generated by wlpiaoyi, do not modify.");
        element.addElement(ele);
        ele = new TextElement("-->");
        element.addElement(ele);

        this.executeUpdate(element, introspectedTable);
        return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }

    private void executeUpdate(
            XmlElement element, IntrospectedTable introspectedTable) {
        element.getElements().clear();

        TextElement ele = new TextElement("update " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + " set ");
        element.addElement(ele);

        String tag = null;
        StringBuffer sb = new StringBuffer();
        for (IntrospectedColumn column : introspectedTable.getBaseColumns()) {
            if(this.excludeColumns.contains(column.getActualColumnName())) continue;
            if(tag == null) tag = ",\n\t";
            else sb.append(tag);
            sb.append("\t");
            sb.append(column.getActualColumnName());
            sb.append(" = ");
            sb.append("#{");
            sb.append(column.getJavaProperty());
            sb.append(",jdbcType=");
            sb.append(column.getJdbcTypeName());
            sb.append("}");
        }
        ele = new TextElement(sb.toString());
        element.addElement(ele);

        tag = null;
        sb = new StringBuffer("where \n\t");
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            if(this.excludeColumns.contains(column.getActualColumnName())) continue;
            if(tag == null) tag = "\n\t,";
            else sb.append(tag);
            sb.append("\t");
            sb.append(column.getActualColumnName());
            sb.append(" = ");
            sb.append("#{");
            sb.append(column.getJavaProperty());
            sb.append(",jdbcType=");
            sb.append(column.getJdbcTypeName());
            sb.append("}");
        }
        ele = new TextElement(sb.toString());
        element.addElement(ele);
    }

    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable){
        List<XmlElement> elements = new ArrayList<>();
        for (Element ele : element.getElements()) {
            if(ele instanceof TextElement) continue;
            if(ele instanceof  XmlElement){
                XmlElement xmlElement = (XmlElement) ele;
                if(xmlElement.getName().equals("if")){
                    elements.add(xmlElement);
                }
            }
        }
        element.getElements().clear();
        TextElement ele = new TextElement("<!--");
        element.addElement(ele);
        ele = new TextElement("  WARNING - @wlpiaoyi");
        element.addElement(ele);
        ele = new TextElement("  This element is automatically generated by wlpiaoyi, do not modify.");
        element.addElement(ele);
        ele = new TextElement("-->");
        element.addElement(ele);

        this.executeUpdate(element, introspectedTable);
        for (XmlElement xmle : elements) {
            element.addElement(xmle);
        }

        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }
}
