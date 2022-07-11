package org.wlpiaoyi.framework.utils.queue;

import static java.lang.Thread.sleep;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/11 16:34
 * @Version 1.0
 */
public class QueueFactory {

    public static Queue createDefault(){
        Queue queue = new QueueDefault();
        return queue;
    }

}
