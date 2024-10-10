package org.wlpiaoyi.framework.utils.reflect;

import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassModel {

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>classPath</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/26 17:14</p>
     * <p><b>{@code @return:}</b>{@link Class<?>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Class<?> getClass(String classPath) throws ClassNotFoundException {
        return Class.forName(classPath).getDeclaringClass();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 根据指定的 class ， 实例化一个对象，根据构造参数来实例化
     * </p>
     *
     * <p><b>@param</b> <b>clazz</b>
     * {@link Class<T>}
     * </p>
     *
     * <p><b>@param</b> <b>initargs</b>
     * {@link Object...}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/26 17:20</p>
     * <p><b>{@code @return:}</b>{@link T}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static <T> T newInstance(Class<T> clazz, Object ... initargs) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BusinessException("实例化对象时出现错误,请尝试给 %s 添加当前构参的构造方法", e, clazz.getName());
        }
    }

}
