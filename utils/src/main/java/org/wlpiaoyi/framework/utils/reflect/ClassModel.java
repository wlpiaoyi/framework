package org.wlpiaoyi.framework.utils.reflect;


import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.SystemException;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
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
        return Class.forName(classPath);
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
    public static <T> T newInstance(Class<T> clazz, Object ... initArgs) {
        try {
            Object[] args = initArgs == null ? new Object[0] : initArgs;
            Constructor<T> constructor = findDeclaredConstructor(clazz, args);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SystemException("实例化对象时出现错误,请尝试给 %s 添加当前构参的构造方法", e, clazz.getName());
        }
    }

    /**
     * 按构造参数匹配声明构造器：先按运行时类型精确查找，失败后再按可赋值类型（含包装类型与基本类型）匹配。
     */
    private static <T> Constructor<T> findDeclaredConstructor(Class<T> clazz, Object[] args) throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class<?>[args.length];
        boolean hasNullArg = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                hasNullArg = true;
                parameterTypes[i] = Object.class;
            } else {
                parameterTypes[i] = args[i].getClass();
            }
        }
        if (!hasNullArg) {
            try {
                return clazz.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException ignored) {
                // 可能是基本类型构造器，或形参是父类型，继续按可赋值匹配
            }
        }
        Constructor<T> matched = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (!isConstructorCompatible(constructor.getParameterTypes(), args)) {
                continue;
            }
            if (matched != null) {
                throw new NoSuchMethodException(clazz.getName() + " 存在多个可匹配的构造方法");
            }
            matched = (Constructor<T>) constructor;
        }
        if (matched == null) {
            throw new NoSuchMethodException(clazz.getName() + " 找不到对应参数的构造方法");
        }
        return matched;
    }

    private static boolean isConstructorCompatible(Class<?>[] parameterTypes, Object[] args) {
        if (parameterTypes.length != args.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!isArgumentCompatible(parameterTypes[i], args[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isArgumentCompatible(Class<?> parameterType, Object arg) {
        if (arg == null) {
            return !parameterType.isPrimitive();
        }
        Class<?> argumentType = arg.getClass();
        if (parameterType.isAssignableFrom(argumentType)) {
            return true;
        }
        return parameterType.isPrimitive() && parameterType == wrapperToPrimitive(argumentType);
    }

    private static Class<?> wrapperToPrimitive(Class<?> wrapperType) {
        if (wrapperType == Boolean.class) {
            return boolean.class;
        }
        if (wrapperType == Byte.class) {
            return byte.class;
        }
        if (wrapperType == Character.class) {
            return char.class;
        }
        if (wrapperType == Short.class) {
            return short.class;
        }
        if (wrapperType == Integer.class) {
            return int.class;
        }
        if (wrapperType == Long.class) {
            return long.class;
        }
        if (wrapperType == Float.class) {
            return float.class;
        }
        if (wrapperType == Double.class) {
            return double.class;
        }
        return null;
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取类声明的字段列表（含父类，包含实例字段与静态字段）。
     * 未指定注解时返回全部字段；指定注解时仅返回同时标注了全部给定注解的字段。
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/8/7 15:42</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @SafeVarargs
    public static List<Field> getDeclaredFields(Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return collectDeclaredFields(clazz, null, annotationClasses);
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取类声明的实例字段列表（含父类，不含静态字段）。
     * 未指定注解时返回全部实例字段；指定注解时仅返回同时标注了全部给定注解的实例字段。
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/8/7 15:42</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @SafeVarargs
    public static List<Field> getDeclaredInstanceFields(Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return collectDeclaredFields(clazz, false, annotationClasses);
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取类声明的静态字段列表（含父类，不含实例字段）。
     * 未指定注解时返回全部静态字段；指定注解时仅返回同时标注了全部给定注解的静态字段。
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/8/17 09:31</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @SafeVarargs
    public static List<Field> getDeclaredStaticFields(Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return collectDeclaredFields(clazz, true, annotationClasses);
    }


    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取类声明的方法列表（含父类，包含实例方法与静态方法，排除 bridge/synthetic）。
     * 未指定注解时返回全部方法；指定注解时仅返回同时标注了全部给定注解的方法。
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/8/17 09:39</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @SafeVarargs
    public static List<Method> getDeclaredMethods(Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return collectDeclaredMethods(clazz, null, annotationClasses);
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取类声明的实例方法列表（含父类，不含静态方法，排除 bridge/synthetic）。
     * 未指定注解时返回全部实例方法；指定注解时仅返回同时标注了全部给定注解的实例方法。
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/8/11 17:31</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @SafeVarargs
    public static List<Method> getDeclaredInstanceMethods(Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return collectDeclaredMethods(clazz, false, annotationClasses);
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取类声明的静态方法列表（含父类，不含实例方法，排除 bridge/synthetic）。
     * 未指定注解时返回全部静态方法；指定注解时仅返回同时标注了全部给定注解的静态方法。
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/8/17 09:31</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @SafeVarargs
    public static List<Method> getDeclaredStaticMethods(Class<?> clazz, Class<? extends Annotation>... annotationClasses) {
        return collectDeclaredMethods(clazz, true, annotationClasses);
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 扫描指定包及其子包，返回同时标注了全部给定注解的实体类。
     * 排除接口、抽象类、枚举、注解类型及内部类；未指定注解时返回空列表。
     * </div>
     * </p>
     * <p><b>{@code @date:}</b>           2026/8/17 09:22</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     * <hr/>
     */
    @SafeVarargs
    public static List<Class<?>> scanClasses(String packageName, Class<? extends Annotation>... annotationClasses) {
        Objects.requireNonNull(packageName, "packageName");
        if (ValueUtils.isBlank(packageName) || ValueUtils.isBlank(annotationClasses)) {
            return new ArrayList<>();
        }
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = resolveClassLoader();
        List<Class<?>> result = new ArrayList<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    scanDirectory(toFile(url), packageName, classLoader, annotationClasses, result);
                } else if ("jar".equals(protocol)) {
                    scanJar(url, packagePath, packageName, classLoader, annotationClasses, result);
                }
            }
        } catch (IOException e) {
            throw new SystemException("扫描包 %s 时出现错误", e, packageName);
        }
        return result;
    }

    private static ClassLoader resolveClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            return classLoader;
        }
        return ClassModel.class.getClassLoader();
    }

    private static File toFile(URL url) {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8));
        }
    }

    private static void scanDirectory(File directory, String packageName, ClassLoader classLoader,
                                      Class<? extends Annotation>[] annotationClasses, List<Class<?>> result) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classLoader, annotationClasses, result);
                continue;
            }
            String fileName = file.getName();
            if (!fileName.endsWith(".class") || fileName.contains("$")) {
                continue;
            }
            String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
            addIfMatch(className, classLoader, annotationClasses, result);
        }
    }

    private static void scanJar(URL url, String packagePath, String packageName, ClassLoader classLoader,
                                Class<? extends Annotation>[] annotationClasses, List<Class<?>> result) throws IOException {
        String path = url.getPath();
        int separatorIndex = path.indexOf("!/");
        String jarPath = separatorIndex >= 0 ? path.substring(0, separatorIndex) : path;
        if (jarPath.startsWith("file:")) {
            jarPath = jarPath.substring(5);
        }
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);
        while (jarPath.startsWith("//")) {
            jarPath = jarPath.substring(1);
        }
        if (jarPath.length() > 2 && jarPath.charAt(0) == '/'
                && Character.isLetter(jarPath.charAt(1)) && jarPath.charAt(2) == ':') {
            jarPath = jarPath.substring(1);
        }
        String prefix = packagePath + "/";
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.endsWith(".class") || name.contains("$")) {
                    continue;
                }
                int packageIndex = resolvePackageIndex(name, prefix);
                if (packageIndex < 0) {
                    continue;
                }
                String classFilePath = name.substring(packageIndex);
                String className = classFilePath.substring(0, classFilePath.length() - 6).replace('/', '.');
                if (!className.startsWith(packageName + ".") && !className.equals(packageName)) {
                    continue;
                }
                addIfMatch(className, classLoader, annotationClasses, result);
            }
        }
    }

    /**
     * 定位包路径在 JAR 条目中的起始位置，兼容 Spring Boot 的 BOOT-INF/classes 前缀。
     */
    private static int resolvePackageIndex(String entryName, String prefix) {
        if (entryName.startsWith(prefix)) {
            return 0;
        }
        String nestedPrefix = "/" + prefix;
        int nestedIndex = entryName.indexOf(nestedPrefix);
        if (nestedIndex >= 0) {
            return nestedIndex + 1;
        }
        return -1;
    }

    private static void addIfMatch(String className, ClassLoader classLoader,
                                   Class<? extends Annotation>[] annotationClasses, List<Class<?>> result) {
        if (className.endsWith(".package-info") || className.endsWith(".module-info")) {
            return;
        }
        try {
            Class<?> clazz = Class.forName(className, false, classLoader);
            if (!isEntityClass(clazz)) {
                return;
            }
            if (hasAllAnnotations(clazz, annotationClasses)) {
                result.add(clazz);
            }
        } catch (ClassNotFoundException | LinkageError ignored) {
            // 跳过无法加载的类
            log.debug("无法加载类 {}", className);
        }
    }

    /**
     * 是否为可实例化的实体类（排除接口、抽象类、枚举、注解及匿名/合成类）。
     */
    private static boolean isEntityClass(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        return !clazz.isInterface()
                && !clazz.isAnnotation()
                && !clazz.isEnum()
                && !clazz.isAnonymousClass()
                && !clazz.isLocalClass()
                && !clazz.isSynthetic()
                && !Modifier.isAbstract(modifiers);
    }

    /**
     * 收集类及其父类中声明的字段。
     *
     * @param staticMember true 仅静态字段，false 仅实例字段，null 全部字段
     */
    private static List<Field> collectDeclaredFields(Class<?> clazz, Boolean staticMember,
                                                     Class<? extends Annotation>[] annotationClasses) {
        Objects.requireNonNull(clazz, "clazz");
        List<Field> result = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (staticMember != null && Modifier.isStatic(field.getModifiers()) != staticMember) {
                    continue;
                }
                if (hasAllAnnotations(field, annotationClasses)) {
                    result.add(field);
                }
            }
            current = current.getSuperclass();
        }
        return result;
    }

    /**
     * 收集类及其父类中声明的方法，排除编译器生成的 bridge/synthetic 方法。
     *
     * @param staticMember true 仅静态方法，false 仅实例方法，null 全部方法
     */
    private static List<Method> collectDeclaredMethods(Class<?> clazz, Boolean staticMember,
                                                       Class<? extends Annotation>[] annotationClasses) {
        Objects.requireNonNull(clazz, "clazz");
        List<Method> result = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.isSynthetic() || method.isBridge()) {
                    continue;
                }
                if (staticMember != null && Modifier.isStatic(method.getModifiers()) != staticMember) {
                    continue;
                }
                if (hasAllAnnotations(method, annotationClasses)) {
                    result.add(method);
                }
            }
            current = current.getSuperclass();
        }
        return result;
    }

    /**
     * 未指定注解时视为匹配；指定注解时须同时具备全部给定注解。
     */
    private static boolean hasAllAnnotations(AnnotatedElement element, Class<? extends Annotation>[] annotationClasses) {
        if (ValueUtils.isBlank(annotationClasses)) {
            return true;
        }
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (annotationClass == null) {
                continue;
            }
            if (!element.isAnnotationPresent(annotationClass)) {
                return false;
            }
        }
        return true;
    }

}
