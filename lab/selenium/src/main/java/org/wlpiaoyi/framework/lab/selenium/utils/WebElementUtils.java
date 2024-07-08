package org.wlpiaoyi.framework.lab.selenium.utils;

import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.utils.ValueUtils;

public class WebElementUtils {

    public static void click(Browser browser, WebElement ele){
        try{
            ele.click();
        }catch (Exception e){
            ((JavascriptExecutor)browser.getDriver()).executeScript("arguments[0].click();", ele);
        }
    }
    @SneakyThrows
    public static String getValue(WebElement element, int reGetNum){
        if(reGetNum <= 0){
            return getValue(element);
        }
        String value = null;
        while (-- reGetNum > 0){
            value = getValue(element);
            if(ValueUtils.isNotBlank(value)){
                break;
            }
            Thread.sleep(1000);
        }
        return value;
    }

    public static String getValue(WebElement element){
        String value = element.getText();
        if(value == null){
            value = element.getAttribute("value");
        }
        return value;
    }

    /**
     * 给元素设置value
     * @param element
     * @param value
     * @return
     */
    public static boolean setValue(WebElement element, String value) {

        int length = 0;
        if(element.getAttribute("value") != null){
            length = element.getAttribute("value").length();
        }

        if(length > 0){
            element.clear();
            for (int i = 0; i < 20 ; i++) {
                try { Thread.sleep(100);} catch (InterruptedException e) {}
                if(element.getAttribute("value") == null || element.getAttribute("value").length() == 0)
                    break;
            }

            if(element.getAttribute("value") != null){
                length = element.getAttribute("value").length();
            }

            for (int i = length; i > 0  ; i--) {
                element.sendKeys(Keys.BACK_SPACE);
                try { Thread.sleep(50);} catch (InterruptedException e) {}
            }
            length = element.getAttribute("value").length();
        }

        element.sendKeys(value);

        return length == 0;
    }
}
