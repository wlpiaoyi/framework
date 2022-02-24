package org.wlpiaoyi.framework.utils.algorithm;

import java.util.Collection;
import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/2/17 10:34 AM
 * @Version 1.0
 */
public interface Algorithm<T> {

     T execute(T... values);
     T execute(List<T> values);
}
