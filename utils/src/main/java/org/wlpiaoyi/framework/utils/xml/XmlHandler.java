package org.wlpiaoyi.framework.utils.xml;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

/**
 * SAX XML解析处理器
 *
 * <p>
 * <b>{@code @author:}</b>wlpiaoyi
 * </p>
 * <p>
 * <b>{@code @description:}</b>实现SAX解析的事件处理器，提供完整的XML解析生命周期管理，
 * 支持深度限制、命名空间处理、错误恢复等特性
 * </p>
 * <p>
 * <b>{@code @date:}</b>2026-02-04 10:39:52
 * </p>
 * <p>
 * <b>{@code @version:}:</b>2.0
 * </p>
 */
@Slf4j
class XmlHandler extends DefaultHandler {

    // ==================== 常量定义 ====================

    /** 默认最大XML深度 */
    private static final int DEFAULT_MAX_DEEP = 100;

    /** 默认元素名称数组初始大小 */
    private static final int DEFAULT_ELEMENT_CAPACITY = 128;

    // ==================== 成员变量 ====================

    /** 当前解析的文本内容缓冲区 */
    private final StringBuilder currentValue = new StringBuilder(1024);

    /** XML最大允许深度 */
    private final int maxDeep;

    /** 元素名称栈（跟踪当前路径） */
    private final String[] elementStack;

    /** 元素索引数组（记录同级元素的出现次数） */
    private final int[] elementIndexes;

    /** 命名空间前缀映射栈 */
    private final Deque<NamespaceContext> namespaceStack = new LinkedList<>();

    /** 当前XML深度（-1表示未开始，0表示根元素） */
    private int xmlDeep = -1;

    /** 用户自定义数据（解析过程中传递） */
    private final Object userData;

    /** XML解析回调接口 */
    private final XmlParsing xmlParsing;

    /** 解析开始时间（用于性能监控） */
    private long parseStartTime;

    /** 元素计数
     * -- GETTER --
     *  获取已解析元素数量
     */
    @Getter
    private int elementCount = 0;

    /** 字符计数
     * -- GETTER --
     *  获取已解析字符数量
     */
    @Getter
    private int characterCount = 0;

    /** 解析状态
     * -- GETTER --
     *  获取当前解析状态
     */
    @Getter
    private ParseState parseState = ParseState.INITIALIZED;

    /** 当前命名空间上下文 */
    private NamespaceContext currentNamespaceContext;

    // ==================== 内部类 ====================

    /**
     * 解析状态枚举
     */
    public enum ParseState {
        INITIALIZED,   // 已初始化
        STARTING,      // 开始解析
        PARSING,       // 解析中
        COMPLETED,     // 解析完成
        ERROR          // 解析错误
    }

    /**
     * 命名空间上下文
     */
    private record NamespaceContext(String prefix, String uri, int depth) {

        @Override
        public String toString() {
            return String.format("Namespace[prefix=%s, uri=%s, depth=%d]",
                    prefix, uri, depth);
        }
    }

    // ==================== 构造方法 ====================

    /**
     * 使用默认最大深度构造XmlHandler
     *
     * @param userData 用户自定义数据
     * @param xmlParsing XML解析回调接口
     */
    XmlHandler(Object userData, XmlParsing xmlParsing) {
        this(userData, xmlParsing, DEFAULT_MAX_DEEP);
    }

    /**
     * 构造XmlHandler
     *
     * @param userData 用户自定义数据
     * @param xmlParsing XML解析回调接口
     * @param maxDeep 最大XML深度限制（防止栈溢出攻击）
     */
    XmlHandler(Object userData, XmlParsing xmlParsing, int maxDeep) {
        if (maxDeep <= 0) {
            throw new IllegalArgumentException("maxDeep必须大于0");
        }
        if (xmlParsing == null) {
            throw new IllegalArgumentException("xmlParsing不能为null");
        }

        this.maxDeep = maxDeep;
        this.elementStack = new String[DEFAULT_ELEMENT_CAPACITY];
        this.elementIndexes = new int[DEFAULT_ELEMENT_CAPACITY];
        this.userData = userData;
        this.xmlParsing = xmlParsing;

        log.debug("XmlHandler初始化完成: maxDeep={}, userData={}",
                maxDeep, userData != null ? userData.getClass().getSimpleName() : "null");
    }

    // ==================== SAX事件处理方法 ====================

