package org.wlpiaoyi.framework.lab.selenium.test;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.lab.selenium.excel.ExcelWriter;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class BrowserCabgov {

    private Browser browser;

    public BrowserCabgov(){
        browser = new Browser().setOptionHeadless(false).setUrl("https://gab.122.gov.cn/m/login");
//        this.browser.setOptionHeadless(true);
        this.browser.setOptionLoadimg(true);
        this.browser.setDriverPath(System.getProperty("user.dir") +"/chromedriver.126.0.v");
    }

    @SneakyThrows
    public void start(){
        try{
            browser.openChromeDriver();
        }catch (Exception e) {e.printStackTrace();}
        String[] args = ReaderUtils.loadString(System.getProperty("user.dir") + "/car_no.txt", null).split("\n");
        List<Map<String, String>> itemsList = new ArrayList<>();
        try{
            for(String arg : args){
                try{
                    this.exce(arg, itemsList);
                    System.out.println("success car_no:" + arg);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("failed car_no:" + arg);
                }
                writeExcel(itemsList);
            }
        }finally {
            writeExcel(itemsList);
        }

    }

    @SneakyThrows
    public void writeExcel(List<Map<String, String>> itemsList){
        Gson gson = GsonBuilder.gsonDefault();
        String fileName = DateUtils.formatDate(new Date(), "YYMMDD HH:mm:ss");
        WriterUtils.overwrite(new File(System.getProperty("user.dir") + "/" + fileName  + ".txt"), gson.toJson(itemsList).getBytes());
        OutputStream os = new FileOutputStream(System.getProperty("user.dir") + "/" + fileName + ".xlsx");
        ExcelWriter.exportData(itemsList).write(os);
        os.flush();
        os.close();
    }

    void exce(String value, List<Map<String, String>> itemsList){
        this.openAndLogin();
        this.search(value);
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
                    webElements = webElement.findElements(By.xpath("table/tbody/tr"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                if(ValueUtils.isBlank(webElements)){
                    continue;
                }
                for(WebElement trEle : webElements){
                    List<WebElement> datas = trEle.findElements(By.xpath("td"));
                    if(!"未交款".equals(datas.get(6).getText())){
                        continue;
                    }
                    try{
                        Thread.sleep(500);
                        WebElementUtils.click(browser, datas.get(7).findElement(By.xpath("a")));
                    }catch (Exception e){
                        throw e;
                    }
                    Thread.sleep(2000);
                    i = 30;
                    Map<String, String> item = this.view();
                    item.put("状态", datas.get(4).getText() + "|" +datas.get(6).getText());
                    itemsList.add(item);
                    System.out.println("=========================================>");
                }
                try{
                    webElement = browser.getDriver().findElement(By.id("mypagination1"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                if(webElement == null){ break; }

                try{
                    webElements = webElement.findElements(By.xpath("ul/li"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                if(ValueUtils.isBlank(webElements) || webElements.size() <= 5){
                    break;
                }
                webElements.remove(0);
                webElements.remove(0);
                webElements.remove(webElements.size() - 1);
                webElements.remove(webElements.size() - 1);

                if(webElements.size() <= 1){
                    break;
                }
                List<WebElement> removes = new ArrayList<>();
                for(WebElement ele : webElements){
                    removes.add(ele);
                    if("active".equals(ele.getAttribute("class"))){
                        break;
                    }
                }
                webElements.removeAll(removes);
                if(webElements.size() == 0){
                    break;
                }
                WebElementUtils.click(browser, webElements.get(0).findElement(By.xpath("a")));
                Thread.sleep(2000);
                i = 30;
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

    boolean clickFeed(){
        try{
            WebElement webElement = browser.getDriver().findElement(By.className("aui_state_highlight"));
            if(webElement != null){
                WebElementUtils.click(browser, webElement);
                return true;
            }
        }catch (Exception e){
//            e.printStackTrace();
        }
        return false;

    }

    void openAndLogin(){

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

                int ti = 10;
                while (ti -- > 0){
                    try{
                        Thread.sleep(1000);
                        if(clickFeed()){
                            break;
                        }else if(ti > 5){
                            break;
                        }
                    }catch (Exception e){}
                }

                WebElementUtils.click(browser, webElements.get(0));

                WebElementUtils.click(browser, browser.getDriver().findElement(By.id("sidebar_menu_5")));
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

    void search(String text){

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
                    WebElementUtils.click(browser, webElements.get(0).findElements(By.className("add-on")).get(0));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/thead/tr/th")).get(1));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-years")).get(0).findElements(By.xpath("table/tbody/tr/td/span")).get(1));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser,browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/tbody/tr/td/span")).get(0));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-days")).get(0).findElements(By.xpath("table/tbody/tr/td")).get(6));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, webElements.get(1).findElement(By.id("hpzl")).findElements(By.xpath("option")).get(3));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.setValue(webElements.get(2).findElement(By.id("hphm")), text);
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, webElements.get(3).findElement(By.xpath("button")));
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

    @SneakyThrows
    Map<String, String> view(){
        try{

            Map<String, String> itemMap = new HashMap<>();
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
                    Thread.sleep(1000);
                    int itm = 10;
                    while (--itm > 0){
                        Map<String, String> data = new HashMap<>();
                        boolean isGoon = false;
                        for (WebElement ele : webElements){
                            if(ele.findElements(By.xpath("span")).size() < 2){
                                continue;
                            }
                            List<WebElement> spans = ele.findElements(By.xpath("span"));
                            String name = spans.get(0).getText();
                            String value = spans.get(1).getText();
                            if(ValueUtils.isBlank(name) || ValueUtils.isBlank(value)){
                                System.out.println("waiting");
                                isGoon = true;
                                break;
                            }
                            data.put(name, value);
                        }
                        if(isGoon){
                            Thread.sleep(1000);
                            webElements = webElement.findElements(By.xpath("form/div"));
                            continue;
                        }
                        itemMap.putAll(data);
                    }
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
            return itemMap;
        }finally {
            WebElement webElement = browser.getDriver().findElement(By.id("view"));
            WebElementUtils.click(browser, webElement.findElement(By.id("bind_close")));
            Thread.sleep(1000);
        }
    }
}
