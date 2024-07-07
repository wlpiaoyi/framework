package org.wlpiaoyi.framework.lab.selenium.test;

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

public class BrowserTesGabgov {

    private Browser browser;


    @Before
    public void setUp() throws Exception {
        browser = new Browser().setOptionHeadless(false).setUrl("https://gab.122.gov.cn/m/login");
//        this.browser.setOptionHeadless(true);
        this.browser.setOptionLoadimg(true);
        this.browser.setDriverPath(System.getProperty("user.dir") +"/chromedriver.126.0.v");
    }

    public void openAndLogin(){

        try{
            browser.openChromeDriver();
        }catch (Exception e) {e.printStackTrace();}
        String errorMsg = null;
        int i = 300;
        while (i-- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
                try{
                    webElements = browser.getDriver().findElements(By.xpath("/html/body/div"));
                }catch (Exception e){}
                if(ValueUtils.isBlank(webElements)){
                    continue;
                }
                if(ValueUtils.isBlank(webElements) || webElements.size() < 2){
                    continue;
                }
                WebElement webElement = null;
                try{
                    webElement = webElements.get(1).findElement(By.className("pull-right"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                try{
                    webElement = webElement.findElement(By.xpath("select"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                try{
                    webElements = webElement.findElements(By.xpath("option"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                if(ValueUtils.isBlank(webElements) || webElements.size() < 1){
                    continue;
                }

                int ti = 300;
                while (ti -- > 0){

                    try{
                        Thread.sleep(1000);
                        webElement = browser.getDriver().findElement(By.className("aui_state_highlight"));
                    }catch (Exception e){}
                    if(webElement == null){
                        break;
                    }
                }

                webElements.get(0).click();

                browser.getDriver().findElement(By.id("sidebar_menu_5")).click();
                Thread.sleep(1000);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务左边目录");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
    }

    public void search(String text){

        String errorMsg = null;
        int i = 30;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.id("mem-content")).findElement(By.id("vehSearchForm"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                try{
                    webElements = webElement.findElements(By.xpath("div"));
                }catch (Exception e){}
                if(ValueUtils.isBlank(webElements) || webElements.size() < 4){
                    continue;
                }
                Thread.sleep(1000);

                try{
                    webElements.get(0).findElements(By.className("add-on")).get(0).click();
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/thead/tr/th")).get(1).click();
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    browser.getDriver().findElements(By.className("datetimepicker-years")).get(0).findElements(By.xpath("table/tbody/tr/td/span")).get(1).click();
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/tbody/tr/td/span")).get(0).click();
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    browser.getDriver().findElements(By.className("datetimepicker-days")).get(0).findElements(By.xpath("table/tbody/tr/td")).get(6).click();
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    webElements.get(1).findElement(By.id("hpzl")).findElements(By.xpath("option")).get(3).click();
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.setValue(webElements.get(2).findElement(By.id("hphm")), text);
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    webElements.get(3).findElement(By.xpath("button")).click();
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                errorMsg = null;
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务右上Tab");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
    }

    @Test
    public void test() {
        this.openAndLogin();
        this.search("ADC6266");
        String errorMsg = null;
        int i = 30;
        i = 30;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.id("violationveh"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                if(webElement == null){ continue; }


                try{
                    webElements = webElement.findElements(By.xpath("tabel/tbody/tr"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                if(ValueUtils.isBlank(webElements)){
                    continue;
                }
                for(WebElement trEle : webElements){
                    List<WebElement> datas = trEle.findElements(By.xpath("td"));
                    if(!"未处理".equals(datas.get(4).getText())){
                        continue;
                    }
                    if(!"未处理".equals(datas.get(6).getText())){
                        continue;
                    }
                    datas.get(7).findElement(By.xpath("a")).click();
                    Thread.sleep(1000);
                    this.view();
                    System.out.println("=========================================>");
                    break;
                }
                try{
                    webElement = browser.getDriver().findElement(By.id("mypagination1"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                if(webElement == null){ continue; }

                try{
                    webElements = webElement.findElements(By.xpath("ul/ls"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                if(ValueUtils.isBlank(webElements) || webElements.size() <= 5){
                    break;
                }
                webElements.remove(0);
                webElements.remove(0);
                webElements.remove(0);
                webElements.remove(webElements.size() - 1);
                webElements.remove(webElements.size() - 1);
                if(webElements.size() > 0){
                    webElements.get(0).findElement(By.xpath("a")).click();
                    Thread.sleep(2000);
                    continue;
                }else{
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务右上Tab");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }

        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void view(){
        String errorMsg = null;
        int i = 30;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.id("view"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }

                try{
                    webElement = webElement.findElement(By.className("modal-body"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }

                try{
                    webElement = webElement.findElement(By.className("xqInfo"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }

                try{
                    webElements = webElement.findElements(By.xpath("form/div"));
                }catch (Exception e){}
                if(ValueUtils.isBlank(webElements) || webElements.size() < 1){
                    continue;
                }

                for (WebElement ele : webElements){
                    String name = ele.findElements(By.xpath("span")).get(0).getText();
                    String value = ele.findElements(By.xpath("span")).get(1).getText();
                    System.out.printf("name:" + name + ", value:" + value);
                }
                System.out.printf("\n");

                errorMsg = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务右上Tab");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
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

    }
}