    /**
     * 文档开始解析
     */
    @Override
    public void startDocument() throws SAXException {
        parseStartTime = System.currentTimeMillis();
        parseState = ParseState.STARTING;
        xmlDeep = 0;

        // 重置状态
        Arrays.fill(elementStack, null);
        Arrays.fill(elementIndexes, 0);
        currentValue.setLength(0);
        namespaceStack.clear();
        elementCount = 0;
        characterCount = 0;
        currentNamespaceContext = null;

        log.info("=== XML文档解析开始 ===");
        log.debug("解析器状态重置: 深度={}, 开始时间={}",
                xmlDeep, parseStartTime);

        try {
            xmlParsing.startDocument(userData);
            parseState = ParseState.PARSING;

            log.debug("startDocument回调执行成功");
        } catch (Exception e) {
            parseState = ParseState.ERROR;
            log.error("执行startDocument回调时发生异常", e);
            throw new SAXException("执行startDocument回调失败", e);
        }
    }

    /**
     * 文档解析结束
     */
    @Override
    public void endDocument() throws SAXException {
        parseState = ParseState.COMPLETED;
        long parseEndTime = System.currentTimeMillis();
        long duration = parseEndTime - parseStartTime;

        log.info("=== XML文档解析结束 ===");
        log.info("解析统计: 元素总数={}, 字符总数={}, 耗时={}ms, 平均速度={} 元素/秒",
                elementCount, characterCount, duration,
                duration > 0 ? (elementCount * 1000L / duration) : "N/A");
        log.debug("最终XML深度: {}", xmlDeep);

        try {
            xmlParsing.endDocument(userData);
            log.debug("endDocument回调执行成功");
        } catch (Exception e) {
            parseState = ParseState.ERROR;
            log.error("执行endDocument回调时发生异常", e);
            throw new SAXException("执行endDocument回调失败", e);
        }
    }

    /**
     * 元素开始
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        try {
            // 深度检查
            if (xmlDeep > maxDeep) {
                String errorMsg = String.format("XML深度超出限制: 当前深度=%d, 最大深度=%d",
                        xmlDeep, maxDeep);
                log.error(errorMsg);
                throw new SAXException(errorMsg);
            }

            // 更新状态
            elementStack[xmlDeep] = qName;
            elementIndexes[xmlDeep] += 1;
            elementCount++;
            currentValue.setLength(0);

            // 记录调试信息
            if (log.isDebugEnabled()) {
                String path = getCurrentPath();
                log.debug("开始元素: [深度={}, 名称={}, 路径={}, 属性数={}]",
                        xmlDeep, qName, path, attributes.getLength());

                if (attributes.getLength() > 0) {
                    StringBuilder attrInfo = new StringBuilder("属性: ");
                    for (int i = 0; i < attributes.getLength(); i++) {
                        attrInfo.append(String.format("%s=%s",
                                attributes.getQName(i), attributes.getValue(i)));
                        if (i < attributes.getLength() - 1) {
                            attrInfo.append(", ");
                        }
                    }
                    log.debug(attrInfo.toString());
                }
            }

            // 调用回调
            xmlParsing.startElement(userData, qName, attributes, xmlDeep);

            // 更新深度
            xmlDeep++;

        } catch (SAXException e) {
            parseState = ParseState.ERROR;
            throw e;
        } catch (Exception e) {
            parseState = ParseState.ERROR;
            log.error("处理startElement时发生异常: qName={}, depth={}", qName, xmlDeep, e);
            throw new SAXException("处理XML元素时发生错误", e);
        }
    }

    /**
     * 元素结束
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        try {
            // 深度回退
            xmlDeep--;

            // 验证元素匹配
            if (!qName.equals(elementStack[xmlDeep])) {
                String currentPath = getCurrentPath();
                String expected = elementStack[xmlDeep];
                String errorMsg = String.format(
                        "XML元素不匹配: 期待='%s', 实际='%s', 当前路径='%s'",
                        expected, qName, currentPath);

                log.error(errorMsg);
                log.error("元素栈状态: {}", Arrays.toString(
                        Arrays.copyOf(elementStack, xmlDeep + 2)));

                throw new SAXException(errorMsg);
            }

            // 获取文本内容
            String text = currentValue.toString().trim();
            characterCount += text.length();

            // 记录调试信息
            if (log.isDebugEnabled()) {
                String path = getCurrentPath();
                log.debug("结束元素: [深度={}, 名称={}, 路径={}, 文本长度={}, 内容='{}']",
                        xmlDeep, qName, path, text.length(),
                        text.length() > 100 ? text.substring(0, 100) + "..." : text);
            }

            // 清理栈状态
            elementStack[xmlDeep] = null;
            elementIndexes[xmlDeep] -= 1;

            // 调用回调
            xmlParsing.endElement(userData, qName, text, xmlDeep);

            // 清理命名空间上下文（如果存在）
            cleanupNamespaceContext();

        } catch (SAXException e) {
            parseState = ParseState.ERROR;
            throw e;
        } catch (Exception e) {
            parseState = ParseState.ERROR;
            log.error("处理endElement时发生异常: qName={}, depth={}", qName, xmlDeep, e);
            throw new SAXException("结束XML元素时发生错误", e);
        }
    }

    /**
     * 字符数据处理
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        // 只有处于解析状态时才处理字符
        if (parseState == ParseState.PARSING) {
            currentValue.append(ch, start, length);

            if (log.isTraceEnabled()) {
                String text = new String(ch, start, Math.min(length, 50));
                log.trace("字符数据: [长度={}, 内容='{}']",
                        length, text.length() > 50 ? text + "..." : text);
            }
        }
    }

    /**
     * 命名空间开始映射
     */
    @Override
    public void startPrefixMapping(String prefix, String uri) {
        currentNamespaceContext = new NamespaceContext(
                prefix, uri, xmlDeep + 1
        );
        namespaceStack.push(currentNamespaceContext);

        log.debug("命名空间映射开始: {}", currentNamespaceContext);

        // 调用扩展回调
        if (xmlParsing != null) {
            try {
                xmlParsing.startPrefixMapping(userData, prefix, uri, xmlDeep + 1);
            } catch (Exception e) {
                log.warn("调用startPrefixMapping回调时发生异常", e);
            }
        }
    }

