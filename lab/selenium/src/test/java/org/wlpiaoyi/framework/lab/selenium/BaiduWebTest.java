package org.wlpiaoyi.framework.lab.selenium;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 百度网页自动化测试用例
 * 测试目标：验证Browser类的基本功能
 */
@Slf4j
public class BaiduWebTest {

    private Browser browser;
    private static final String SCREENSHOT_DIR = "target/screenshots/baidu-test";

    @Before
    public void initBrowser() {
        log.info("初始化浏览器...");

        // 创建浏览器实例（使用默认配置）
        browser = Browser.createDefault()
                .setHeadless(false)  // 设置为true可以在无头模式下运行
                .setStealthMode(true)
                .setDisableAutomationFlag(true)
//                .setUserDataPath("C:\\Users\\admin\\AppData\\Local\\Google\\Chrome\\User Data")
                .setDriverPath("D:\\Object\\.Java\\framework\\fw_config/selenium/chromedriver")
//                .setBinaryPath("C:\\Users\\admin\\Downloads\\chrome-win64\\chrome-win64\\chrome.exe")
                .setTimeoutMs(60000);  // 设置超时时间为60秒

        // 初始化浏览器
        browser.init();
    }

    @After
    public void tearDownBrowser() {
        log.info("关闭浏览器...");
        if (browser != null) {
            browser.quit();
        }
    }

    /**
     * 测试用例1：访问百度首页
     */
    @Test
    public void testOpenBaiduHomepage() {
        log.info("测试用例1：访问百度首页");

        // 导航到百度首页
        browser.navigateTo("file:///D:/Object/.Java/framework/lab/selenium/src/main/resources/%E7%88%AC%E8%99%AB%E6%A3%80%E6%B5%8B.html");


        // 验证页面标题
        String title = browser.getDriver().getTitle();
        log.info("页面标题: {}", title);

        // 验证搜索框存在
        WebElement searchInput = browser.getDriver().findElement(By.id("chat-textarea"));
//        Assertions.assertTrue(searchInput.isDisplayed(), "搜索框应该显示");
//        Assertions.assertTrue(searchInput.isEnabled(), "搜索框应该可用");

        // 验证搜索按钮存在
        WebElement searchButton = browser.getDriver().findElement(By.id("chat-submit-button"));
//        Assertions.assertTrue(searchButton.isDisplayed(), "搜索按钮应该显示");

        log.info("测试用例1完成");
    }
}