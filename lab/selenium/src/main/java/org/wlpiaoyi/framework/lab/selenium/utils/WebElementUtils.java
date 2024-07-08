package org.wlpiaoyi.framework.lab.selenium.utils;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class WebElementUtils {


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
