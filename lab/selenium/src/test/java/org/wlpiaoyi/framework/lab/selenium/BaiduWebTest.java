package org.wlpiaoyi.framework.lab.selenium;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaiduWebTest {

    private Browser browser;
    private static final String SCREENSHOT_DIR = "target/screenshots/baidu-test";

    @BeforeAll
    void setUp() {
        // 创建截图目录
        createScreenshotDirectory();
    }

    @BeforeEach
    void initBrowser() {
        log.info("初始化浏览器...");

        // 创建浏览器实例（使用默认配置）
        browser = Browser.createDefault()
                .setHeadless(false)  // 设置为true可以在无头模式下运行
                .setStealthMode(true)
                .setLoadImages(true)
                .setDriverPath("D:\\Object\\.Java\\framework\\config/selenium/chromedriver")
                .setTimeoutMs(60000);  // 设置超时时间为60秒

        // 初始化浏览器
        browser.init();
    }

    @AfterEach
    void tearDownBrowser() {
        log.info("关闭浏览器...");
        if (browser != null) {
            browser.quit();
        }
    }

    /**
     * 测试用例1：访问百度首页
     */
    @Test
    @Order(1)
    void testOpenBaiduHomepage() {
        log.info("测试用例1：访问百度首页");

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");

        // 等待页面加载
        browser.waitForElement(By.id("su"));

        // 验证页面标题
        String title = browser.getDriver().getTitle();
        log.info("页面标题: {}", title);

        // 验证搜索框存在
        WebElement searchInput = browser.getDriver().findElement(By.id("chat-textarea"));
        Assertions.assertTrue(searchInput.isDisplayed(), "搜索框应该显示");
        Assertions.assertTrue(searchInput.isEnabled(), "搜索框应该可用");

        // 验证搜索按钮存在
        WebElement searchButton = browser.getDriver().findElement(By.id("chat-submit-button"));
        Assertions.assertTrue(searchButton.isDisplayed(), "搜索按钮应该显示");

        // 截图保存
        takeScreenshot("baidu-homepage.png");

        log.info("测试用例1完成");
    }

    /**
     * 测试用例2：搜索功能测试
     */
    @Test
    @Order(2)
    void testBaiduSearch() {
        log.info("测试用例2：搜索功能测试");

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");

        // 获取搜索框并输入搜索关键词
        WebElement searchInput = browser.waitForElement(By.id("chat-textarea"));
        searchInput.clear();
        searchInput.sendKeys("Selenium自动化测试");

        // 点击搜索按钮
        WebElement searchButton = browser.waitForElementClickable(By.id("chat-submit-button"));
        searchButton.click();

        // 等待搜索结果加载
        browser.waitForElement(By.className("result"));

        // 截图保存
        takeScreenshot("baidu-search-results.png");

        log.info("测试用例2完成");
    }

    /**
     * 测试用例3：Cookie管理测试
     */
    @Test
    @Order(3)
    void testCookieManagement() {
        log.info("测试用例3：Cookie管理测试");

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");

        // 获取当前所有cookies
        Set<Cookie> cookies = browser.getCookies();
        log.info("初始Cookies数量: {}", cookies.size());

        // 添加一个测试cookie
        Cookie testCookie = new Cookie("test_cookie", "test_value", ".baidu.com", "/", null);
        browser.addCookie(testCookie);

        // 验证cookie已添加
        Set<Cookie> updatedCookies = browser.getCookies();
        boolean cookieFound = updatedCookies.stream()
                .anyMatch(c -> "test_cookie".equals(c.getName()));
        Assertions.assertTrue(cookieFound, "测试cookie应该被添加");

        // 删除测试cookie
        browser.deleteCookie("test_cookie");

        // 验证cookie已删除
        Set<Cookie> finalCookies = browser.getCookies();
        boolean cookieDeleted = finalCookies.stream()
                .noneMatch(c -> "test_cookie".equals(c.getName()));
        Assertions.assertTrue(cookieDeleted, "测试cookie应该被删除");

        // 截图保存
        takeScreenshot("baidu-cookie-test.png");

        log.info("测试用例3完成");
    }

    /**
     * 测试用例4：新标签页打开测试
     */
    @Test
    @Order(4)
    void testNewTabNavigation() {
        log.info("测试用例4：新标签页打开测试");

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");

        // 获取当前窗口句柄
        String originalWindow = browser.getCurrentWindowHandle();
        log.info("原始窗口句柄: {}", originalWindow);

        // 在新标签页打开百度知道
        browser.openLinkInNewTab("https://zhidao.baidu.com");

        // 切换到新标签页
        Set<String> windows = browser.getWindowHandles();
        Assertions.assertEquals(2, windows.size(), "应该有两个窗口/标签页");

        // 切换到新窗口
        for (String windowHandle : windows) {
            if (!windowHandle.equals(originalWindow)) {
                browser.switchToWindow(windowHandle);
                break;
            }
        }

        // 验证新标签页的URL
        String newTabUrl = browser.getDriver().getCurrentUrl();
        log.info("新标签页URL: {}", newTabUrl);
        Assertions.assertTrue(newTabUrl.contains("zhidao.baidu.com"), "新标签页应该是百度知道");

        // 验证页面元素
        WebElement zhidaoSearch = browser.waitForElement(By.id("kw"));
        Assertions.assertTrue(zhidaoSearch.isDisplayed(), "百度知道搜索框应该显示");

        // 关闭新标签页，切换回原始窗口
        browser.closeCurrentWindow();
        browser.switchToWindow(originalWindow);

        // 验证回到原始页面
        String originalUrl = browser.getDriver().getCurrentUrl();
        Assertions.assertTrue(originalUrl.contains("www.baidu.com"), "应该回到百度首页");

        // 截图保存（原始窗口）
        takeScreenshot("baidu-original-tab.png");

        log.info("测试用例4完成");
    }

    /**
     * 测试用例5：JavaScript执行测试
     */
    @Test
    @Order(5)
    void testJavaScriptExecution() {
        log.info("测试用例5：JavaScript执行测试");

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");

        // 执行JavaScript获取页面信息
        Object title = browser.executeScript("return document.title");
        Object url = browser.executeScript("return document.URL");
        Object windowSize = browser.executeScript("return {width: window.innerWidth, height: window.innerHeight}");

        log.info("JavaScript获取的标题: {}", title);
        log.info("JavaScript获取的URL: {}", url);
        log.info("JavaScript获取的窗口尺寸: {}", windowSize);

        // 修改搜索框的值
        browser.executeScript("document.getElementById('kw').value = 'JavaScript修改的值'");

        // 验证搜索框的值已修改
        WebElement searchInput = browser.getDriver().findElement(By.id("kw"));
        String inputValue = (String) browser.executeScript("return arguments[0].value", searchInput);
        Assertions.assertEquals("JavaScript修改的值", inputValue, "搜索框的值应该被JavaScript修改");

        // 执行复杂的JavaScript操作：滚动和截图
        browser.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        // 等待滚动完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.warn("等待被中断", e);
        }

        // 截图保存
        takeScreenshot("baidu-javascript-test.png");

        log.info("测试用例5完成");
    }

    /**
     * 测试用例6：无头模式测试
     */
    @Test
    @Order(6)
    void testHeadlessMode() {
        log.info("测试用例6：无头模式测试");

        // 重新创建浏览器实例（无头模式）
        browser.quit();
        browser = Browser.createHeadless()
                .setTimeoutMs(30000)
                .setStealthMode(true);
        browser.init();

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");

        // 验证页面加载成功
        WebElement searchInput = browser.waitForElement(By.id("kw"));
        Assertions.assertTrue(searchInput.isDisplayed(), "搜索框应该显示（无头模式）");

        // 执行搜索
        searchInput.sendKeys("无头浏览器测试");
        browser.getDriver().findElement(By.id("su")).click();

        // 等待搜索结果
        browser.waitForElement(By.className("result"));

        // 截图验证
        byte[] screenshot = browser.takeScreenshot();
        Assertions.assertNotNull(screenshot, "无头模式下应该能截图");
        Assertions.assertTrue(screenshot.length > 0, "截图数据应该非空");

        // 保存截图
        saveScreenshot(screenshot, "baidu-headless-test.png");

        log.info("无头模式测试完成，截图大小: {} bytes", screenshot.length);
    }

    /**
     * 测试用例7：页面导航测试（后退/前进/刷新）
     */
    @Test
    @Order(7)
    void testPageNavigation() {
        log.info("测试用例7：页面导航测试");

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");
        String homepageUrl = browser.getDriver().getCurrentUrl();

        // 搜索内容
        WebElement searchInput = browser.getDriver().findElement(By.id("kw"));
        searchInput.sendKeys("Java编程");
        browser.getDriver().findElement(By.id("su")).click();

        // 等待搜索结果
        browser.waitForElement(By.className("result"));
        String searchResultsUrl = browser.getDriver().getCurrentUrl();
        Assertions.assertNotEquals(homepageUrl, searchResultsUrl, "搜索结果页URL应该不同于首页");

        // 后退到首页
        browser.back();
        String backUrl = browser.getDriver().getCurrentUrl();
        Assertions.assertTrue(backUrl.contains("www.baidu.com"), "后退应该回到百度首页");

        // 等待首页元素
        browser.waitForElement(By.id("kw"));

        // 前进到搜索结果页
        browser.forward();
        String forwardUrl = browser.getDriver().getCurrentUrl();
        Assertions.assertTrue(forwardUrl.contains("wd=Java编程"), "前进应该回到搜索结果页");

        // 刷新页面
        browser.refresh();
        String refreshUrl = browser.getDriver().getCurrentUrl();
        Assertions.assertTrue(refreshUrl.contains("wd=Java编程"), "刷新后应该还在搜索结果页");

        // 截图保存
        takeScreenshot("baidu-navigation-test.png");

        log.info("测试用例7完成");
    }

    /**
     * 测试用例8：反检测功能验证
     */
    @Test
    @Order(8)
    void testAntiDetection() {
        log.info("测试用例8：反检测功能验证");

        // 使用反检测模式的浏览器
        browser.quit();
        browser = Browser.createDefault()
                .setStealthMode(true)
                .setDisableAutomationFlag(true)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                .setTimeoutMs(30000);
        browser.init();

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");

        // 执行JavaScript验证反检测脚本是否生效
        Object webdriverProperty = browser.executeScript("return navigator.webdriver");
        log.info("navigator.webdriver 属性值: {}", webdriverProperty);
        Assertions.assertNull(webdriverProperty, "navigator.webdriver 应该为 undefined（反检测生效）");

        // 检查用户代理
        Object userAgent = browser.executeScript("return navigator.userAgent");
        log.info("用户代理: {}", userAgent);

        // 检查语言设置
        Object languages = browser.executeScript("return navigator.languages");
        log.info("语言设置: {}", languages);

        // 执行搜索操作
        WebElement searchInput = browser.getDriver().findElement(By.id("kw"));
        searchInput.sendKeys("反检测测试");
        browser.getDriver().findElement(By.id("su")).click();

        // 等待搜索结果
        browser.waitForElement(By.className("result"));

        // 截图保存
        takeScreenshot("baidu-anti-detection-test.png");

        log.info("测试用例8完成");
    }

    /**
     * 测试用例9：多窗口切换测试
     */
    @Test
    @Order(9)
    void testMultipleWindows() {
        log.info("测试用例9：多窗口切换测试");

        // 导航到百度首页
        browser.navigateTo("https://www.baidu.com");
        String originalWindow = browser.getCurrentWindowHandle();

        // 打开多个新标签页
        browser.openLinkInNewTab("https://news.baidu.com");
        browser.openLinkInNewTab("https://map.baidu.com");
        browser.openLinkInNewTab("https://tieba.baidu.com");

        // 验证窗口数量
        Set<String> windows = browser.getWindowHandles();
        Assertions.assertEquals(4, windows.size(), "应该有4个窗口/标签页");

        // 切换到每个窗口并验证
        int windowCount = 1;
        for (String windowHandle : windows) {
            browser.switchToWindow(windowHandle);
            String title = browser.getDriver().getTitle();
            String url = browser.getDriver().getCurrentUrl();

            log.info("窗口{} - 标题: {}, URL: {}", windowCount, title, url);

            // 根据URL验证页面类型
            if (url.contains("news.baidu.com")) {
                Assertions.assertTrue(title.contains("百度新闻"), "应该是百度新闻页面");
            } else if (url.contains("map.baidu.com")) {
                Assertions.assertTrue(title.contains("百度地图"), "应该是百度地图页面");
            } else if (url.contains("tieba.baidu.com")) {
                Assertions.assertTrue(title.contains("百度贴吧"), "应该是百度贴吧页面");
            }

            windowCount++;
        }

        // 切换回原始窗口
        browser.switchToWindow(originalWindow);
        String finalUrl = browser.getDriver().getCurrentUrl();
        Assertions.assertTrue(finalUrl.contains("www.baidu.com"), "应该回到百度首页");

        // 关闭所有其他窗口，只保留原始窗口
        for (String windowHandle : windows) {
            if (!windowHandle.equals(originalWindow)) {
                browser.switchToWindow(windowHandle);
                browser.closeCurrentWindow();
            }
        }

        // 切换回原始窗口
        browser.switchToWindow(originalWindow);

        // 截图保存
        takeScreenshot("baidu-multiple-windows-test.png");

        log.info("测试用例9完成");
    }

    /**
     * 测试用例10：综合测试
     */
    @Test
    @Order(10)
    void testComprehensiveScenario() {
        log.info("测试用例10：综合测试");

        // 步骤1：访问百度首页
        browser.navigateTo("https://www.baidu.com");
        Assertions.assertTrue(browser.getDriver().getTitle().contains("百度"), "页面标题应该包含'百度'");

        // 步骤2：检查页面元素
        WebElement searchInput = browser.waitForElement(By.id("kw"));
        WebElement searchButton = browser.waitForElement(By.id("su"));
        Assertions.assertTrue(searchInput.isDisplayed() && searchInput.isEnabled(), "搜索框应该可用");
        Assertions.assertTrue(searchButton.isDisplayed(), "搜索按钮应该显示");

        // 步骤3：执行搜索
        searchInput.sendKeys("综合自动化测试");
        searchButton.click();

        // 步骤4：验证搜索结果
        browser.waitForElement(By.className("result"));
        String searchKeyword = browser.getDriver().findElement(By.id("kw")).getAttribute("value");
        Assertions.assertEquals("综合自动化测试", searchKeyword, "搜索关键词应该正确");

        // 步骤5：点击第一个结果（如果有）
        var results = browser.getDriver().findElements(By.cssSelector("div.result h3 a"));
        if (!results.isEmpty()) {
            String firstResultTitle = results.get(0).getText();
            log.info("第一个搜索结果: {}", firstResultTitle);

            // 保存当前窗口句柄
            String originalWindow = browser.getCurrentWindowHandle();

            // 点击第一个结果（在新标签页打开）
            results.get(0).click();

            // 切换到新标签页
            Set<String> windows = browser.getWindowHandles();
            for (String windowHandle : windows) {
                if (!windowHandle.equals(originalWindow)) {
                    browser.switchToWindow(windowHandle);
                    break;
                }
            }

            // 验证新页面
            String newPageTitle = browser.getDriver().getTitle();
            String newPageUrl = browser.getDriver().getCurrentUrl();
            log.info("新页面标题: {}, URL: {}", newPageTitle, newPageUrl);

            // 关闭新标签页，回到搜索结果页
            browser.closeCurrentWindow();
            browser.switchToWindow(originalWindow);
        }

        // 步骤6：使用高级搜索选项（如果存在）
        try {
            WebElement advancedSearchLink = browser.getDriver().findElement(By.linkText("高级搜索"));
            advancedSearchLink.click();

            // 等待高级搜索页面
            browser.waitForElement(By.name("q1"));

            // 填写高级搜索表单
            browser.getDriver().findElement(By.name("q1")).sendKeys("高级搜索测试");
            browser.getDriver().findElement(By.cssSelector("input[type='submit']")).click();

            // 等待搜索结果
            browser.waitForElement(By.className("result"));
        } catch (Exception e) {
            log.warn("高级搜索功能不可用或已更改: {}", e.getMessage());
        }

        // 步骤7：验证Cookies
        Set<Cookie> cookies = browser.getCookies();
        log.info("当前页面Cookies数量: {}", cookies.size());

        // 步骤8：截图保存
        takeScreenshot("baidu-comprehensive-test.png");

        log.info("测试用例10完成");
    }

    // ============ 辅助方法 ============

    /**
     * 截图并保存到指定目录
     */
    private void takeScreenshot(String filename) {
        try {
            byte[] screenshot = browser.takeScreenshot();
            saveScreenshot(screenshot, filename);
        } catch (Exception e) {
            log.error("截图失败: {}", e.getMessage());
        }
    }

    /**
     * 保存截图到文件
     */
    private void saveScreenshot(byte[] screenshot, String filename) {
        try {
            Path screenshotPath = Paths.get(SCREENSHOT_DIR, filename);
            Files.createDirectories(screenshotPath.getParent());
            Files.write(screenshotPath, screenshot);
            log.info("截图已保存: {}", screenshotPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("保存截图失败: {}", e.getMessage());
        }
    }

    /**
     * 创建截图目录
     */
    private void createScreenshotDirectory() {
        try {
            Path dirPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                log.info("创建截图目录: {}", dirPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建截图目录失败: {}", e.getMessage());
        }
    }
}