package org.wlpiaoyi.framework.utils.xml;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>XmlReader 测试类</p>
 * <p><b>{@code @date:}</b>2026-02-04 11:32:23</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class XmlReaderTest {

    private XmlReader xmlReader;
    private File tempXmlFile;

    // 用于存储解析结果的测试数据结构
    private static class TestParsingResult {
        final List<String> elementPath = new ArrayList<>();
        final Map<String, String> elementTexts = new HashMap<>();
        final Map<String, Map<String, String>> elementAttributes = new HashMap<>();
        boolean documentStarted = false;
        boolean documentEnded = false;
        int elementCount = 0;
    }

    @Before
    public void setUp() throws Exception {
        log.info("开始测试...");
        xmlReader = new XmlReader();

        // 创建临时XML文件用于测试
        tempXmlFile = File.createTempFile("test", ".xml");
        try (FileWriter writer = new FileWriter(tempXmlFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<library>\n");
            writer.write("  <book id=\"1\" category=\"fiction\">\n");
            writer.write("    <title>The Great Gatsby</title>\n");
            writer.write("    <author>F. Scott Fitzgerald</author>\n");
            writer.write("    <year>1925</year>\n");
            writer.write("    <price currency=\"USD\">10.99</price>\n");
            writer.write("  </book>\n");
            writer.write("  <book id=\"2\" category=\"non-fiction\">\n");
            writer.write("    <title>A Brief History of Time</title>\n");
            writer.write("    <author>Stephen Hawking</author>\n");
            writer.write("    <year>1988</year>\n");
            writer.write("    <price currency=\"USD\">12.50</price>\n");
            writer.write("  </book>\n");
            writer.write("</library>");
        }
        log.info("创建临时XML文件: {}", tempXmlFile.getAbsolutePath());
    }

    @Test
    public void testReadString() {
        log.info("开始测试XmlReader字符串解析...");

        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <catalog>
                <product id="P001" type="electronics">
                    <name>Laptop</name>
                    <price>999.99</price>
                    <stock>50</stock>
                </product>
                <product id="P002" type="books">
                    <name>Java Programming</name>
                    <price>49.99</price>
                    <stock>100</stock>
                </product>
            </catalog>
            """;

        TestParsingResult result = new TestParsingResult();

        XmlParsing parsing = new XmlParsing() {
            private StringBuilder currentPath = new StringBuilder();

            @Override
            public void startDocument(Object userData) {
                log.info("文档开始解析");
                result.documentStarted = true;
            }

            @Override
            public void endDocument(Object userData) {
                log.info("文档解析结束");
                result.documentEnded = true;
            }

            @Override
            public void startElement(Object userData, String qName, Attributes attributes, int xmlDeep) {
                currentPath.append("/").append(qName);
                String path = currentPath.toString();
                result.elementPath.add(path);
                result.elementCount++;

                // 保存属性
                Map<String, String> attrs = new HashMap<>();
                for (int i = 0; i < attributes.getLength(); i++) {
                    attrs.put(attributes.getQName(i), attributes.getValue(i));
                }
                result.elementAttributes.put(path, attrs);

                log.info("开始元素: {}, 深度: {}, 属性: {}", qName, xmlDeep, attrs);
            }

            @Override
            public void endElement(Object userData, String qName, String text, int xmlDeep) {
                String path = currentPath.toString();
                if (!text.isEmpty()) {
                    result.elementTexts.put(path, text);
                }
                log.info("结束元素: {}, 文本: {}, 深度: {}", qName, text, xmlDeep);

                // 移除路径的最后一部分
                int lastSlash = currentPath.lastIndexOf("/");
                if (lastSlash >= 0) {
                    currentPath.delete(lastSlash, currentPath.length());
                }
            }
        };

        try {
            xmlReader.read(xml, parsing, result);

            // 验证解析结果
            assertTrue("文档开始事件应该被调用", result.documentStarted);
            assertTrue("文档结束事件应该被调用", result.documentEnded);
            assertEquals("应该解析9个元素", 9, result.elementCount);
            assertEquals("catalog/product[1]/name 文本应该是 'Laptop'",
                    "/catalog/product/name", result.elementPath.get(2));
            assertEquals("第二个产品的ID应该是 'P002'",
                    "P002", result.elementAttributes.get("/catalog/product").get("id"));

            log.info("字符串解析测试通过");

        } catch (XmlParseException e) {
            fail("XML解析失败: " + e.getMessage());
        }
    }

    @Test
    public void testReadFile() {
        log.info("开始测试XmlReader文件解析...");

        TestParsingResult result = new TestParsingResult();

        XmlParsing parsing = new XmlParsing() {
            private StringBuilder currentPath = new StringBuilder();

            @Override
            public void startDocument(Object userData) {
                result.documentStarted = true;
            }

            @Override
            public void endDocument(Object userData) {
                result.documentEnded = true;
            }

            @Override
            public void startElement(Object userData, String qName, Attributes attributes, int xmlDeep) {
                currentPath.append("/").append(qName);
                result.elementPath.add(currentPath.toString());
                result.elementCount++;
            }

            @Override
            public void endElement(Object userData, String qName, String text, int xmlDeep) {
                String path = currentPath.toString();
                if (!text.isEmpty()) {
                    result.elementTexts.put(path, text);
                }

                int lastSlash = currentPath.lastIndexOf("/");
                if (lastSlash >= 0) {
                    currentPath.delete(lastSlash, currentPath.length());
                }
            }
        };

        try {
            xmlReader.read(tempXmlFile, parsing, result);

            // 验证解析结果
            assertTrue(result.documentStarted);
            assertTrue(result.documentEnded);
            assertTrue("应该解析多个元素", result.elementCount > 0);
            assertEquals("第一个书名应该是 'The Great Gatsby'",
                    "The Great Gatsby", result.elementTexts.get("/library/book/title"));
            assertEquals("第二个书作者应该是 'Stephen Hawking'",
                    "Stephen Hawking", result.elementTexts.get("/library/book/author"));

            log.info("文件解析测试通过，共解析 {} 个元素", result.elementCount);

        } catch (XmlParseException e) {
            fail("XML文件解析失败: " + e.getMessage());
        }
    }

    @Test
    public void testReadInputStream() {
        log.info("开始测试XmlReader输入流解析...");

        String xml = """
            <?xml version="1.0"?>
            <config>
                <server>
                    <host>localhost</host>
                    <port>8080</port>
                    <enabled>true</enabled>
                </server>
                <database>
                    <url>jdbc:mysql://localhost:3306/test</url>
                    <username>root</username>
                    <password>secret</password>
                </database>
            </config>
            """;

        TestParsingResult result = new TestParsingResult();

        XmlParsing parsing = new XmlParsing() {
            @Override
            public void startDocument(Object userData) {
                result.documentStarted = true;
            }

            @Override
            public void endDocument(Object userData) {
                result.documentEnded = true;
            }

            @Override
            public void startElement(Object userData, String qName, Attributes attributes, int xmlDeep) {
                result.elementCount++;
            }

            @Override
            public void endElement(Object userData, String qName, String text, int xmlDeep) {
                // 只记录特定元素的文本
                if ("port".equals(qName)) {
                    result.elementTexts.put("port", text);
                } else if ("enabled".equals(qName)) {
                    result.elementTexts.put("enabled", text);
                }
            }
        };

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes())) {
            xmlReader.read(inputStream, parsing, result);

            // 验证解析结果
            assertTrue(result.documentStarted);
            assertTrue(result.documentEnded);
            assertEquals("端口应该是 8080", "8080", result.elementTexts.get("port"));
            assertEquals("启用状态应该是 true", "true", result.elementTexts.get("enabled"));

            log.info("输入流解析测试通过");

        } catch (Exception e) {
            fail("输入流解析失败: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidXml() {
        log.info("开始测试无效XML解析...");

        // 无效的XML（缺少结束标签）
        String invalidXml = """
            <?xml version="1.0"?>
            <root>
                <element>Some text
            </root>
            """;

        XmlParsing parsing = new XmlParsing() {
            @Override
            public void startDocument(Object userData) {}
            @Override
            public void endDocument(Object userData) {}
            @Override
            public void startElement(Object userData, String qName, Attributes attributes, int xmlDeep) {}
            @Override
            public void endElement(Object userData, String qName, String text, int xmlDeep) {}
        };

        try {
            xmlReader.read(invalidXml, parsing, null);
            fail("应该抛出XmlParseException异常");
        } catch (XmlParseException e) {
            log.info("无效XML解析测试通过，预期异常: {}", e.getMessage());
            assertTrue("异常消息应该包含错误信息", e.getMessage().contains("XML解析失败"));
        }
    }

    @Test
    public void testMaxDepthLimit() {
        log.info("开始测试XML深度限制...");

        // 创建一个深度很大的XML
        StringBuilder deepXml = new StringBuilder("<?xml version=\"1.0\"?><root>");
        for (int i = 0; i < 120; i++) {
            deepXml.append("<level").append(i).append(">");
        }
        deepXml.append("Deep content");
        for (int i = 0; i < 120; i++) {
            deepXml.append("</level").append(i).append(">");
        }
        deepXml.append("</root>");

        XmlParsing parsing = new XmlParsing() {
            @Override
            public void startDocument(Object userData) {}
            @Override
            public void endDocument(Object userData) {}
            @Override
            public void startElement(Object userData, String qName, Attributes attributes, int xmlDeep) {}
            @Override
            public void endElement(Object userData, String qName, String text, int xmlDeep) {}
        };

        try {
            // 使用默认深度限制（100）
            xmlReader.read(deepXml.toString(), parsing, null);
            fail("超过深度限制应该抛出异常");
        } catch (XmlParseException e) {
            log.info("深度限制测试通过，捕获预期异常: {}", e.getMessage());
            assertTrue("异常消息应该包含深度限制信息",
                    e.getMessage().contains("XML解析失败") || e.getMessage().contains("深度超出限制"));
        }
    }

    @Test
    public void testXmlValidation() {
        log.info("开始测试XML验证功能...");

        // 有效的XML
        String validXml = "<?xml version=\"1.0\"?><valid><element>test</element></valid>";
        boolean isValid = xmlReader.validate(validXml);
        assertTrue("有效的XML应该通过验证", isValid);
        log.info("有效XML验证通过");

        // 无效的XML
        String invalidXml = "<?xml version=\"1.0\"?><invalid><element>test";
        boolean isInvalid = xmlReader.validate(invalidXml);
        assertFalse("无效的XML应该验证失败", isInvalid);
        log.info("无效XML验证测试通过");
    }

    @Test
    public void testXmlWithNamespaces() {
        log.info("开始测试带命名空间的XML解析...");

        String xmlWithNs = """
            <?xml version="1.0" encoding="UTF-8"?>
            <root xmlns="http://example.com/default"
                  xmlns:xs="http://www.w3.org/2001/XMLSchema"
                  xmlns:app="http://example.com/app">
                <app:employee id="101">
                    <app:name>John Doe</app:name>
                    <app:department>Engineering</app:department>
                    <xs:type>full-time</xs:type>
                </app:employee>
                <app:employee id="102">
                    <app:name>Jane Smith</app:name>
                    <app:department>Marketing</app:department>
                    <xs:type>part-time</xs:type>
                </app:employee>
            </root>
            """;

        final List<String> elements = new ArrayList<>();

        XmlParsing parsing = new XmlParsing() {
            @Override
            public void startDocument(Object userData) {}

            @Override
            public void endDocument(Object userData) {}

            @Override
            public void startElement(Object userData, String qName, Attributes attributes, int xmlDeep) {
                elements.add(qName);
                log.info("带命名空间的元素: {}, 深度: {}", qName, xmlDeep);
            }

            @Override
            public void endElement(Object userData, String qName, String text, int xmlDeep) {
                if (!text.trim().isEmpty()) {
                    log.info("元素 {} 的文本: {}", qName, text);
                }
            }

            @Override
            public void startPrefixMapping(Object userData, String prefix, String uri, int xmlDeep) {
                log.info("命名空间映射: 前缀={}, URI={}, 深度={}", prefix, uri, xmlDeep);
            }
        };

        try {
            xmlReader.read(xmlWithNs, parsing, null);
            assertTrue("应该包含带命名空间的元素", elements.contains("app:employee"));
            assertTrue("应该包含xs:type元素", elements.contains("xs:type"));
            log.info("命名空间XML解析测试通过");
        } catch (XmlParseException e) {
            fail("带命名空间的XML解析失败: " + e.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
        log.info("清理测试资源...");

        // 删除临时文件
        if (tempXmlFile != null && tempXmlFile.exists()) {
            if (tempXmlFile.delete()) {
                log.info("已删除临时文件: {}", tempXmlFile.getAbsolutePath());
            } else {
                log.warn("无法删除临时文件: {}", tempXmlFile.getAbsolutePath());
            }
        }

        // 打印解析器使用统计
        log.info("本次测试共创建了 {} 个解析器实例", XmlReader.getParserInstanceCount());
        log.info("测试结束");
    }
}