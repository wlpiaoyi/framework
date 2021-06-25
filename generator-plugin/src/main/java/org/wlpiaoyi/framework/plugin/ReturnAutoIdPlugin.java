package org.wlpiaoyi.framework.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class ReturnAutoIdPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }

    private void execute(XmlElement element,
                         IntrospectedTable introspectedTable){

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if(primaryKeyColumns.isEmpty()) return;

        IntrospectedColumn primaryKeyColumn = primaryKeyColumns.get(0);

        element.addAttribute(new Attribute("keyColumn", primaryKeyColumn.getActualColumnName()));
        element.addAttribute(new Attribute("useGeneratedKeys", "true"));
        element.addAttribute(new Attribute("keyProperty", primaryKeyColumn.getJavaProperty()));

    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable){
        this.execute(element, introspectedTable);
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable){
        this.execute(element, introspectedTable);
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

}
