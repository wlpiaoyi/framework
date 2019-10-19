package org.wlpiaoyi.framework.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.wlpiaoyi.framework.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.OSUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

public class BrowserTest {

    private Browser browser;


    @Before
    public void setUp() throws Exception {
        browser = new Browser().setOptionHeadless(false).setUrl("https://www.baidu.com");
        this.browser.setOptionHeadless(true);
        this.browser.setOptionLoadimg(false);
        this.browser.setDriverPath(System.getProperty("user.dir") +"/chromedriver");
//        this.browser.setProxyServer("127.0.0.1:9000");
    }
    @Test
    public void test() {
        try{
            browser.openChromeDriver();
        }catch (Exception e) {e.printStackTrace();}
    }

    @After
    public void tearDown() throws Exception {
        if(!WebElementUtils.setValue(browser.getDriver().findElement(By.id("index-kw")), "test01"))
            throw new BusinessException("set input value exception!!");
        Thread.sleep(500);
        if(!WebElementUtils.setValue(browser.getDriver().findElement(By.id("index-kw")), "test02"))
            throw new BusinessException("set input value exception!!");
        else{
            Thread.sleep(100);
            browser.quit();
            Thread.sleep(500);
        }
    }
}
