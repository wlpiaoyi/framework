package org.wlpiaoyi.framework.utils.xml;

import org.xml.sax.Attributes;

/**
 * XML解析回调接口
 *
 * <p><b>{@code @author:}</b>wlpiaoyi</p><p>
 * <b>{@code @description:}</b>
 * 定义XML解析过程中的生命周期回调方法，
 * 用于在SAX解析的不同阶段处理数据
 * </p>
 * <p><b>{@code @date:}</b>2026-02-04 11:11:07</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public interface XmlParsing {

    /**
     * XML文档开始解析时的回调
     *
     * @param userData 用户自定义数据，可在整个解析过程中传递和修改
     */
    void startDocument(Object userData);

    /**
     * XML文档解析结束时的回调
     *
     * @param userData 用户自定义数据，包含最终解析结果
     */
    void endDocument(Object userData);

    /**
     * 遇到XML元素开始标签时的回调
     *
     * @param userData 用户自定义数据
     * @param qName 元素限定名（带命名空间前缀）
     * @param attributes 元素属性集合
     * @param xmlDeep 当前元素在XML树中的深度（根元素为0）
     */
    void startElement(Object userData, String qName, Attributes attributes, int xmlDeep);

    /**
     * 遇到XML元素结束标签时的回调
     *
     * @param userData 用户自定义数据
     * @param qName 元素限定名（带命名空间前缀）
     * @param text 元素的文本内容（已trim处理）
     * @param xmlDeep 当前元素在XML树中的深度（根元素为0）
     */
    void endElement(Object userData, String qName, String text, int xmlDeep);

    /**
     * 可选：处理命名空间映射
     *
     * @param userData 用户自定义数据
     * @param prefix 命名空间前缀
     * @param uri 命名空间URI
     * @param xmlDeep 当前深度
     */
    default void startPrefixMapping(Object userData, String prefix, String uri, int xmlDeep) {
        // 默认实现，子类可选择重写
    }

    /**
     * 可选：命名空间映射结束
     *
     * @param userData 用户自定义数据
     * @param prefix 命名空间前缀
     * @param xmlDeep 当前深度
     */
    default void endPrefixMapping(Object userData, String prefix, int xmlDeep) {
        // 默认实现，子类可选择重写
    }
}