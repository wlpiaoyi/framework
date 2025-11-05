package org.wlpiaoyi.framework.lab.selenium.for12123.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.List;

public class BrowserTestBaidu {

    private Browser browser;


    @Before
    public void setUp() throws Exception {
        browser = new Browser().setOptionHeadless(false).setUrl("https://www.baidu.com");
//        this.browser.setOptionHeadless(true);
        this.browser.setOptionLoadimg(true);
        this.browser.setDriverPath(System.getProperty("user.dir") +"/chromedriver.126.0.v");
//        this.browser.setProxyServer("127.0.0.1:8010");
    }
    @Test
    public void test() {
        try{
            browser.openChromeDriver();
        }catch (Exception e) {e.printStackTrace();}
    }

    public WebElement getNearBy(WebElement ce, Integer[] ui){
        int maxV = Integer.MAX_VALUE;
        List<WebElement> elements = ((RemoteWebElement) ce).findElementsByXPath("./*");
        if(elements == null){
            return null;
        }
        WebElement element = null;
        for (WebElement e : elements){
            WebElement targetE = getNearBy(e, ui);
            if(targetE == null){
                targetE = e;
            }
            int iuu = Math.abs(targetE.getRect().getX() - ui[0]) + Math.abs(targetE.getRect().getY() - ui[1]);
            if(iuu >= maxV){
                continue;
            }
            element = targetE;
            maxV = iuu;
        }
        return element;
    }

    @After
    public void tearDown() throws Exception {
        if(!WebElementUtils.setValue(browser.getDriver().findElement(By.id("kw")), "test01"))
            throw new BusinessException("set input value exception!!");
        JavascriptExecutor executor = (JavascriptExecutor) browser.getDriver();
        String js = "window.hovered_element = null\n" +
                "var mx = 'x=-1,y=-1';\n" +
                "function track_mouse(event){\n" +
                "    var x = event.clientX, y = event.clientY\n" +
                "    mx = x + \",\"+y\n" +
                "    window.hovered_element = mx;\n" +
                "}\n" +
                "window.onmousemove = track_mouse;";
        executor.executeScript(js);
        Thread.sleep(1000);
        Object o = executor.executeScript("return window.hovered_element;");
        Integer[] is = ValueUtils.toIntegerArray(o.toString());
        List<WebElement> elements = browser.getDriver().findElements(By.xpath("/html/body"));
        int iu = Integer.MAX_VALUE;
        WebElement element = getNearBy(elements.get(0), is);
        executor.executeScript("document.getElementById(\"" + ((RemoteWebElement) element).getId() +"\").style=\"background-color:#F00\"");
        browser.getDriver().findElement(By.id("su")).click();
        Thread.sleep(1000);
        if(!WebElementUtils.setValue(browser.getDriver().findElement(By.id("kw")), "test02"))
            throw new BusinessException("set input value exception!!");
        Thread.sleep(1000);
        browser.getDriver().findElement(By.id("su")).click();
        Thread.sleep(1000);
        browser.quit();
        Thread.sleep(500);

    }
}
