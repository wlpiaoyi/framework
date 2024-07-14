package org.wlpiaoyi.framework.lab.selenium.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.wlpiaoyi.framework.lab.selenium.Browser;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>    元素操控</p>
 * <p><b>{@code @date:}</b>           2024/7/14 10:24</p>
 * <p><b>{@code @version:}</b>        1.0</p>
 */

public class WebElementUtils {

    /**
     * <p><b>{@code @description:}</b>
     * 出发click事件
     * </p>
     *
     * <p><b>@param</b> <b>browser</b>
     * {@link Browser}
     * </p>
     *
     * <p><b>@param</b> <b>ele</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/7/14 10:23</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void click(Browser browser, WebElement ele){
        try{
            ele.click();
        }catch (Exception e){
            ((JavascriptExecutor)browser.getDriver()).executeScript("arguments[0].click();", ele);
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取元素Value
     * </p>
     *
     * <p><b>@param</b> <b>element</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/7/14 10:23</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String getValue(WebElement element){
        String value = element.getText();
        if(value == null){
            value = element.getAttribute("value");
        }
        return value;
    }


    /**
     * <p><b>{@code @description:}</b>
     * 给元素设置value
     * </p>
     *
     * <p><b>@param</b> <b>element</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>@param</b> <b>value</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/7/14 10:22</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
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
