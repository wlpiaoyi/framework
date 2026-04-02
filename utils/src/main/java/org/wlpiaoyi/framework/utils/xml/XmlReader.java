package org.wlpiaoyi.framework.utils.xml;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <br/>提供多种XML解析方法，支持文件、字符串、输入流、URL等数据源，
 * <br/>集成安全配置和性能监控，是SAX解析的包装器
 * </p>
 * <p><b>{@code @date:}</b>2026-02-04 10:37:57
 * </p>
 * <p><b>{@code @version:}:</b>2.0</p>
 */
@Slf4j
public class XmlReader {

    // ==================== 配置常量 ====================

    /** 默认最大XML深度 */
    public static final int DEFAULT_MAX_DEEP = 100;

    /** 解析器实例计数器（用于监控） */
    private static final AtomicInteger parserInstanceCounter = new AtomicInteger(0);

    // ==================== 解析方法 ====================

    /**
     * 解析XML字符串
     *
     * @param xml XML字符串内容
     * @param xmlParsing 解析回调接口
     * @throws XmlParseException XML解析异常
     */
    public void read(String xml, XmlParsing xmlParsing) throws XmlParseException {
        read(xml, xmlParsing, null, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析XML字符串
     *
     * @param xml XML字符串内容
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @throws XmlParseException XML解析异常
     */
    public void read(String xml, XmlParsing xmlParsing, Object userData)
            throws XmlParseException {
        read(xml, xmlParsing, userData, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析XML字符串
     *
     * @param xml XML字符串内容
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @param maxDeep 最大XML深度
     * @throws XmlParseException XML解析异常
     */
    public void read(String xml, XmlParsing xmlParsing, Object userData, int maxDeep)
            throws XmlParseException {
        if (xml == null) {
            throw new XmlParseException("XML内容不能为空");
        }
        long startTime = System.currentTimeMillis();
        log.info("开始解析XML字符串，长度={}", xml.length());

        try {
            parse(new InputSource(new StringReader(xml)),
                    xmlParsing, userData, maxDeep, "字符串");
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("XML字符串解析完成，耗时={}ms", duration);
        }
    }

    /**
     * 解析XML文件
     *
     * @param file XML文件
     * @param xmlParsing 解析回调接口
     * @throws XmlParseException XML解析异常
     */
    public void read(File file, XmlParsing xmlParsing) throws XmlParseException {
        read(file, xmlParsing, null, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析XML文件
     *
     * @param file XML文件
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @throws XmlParseException XML解析异常
     */
    public void read(File file, XmlParsing xmlParsing, Object userData)
            throws XmlParseException {
        read(file, xmlParsing, userData, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析XML文件
     *
     * @param file XML文件
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @param maxDeep 最大XML深度
     * @throws XmlParseException XML解析异常
     */
    public void read(File file, XmlParsing xmlParsing, Object userData, int maxDeep)
            throws XmlParseException {
        long startTime = System.currentTimeMillis();
        log.info("开始解析XML文件，路径={}, 大小={}字节",
                file.getAbsolutePath(), file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            parse(new InputSource(fis), xmlParsing, userData, maxDeep, "文件");
        } catch (IOException e) {
            throw new XmlParseException("读取XML文件失败: " + file.getAbsolutePath(), e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("XML文件解析完成，耗时={}ms", duration);
        }
    }

    /**
     * 解析XML输入流
     *
     * @param inputStream XML输入流
     * @param xmlParsing 解析回调接口
     * @throws XmlParseException XML解析异常
     */
    public void read(InputStream inputStream, XmlParsing xmlParsing)
            throws XmlParseException {
        read(inputStream, xmlParsing, null, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析XML输入流
     *
     * @param inputStream XML输入流
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @throws XmlParseException XML解析异常
     */
    public void read(InputStream inputStream, XmlParsing xmlParsing, Object userData)
            throws XmlParseException {
        read(inputStream, xmlParsing, userData, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析XML输入流
     *
     * @param inputStream XML输入流
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @param maxDeep 最大XML深度
     * @throws XmlParseException XML解析异常
     */
    public void read(InputStream inputStream, XmlParsing xmlParsing,
                       Object userData, int maxDeep) throws XmlParseException {
        log.info("开始解析XML输入流");
        parse(new InputSource(inputStream), xmlParsing, userData, maxDeep, "输入流");
    }

    /**
     * 解析URL指向的XML资源
     *
     * @param url XML资源URL
     * @param xmlParsing 解析回调接口
     * @return 处理后的用户数据
     * @throws XmlParseException XML解析异常
     */
    public void read(URL url, XmlParsing xmlParsing) throws XmlParseException {
        read(url, xmlParsing, null, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析URL指向的XML资源
     *
     * @param url XML资源URL
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @throws XmlParseException XML解析异常
     */
    public void read(URL url, XmlParsing xmlParsing, Object userData)
            throws XmlParseException {
        read(url, xmlParsing, userData, DEFAULT_MAX_DEEP);
    }

    /**
     * 解析URL指向的XML资源
     *
     * @param url XML资源URL
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @param maxDeep 最大XML深度
     * @throws XmlParseException XML解析异常
     */
    public void read(URL url, XmlParsing xmlParsing, Object userData, int maxDeep)
            throws XmlParseException {
        long startTime = System.currentTimeMillis();
        log.info("开始解析URL XML资源，URL={}", url);

        try (InputStream is = url.openStream()) {
            parse(new InputSource(is), xmlParsing, userData, maxDeep, "URL");
        } catch (IOException e) {
            throw new XmlParseException("读取URL XML资源失败: " + url, e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("URL XML资源解析完成，耗时={}ms", duration);
        }
    }

    // ==================== 核心解析方法 ====================

    /**
     * 核心解析方法
     *
     * @param inputSource 输入源
     * @param xmlParsing 解析回调接口
     * @param userData 用户自定义数据
     * @param maxDeep 最大XML深度
     * @param sourceType 数据源类型（用于日志）
     * @throws XmlParseException XML解析异常
     */
    private void parse(InputSource inputSource, XmlParsing xmlParsing,
                         Object userData, int maxDeep, String sourceType)
            throws XmlParseException {

        if (xmlParsing == null) {
            throw new XmlParseException("XML解析回调不能为空");
        }

        int parserId = parserInstanceCounter.incrementAndGet();
        log.debug("创建SAX解析器实例，ID={}, 数据源类型={}", parserId, sourceType);

        // 设置字符编码
        if (inputSource.getEncoding() == null) {
            inputSource.setEncoding(StandardCharsets.UTF_8.name());
            log.debug("设置默认编码为UTF-8");
        }

        try {
            // 创建安全配置的SAX解析器工厂
            SAXParserFactory factory = createSecureSAXParserFactory();

            // 创建解析器
            SAXParser parser = factory.newSAXParser();
            log.debug("SAX解析器创建成功，ID={}", parserId);

            // 创建处理器
            XmlHandler handler = new XmlHandler(userData, xmlParsing, maxDeep);

            // 执行解析
            log.info("开始执行SAX解析，解析器ID={}", parserId);
            parser.parse(inputSource, handler);

            // 检查解析状态
            if (handler.getParseState() == XmlHandler.ParseState.ERROR) {
                throw new XmlParseException("XML解析过程中发生错误");
            }

            // 输出解析统计
            log.info("解析统计: 元素={}, 字符={}, 深度={}, 耗时={}ms",
                    handler.getElementCount(), handler.getCharacterCount(),
                    handler.getCurrentDepth(), handler.getParseDuration());

        } catch (ParserConfigurationException e) {
            log.error("解析器配置错误，ID={}", parserId, e);
            throw new XmlParseException("SAX解析器配置失败", e);
        } catch (SAXException e) {
            log.error("SAX解析错误，ID={}", parserId, e);
            throw new XmlParseException("XML解析失败", e);
        } catch (IOException e) {
            log.error("IO错误，ID={}", parserId, e);
            throw new XmlParseException("读取XML数据失败", e);
        } catch (Exception e) {
            log.error("未知解析错误，ID={}", parserId, e);
            throw new XmlParseException("XML解析发生未知错误", e);
        } finally {
            log.debug("SAX解析器实例销毁，ID={}", parserId);
        }
    }

    // ==================== 安全配置 ====================

    /**
     * 创建安全配置的SAX解析器工厂（防止XXE攻击）
     *
     * @return 安全配置的SAXParserFactory
     * @throws ParserConfigurationException 配置异常
     */
    private SAXParserFactory createSecureSAXParserFactory()
            throws ParserConfigurationException {

        SAXParserFactory factory = SAXParserFactory.newInstance();

        // 安全配置：防止XXE攻击
        try {
            // 禁用DTD
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            // 禁用外部实体
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            // 禁用模式验证
            factory.setValidating(false);

            // 启用命名空间感知
            factory.setNamespaceAware(true);

            log.debug("SAX解析器工厂安全配置完成");

        } catch (Exception e) {
            log.warn("设置SAX解析器安全特性失败，将使用默认配置", e);
        }

        return factory;
    }

    // ==================== 工具方法 ====================

    /**
     * 获取已创建的解析器实例数量
     */
    public static int getParserInstanceCount() {
        return parserInstanceCounter.get();
    }

    /**
     * 验证XML格式（快速验证）
     */
    public boolean validate(String xml) {
        if (xml == null || xml.isEmpty()) {
            return false;
        }
        try {
            // 使用简单的解析回调进行验证
            XmlParsing validator = new XmlParsing() {
                @Override
                public void startDocument(Object userData) {}

                @Override
                public void endDocument(Object userData) {}

                @Override
                public void startElement(Object userData, String qName,
                                         org.xml.sax.Attributes attributes, int xmlDeep) {}

                @Override
                public void endElement(Object userData, String qName,
                                       String text, int xmlDeep) {}
            };

            read(xml, validator, null, 1);
            return true;

        } catch (XmlParseException e) {
            log.debug("XML验证失败: {}", e.getMessage());
            return false;
        }
    }
}