    /**
     * 命名空间结束映射
     */
    @Override
    public void endPrefixMapping(String prefix) {
        if (currentNamespaceContext != null &&
                currentNamespaceContext.prefix.equals(prefix)) {
            namespaceStack.pop();
            currentNamespaceContext = namespaceStack.peek();

            log.debug("命名空间映射结束: prefix={}", prefix);

            // 调用扩展回调
            if (xmlParsing != null) {
                try {
                    xmlParsing.endPrefixMapping(userData, prefix, xmlDeep);
                } catch (Exception e) {
                    log.warn("调用endPrefixMapping回调时发生异常", e);
                }
            }
        }
    }

    /**
     * 可忽略的空白字符
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
        log.trace("可忽略空白: 长度={}", length);
    }

    /**
     * 处理指令
     */
    @Override
    public void processingInstruction(String target, String data) {
        log.debug("处理指令: target={}, data={}", target, data);
    }

    // ==================== 错误处理方法 ====================

    /**
     * 警告信息
     */
    @Override
    public void warning(SAXParseException e) {
        log.warn("XML解析警告: [行={}, 列={}] {}",
                e.getLineNumber(), e.getColumnNumber(), e.getMessage());
    }

    /**
     * 可恢复的错误
     */
    @Override
    public void error(SAXParseException e) {
        log.error("XML解析错误: [行={}, 列={}, 路径={}] {}",
                e.getLineNumber(), e.getColumnNumber(), getCurrentPath(), e.getMessage());
    }

    /**
     * 致命错误
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        parseState = ParseState.ERROR;
        log.error("XML解析致命错误: [行={}, 列={}, 路径={}] {}",
                e.getLineNumber(), e.getColumnNumber(), getCurrentPath(), e.getMessage());
        throw e;
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取当前XML路径（如：/root/books/book）
     */
    private String getCurrentPath() {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i <= xmlDeep && elementStack[i] != null; i++) {
            path.append('/').append(elementStack[i]);
            if (elementIndexes[i] > 1) {
                path.append('[').append(elementIndexes[i]).append(']');
            }
        }
        return path.toString();
    }

    /**
     * 清理命名空间上下文
     */
    private void cleanupNamespaceContext() {
        while (!namespaceStack.isEmpty() &&
                namespaceStack.peek().depth > xmlDeep) {
            NamespaceContext ctx = namespaceStack.pop();
            log.debug("清理命名空间: {}", ctx);
        }
        currentNamespaceContext = namespaceStack.peek();
    }

    /**
     * 获取当前XML深度
     */
    public int getCurrentDepth() {
        return xmlDeep;
    }

    /**
     * 获取解析耗时（毫秒）
     */
    public long getParseDuration() {
        return parseState == ParseState.INITIALIZED ? 0 :
                System.currentTimeMillis() - parseStartTime;
    }
}