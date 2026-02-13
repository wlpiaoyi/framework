package org.wlpiaoyi.framework.utils.xml;

import java.util.Collection;
import java.util.Map;

/**
 * <p<b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 用于封装XML对象, 适合小数据量的XML数据封装
 * </p>
 * <p><b>{@code @date:}</b>2026/2/5 9:32</p>
 * <p><b>{@code @version:}</b>1.0</p>
 */
public interface XmlObject {

    /**
     * <p><b>{@code @description:}</b>
     * 获取XML元素限定名
     * </p>
     *
     * <p><b>{@code @param:}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/5 9:32</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    String getQName();

    /**
     * <p><b>{@code @description:}</b>
     * 获取XML元素文本内容
     * </p>
     *
     * <p><b>{@code @param:}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/5 9:32</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    String getText();

    /**
     * <p><b>{@code @description:}</b>
     * 获取XML元素属性
     * </p>
     *
     * <p><b>{@code @param:}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/5 9:32</p>
     * <p><b>{@code @return:}</b>{@link Map<String, String>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    Map<String, String> getAttribute();

    /**
     * <p><b>{@code @description:}</b>
     * 获取XML元素子元素
     * </p>
     *
     * <p><b>{@code @param:}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/5 9:32</p>
     * <p><b>{@code @return:}</b>{@link Collection<XmlObject>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    Collection<XmlObject> getChildren();
}
