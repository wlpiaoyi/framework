package org.wlpiaoyi.framework.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class ReturnAutoIdPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable){
        element.addAttribute(new Attribute("keyColumn", "id"));
        element.addAttribute(new Attribute("useGeneratedKeys", "true"));
        element.addAttribute(new Attribute("keyProperty", "id"));
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable){
        element.addAttribute(new Attribute("keyColumn", "id"));
        element.addAttribute(new Attribute("useGeneratedKeys", "true"));
        element.addAttribute(new Attribute("keyProperty", "id"));
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

}
