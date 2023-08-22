package org.wlpiaoyi.framework.mybatis.plugin;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

public class UnmergeableXmlMappersPlugin extends PluginAdapter {

    public UnmergeableXmlMappersPlugin() {
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        sqlMap.setMergeable(false);
        return true;
    }
}
