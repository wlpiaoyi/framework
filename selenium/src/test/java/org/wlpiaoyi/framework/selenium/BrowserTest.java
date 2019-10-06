package org.wlpiaoyi.framework.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

public class BrowserTest {

    private Browser browser;


    @Before
    public void setUp() throws Exception {
        browser = new Browser().setOptionHeadless(false).setUrl("https://www.baidu.com");
    }
    @Test
    public void test() {
        browser.openDriver();
    }

    @After
    public void tearDown() throws Exception {
        if(!Browser.setElementValue(browser.getDriver().findElement(By.id("index-kw")), "test01"))
            throw new BusinessException("set input value exception!!");
        Thread.sleep(500);
        if(!Browser.setElementValue(browser.getDriver().findElement(By.id("index-kw")), "test02"))
            throw new BusinessException("set input value exception!!");
        else{
            Thread.sleep(100);
            browser.quit();
            Thread.sleep(500);
        }
    }
}
