package org.wlpiaoyi.framework.utils.reflect;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.exception.SystemException;
import org.wlpiaoyi.framework.utils.reflect.scan.ScanMarker;
import org.wlpiaoyi.framework.utils.reflect.scan.ScanOther;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    ClassModel 反射工具测试。
 *                          覆盖类加载、构造器实例化、字段/方法收集（含子类与注解过滤）以及包扫描。
 * {@code @date:}           2026/8/17
 * {@code @version:}:       1.0
 */
public class ClassModelTest {

    /** 包扫描夹具所在包，含本包与 nested 子包中的实体类 */
    private static final String SCAN_PACKAGE = "org.wlpiaoyi.framework.utils.reflect.scan";

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * 按全限定名加载已存在的类，应返回对应 Class。
     */
    @Test
    public void testGetClass() throws ClassNotFoundException {
        Class<?> clazz = ClassModel.getClass(NoArgBean.class.getName());
        assertEquals(NoArgBean.class, clazz);
    }

    /**
     * 按全限定名加载不存在的类，应抛出 ClassNotFoundException。
     */
    @Test(expected = ClassNotFoundException.class)
    public void testGetClassNotFound() throws ClassNotFoundException {
        ClassModel.getClass("org.wlpiaoyi.framework.utils.reflect.NotExistsClass");
    }

    /**
     * 无参构造：不传 initArgs 时应走默认构造器并成功实例化。
     */
    @Test
    public void testNewInstanceNoArg() {
        NoArgBean bean = ClassModel.newInstance(NoArgBean.class);
        assertNotNull(bean);
    }

    /**
     * 无参构造兼容：显式传入 null 可变参数时，应视为无参并成功实例化。
     */
    @Test
    public void testNewInstanceWithNullVarArgs() {
        NoArgBean bean = ClassModel.newInstance(NoArgBean.class, (Object[]) null);
        assertNotNull(bean);
    }

    /**
     * 有参构造：按实参运行时类型匹配构造器，并校验注入的字段值。
     */
    @Test
    public void testNewInstanceWithArgs() {
        ArgsBean bean = ClassModel.newInstance(ArgsBean.class, "alice", 18);
        assertEquals("alice", bean.name);
        assertEquals(18, bean.age);
    }

    /**
     * 基本类型构造器：实参是包装类型 Integer，应能匹配形参为 int 的构造器。
     */
    @Test
    public void testNewInstancePrimitiveConstructor() {
        PrimitiveBean bean = ClassModel.newInstance(PrimitiveBean.class, 7);
        assertEquals(7, bean.value);
    }

    /**
     * 父类型构造器：实参是 Integer，应能匹配形参为 Number 的构造器。
     */
    @Test
    public void testNewInstanceParentType() {
        NumberBean bean = ClassModel.newInstance(NumberBean.class, 12);
        assertEquals(12, bean.value);
    }

    /**
     * 私有构造器：应通过 setAccessible 完成实例化，而不是因访问权限失败。
     */
    @Test
    public void testNewInstancePrivateConstructor() {
        PrivateCtorBean bean = ClassModel.newInstance(PrivateCtorBean.class, "secret");
        assertEquals("secret", bean.value);
    }

    /**
     * null 实参：应匹配非基本类型构造器（String），并把 null 传进去。
     */
    @Test
    public void testNewInstanceNullArg() {
        NullArgBean bean = ClassModel.newInstance(NullArgBean.class, new Object[]{null});
        assertNull(bean.value);
    }

    /**
     * 找不到匹配构造器：ArgsBean 只有 (String, int)，传入单个 int 时应抛 SystemException。
     */
    @Test(expected = SystemException.class)
    public void testNewInstanceNoMatchingConstructor() {
        ClassModel.newInstance(ArgsBean.class, 1);
    }

    /**
     * 构造器歧义：String 同时可赋给 CharSequence 与 Serializable，存在多个匹配时应抛 SystemException。
     */
    @Test(expected = SystemException.class)
    public void testNewInstanceAmbiguousConstructor() {
        ClassModel.newInstance(AmbiguousBean.class, "text");
    }

    /**
     * null 实参歧义：null 可匹配多个非基本类型构造器，存在多个匹配时应抛 SystemException。
     */
    @Test(expected = SystemException.class)
    public void testNewInstanceAmbiguousNullArg() {
        ClassModel.newInstance(AmbiguousBean.class, new Object[]{null});
    }

    /**
     * 收集全部声明字段：含子类、父类，以及实例字段和静态字段。
     */
    @Test
    public void testGetDeclaredFields() {
        List<Field> fields = ClassModel.getDeclaredFields(ChildType.class);
        Set<String> names = fieldNames(fields);
        // 父类字段
        assertTrue(names.contains("parentInstance"));
        assertTrue(names.contains("parentStatic"));
        assertTrue(names.contains("parentAnnoA"));
        // 子类字段
        assertTrue(names.contains("childInstance"));
        assertTrue(names.contains("childStatic"));
        assertTrue(names.contains("childBoth"));
        assertTrue(names.contains("childOnlyA"));
        // 不应把 Class 对象本身当成业务字段
        assertFalse(names.contains("class"));
    }

    /**
     * 仅收集实例字段：含子类与父类，但不包含静态字段。
     */
    @Test
    public void testGetDeclaredInstanceFields() {
        List<Field> fields = ClassModel.getDeclaredInstanceFields(ChildType.class);
        Set<String> names = fieldNames(fields);
        assertTrue(names.contains("parentInstance"));
        assertTrue(names.contains("childInstance"));
        assertFalse(names.contains("parentStatic"));
        assertFalse(names.contains("childStatic"));
        for (Field field : fields) {
            assertFalse(Modifier.isStatic(field.getModifiers()));
        }
    }

    /**
     * 仅收集静态字段：含子类与父类，但不包含实例字段。
     */
    @Test
    public void testGetDeclaredStaticFields() {
        List<Field> fields = ClassModel.getDeclaredStaticFields(ChildType.class);
        Set<String> names = fieldNames(fields);
        assertTrue(names.contains("parentStatic"));
        assertTrue(names.contains("childStatic"));
        assertFalse(names.contains("parentInstance"));
        assertFalse(names.contains("childInstance"));
        for (Field field : fields) {
            assertTrue(Modifier.isStatic(field.getModifiers()));
        }
    }

    /**
     * 按注解过滤字段：指定单个注解时返回带该注解的字段；指定多个注解时必须同时具备全部注解。
     */
    @Test
    public void testGetDeclaredFieldsByAnnotation() {
        // 只要带 @FieldA：父类 parentAnnoA、子类 childBoth / childOnlyA
        List<Field> onlyA = ClassModel.getDeclaredFields(ChildType.class, FieldA.class);
        Set<String> onlyANames = fieldNames(onlyA);
        assertTrue(onlyANames.contains("parentAnnoA"));
        assertTrue(onlyANames.contains("childBoth"));
        assertTrue(onlyANames.contains("childOnlyA"));
        assertFalse(onlyANames.contains("childInstance"));

        // 同时带 @FieldA 和 @FieldB：只有 childBoth
        List<Field> both = ClassModel.getDeclaredFields(ChildType.class, FieldA.class, FieldB.class);
        Set<String> bothNames = fieldNames(both);
        assertEquals(Set.of("childBoth"), bothNames);
    }

    /**
     * 实例字段 + 注解过滤：只返回带指定注解的实例字段，静态字段即使带注解也不应出现。
     */
    @Test
    public void testGetDeclaredInstanceFieldsByAnnotation() {
        List<Field> fields = ClassModel.getDeclaredInstanceFields(ChildType.class, FieldA.class);
        Set<String> names = fieldNames(fields);
        assertTrue(names.contains("parentAnnoA"));
        assertTrue(names.contains("childOnlyA"));
        assertFalse(names.contains("childStatic"));
    }

    /**
     * clazz 为 null 时，字段收集应抛出 NullPointerException。
     */
    @Test(expected = NullPointerException.class)
    public void testGetDeclaredFieldsNullClass() {
        ClassModel.getDeclaredFields(null);
    }

    /**
     * 收集全部声明方法：含子类、父类，以及实例方法和静态方法；不包含 Object 上的方法。
     */
    @Test
    public void testGetDeclaredMethods() {
        List<Method> methods = ClassModel.getDeclaredMethods(ChildType.class);
        Set<String> names = methodNames(methods);
        // 父类方法
        assertTrue(names.contains("parentInstanceMethod"));
        assertTrue(names.contains("parentStaticMethod"));
        assertTrue(names.contains("parentAnnoAMethod"));
        // 子类方法
        assertTrue(names.contains("childInstanceMethod"));
        assertTrue(names.contains("childStaticMethod"));
        assertTrue(names.contains("childBothMethod"));
        assertTrue(names.contains("childOnlyAMethod"));
        // 遍历在 Object 处停止，不应包含 Object 方法
        assertFalse(names.contains("toString"));
        assertFalse(names.contains("equals"));
        assertFalse(names.contains("hashCode"));
    }

    /**
     * 仅收集实例方法：含子类与父类，但不包含静态方法，也不包含 bridge/synthetic 方法。
     */
    @Test
    public void testGetDeclaredInstanceMethods() {
        List<Method> methods = ClassModel.getDeclaredInstanceMethods(ChildType.class);
        Set<String> names = methodNames(methods);
        assertTrue(names.contains("parentInstanceMethod"));
        assertTrue(names.contains("childInstanceMethod"));
        assertFalse(names.contains("parentStaticMethod"));
        assertFalse(names.contains("childStaticMethod"));
        for (Method method : methods) {
            assertFalse(Modifier.isStatic(method.getModifiers()));
            assertFalse(method.isBridge());
            assertFalse(method.isSynthetic());
        }
    }

    /**
     * 仅收集静态方法：含子类与父类，但不包含实例方法。
     */
    @Test
    public void testGetDeclaredStaticMethods() {
        List<Method> methods = ClassModel.getDeclaredStaticMethods(ChildType.class);
        Set<String> names = methodNames(methods);
        assertTrue(names.contains("parentStaticMethod"));
        assertTrue(names.contains("childStaticMethod"));
        assertFalse(names.contains("parentInstanceMethod"));
        assertFalse(names.contains("childInstanceMethod"));
        for (Method method : methods) {
            assertTrue(Modifier.isStatic(method.getModifiers()));
        }
    }

    /**
     * 按注解过滤方法：指定单个注解时返回带该注解的方法；指定多个注解时必须同时具备全部注解。
     */
    @Test
    public void testGetDeclaredMethodsByAnnotation() {
        // 只要带 @MethodA：父类 parentAnnoAMethod、子类 childBothMethod / childOnlyAMethod
        List<Method> onlyA = ClassModel.getDeclaredMethods(ChildType.class, MethodA.class);
        Set<String> onlyANames = methodNames(onlyA);
        assertTrue(onlyANames.contains("parentAnnoAMethod"));
        assertTrue(onlyANames.contains("childBothMethod"));
        assertTrue(onlyANames.contains("childOnlyAMethod"));
        assertFalse(onlyANames.contains("childInstanceMethod"));

        // 同时带 @MethodA 和 @MethodB：只有 childBothMethod
        List<Method> both = ClassModel.getDeclaredMethods(ChildType.class, MethodA.class, MethodB.class);
        assertEquals(Set.of("childBothMethod"), methodNames(both));
    }

    /**
     * 排除编译器生成的 bridge/synthetic 方法。
     * GenericChild 覆写泛型父类方法后，Class.getDeclaredMethods 会带上 bridge 方法，
     * ClassModel 收集结果中不应再包含这些方法，且子类自身只保留一份 identity(String)。
     */
    @Test
    public void testGetDeclaredMethodsExcludeBridge() {
        boolean hasBridge = false;
        for (Method method : GenericChild.class.getDeclaredMethods()) {
            if (method.isBridge() || method.isSynthetic()) {
                hasBridge = true;
                break;
            }
        }
        // 先确认夹具本身会产生 bridge，避免测试变成空断言
        assertTrue("测试夹具应产生 bridge/synthetic 方法", hasBridge);

        List<Method> methods = ClassModel.getDeclaredMethods(GenericChild.class);
        for (Method method : methods) {
            assertFalse(method.getName() + " 不应包含 bridge/synthetic 方法", method.isBridge());
            assertFalse(method.getName() + " 不应包含 bridge/synthetic 方法", method.isSynthetic());
        }
        // 父类 identity(T) 仍会作为父类声明方法被收集；这里只校验子类自己声明的那一份
        List<Method> childIdentities = methods.stream()
                .filter(method -> method.getDeclaringClass() == GenericChild.class)
                .filter(method -> "identity".equals(method.getName()))
                .collect(Collectors.toList());
        assertEquals(1, childIdentities.size());
        assertEquals(String.class, childIdentities.get(0).getReturnType());
    }

    /**
     * clazz 为 null 时，方法收集应抛出 NullPointerException。
     */
    @Test(expected = NullPointerException.class)
    public void testGetDeclaredMethodsNullClass() {
        ClassModel.getDeclaredMethods(null);
    }

    /**
     * packageName 为 null 时，包扫描应抛出 NullPointerException。
     */
    @Test(expected = NullPointerException.class)
    public void testScanClassesNullPackage() {
        ClassModel.scanClasses(null, ScanMarker.class);
    }

    /**
     * 包名为空、未指定注解、注解数组为 null 时，扫描应直接返回空列表，而不是继续扫描。
     */
    @Test
    public void testScanClassesBlankArgs() {
        assertTrue(ClassModel.scanClasses("", ScanMarker.class).isEmpty());
        assertTrue(ClassModel.scanClasses(SCAN_PACKAGE).isEmpty());
        assertTrue(ClassModel.scanClasses(SCAN_PACKAGE, (Class[]) null).isEmpty());
    }

    /**
     * 按单个注解扫描实体类：应包含本包与子包中带 @ScanMarker 的可实例化类，
     * 排除未标注类、抽象类、接口、枚举以及内部类。
     */
    @Test
    public void testScanClassesByMarker() throws ClassNotFoundException {
        List<Class<?>> classes = ClassModel.scanClasses(SCAN_PACKAGE, ScanMarker.class);
        Set<String> names = classNames(classes);

        // 应命中的实体类（含子包）
        assertTrue(names.contains(SCAN_PACKAGE + ".ScanEntity"));
        assertTrue(names.contains(SCAN_PACKAGE + ".ScanBothEntity"));
        assertTrue(names.contains(SCAN_PACKAGE + ".nested.ScanNestedEntity"));
        // 不应命中：无注解、抽象类、接口、枚举
        assertFalse(names.contains(SCAN_PACKAGE + ".ScanUnmarked"));
        assertFalse(names.contains(SCAN_PACKAGE + ".ScanAbstract"));
        assertFalse(names.contains(SCAN_PACKAGE + ".ScanInterface"));
        assertFalse(names.contains(SCAN_PACKAGE + ".ScanEnum"));
        // 内部类 class 文件名含 $，扫描时应跳过
        assertFalse(names.stream().anyMatch(name -> name.contains("$")));

        for (Class<?> clazz : classes) {
            assertFalse(clazz.isInterface());
            assertFalse(clazz.isEnum());
            assertFalse(clazz.isAnnotation());
            assertFalse(Modifier.isAbstract(clazz.getModifiers()));
        }

        assertEquals(Class.forName(SCAN_PACKAGE + ".ScanEntity"),
                classes.stream().filter(clazz -> clazz.getName().endsWith(".ScanEntity")).findFirst().orElseThrow());
    }

    /**
     * 按多个注解扫描：必须同时具备全部给定注解，因此只有 ScanBothEntity 应被返回。
     */
    @Test
    public void testScanClassesRequireAllAnnotations() {
        List<Class<?>> classes = ClassModel.scanClasses(SCAN_PACKAGE, ScanMarker.class, ScanOther.class);
        assertEquals(Set.of(SCAN_PACKAGE + ".ScanBothEntity"), classNames(classes));
    }

    /**
     * JAR 协议扫描：把测试夹具打成临时 JAR，切换上下文 ClassLoader 后再扫描，
     * 应仍能找到带 @ScanMarker 的实体类，并继续排除抽象类。
     */
    @Test
    public void testScanClassesFromJar() throws Exception {
        URL resource = ClassModelTest.class.getClassLoader().getResource(SCAN_PACKAGE.replace('.', '/'));
        assertNotNull("扫描包应存在于测试 classpath", resource);
        File directory = new File(resource.toURI());
        assertTrue(directory.isDirectory());

        File jarFile = File.createTempFile("classmodel-scan", ".jar");
        jarFile.deleteOnExit();
        packDirectoryToJar(directory, SCAN_PACKAGE.replace('.', '/'), jarFile);

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader jarLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, previous)) {
            Thread.currentThread().setContextClassLoader(jarLoader);
            List<Class<?>> classes = ClassModel.scanClasses(SCAN_PACKAGE, ScanMarker.class);
            Set<String> names = classNames(classes);
            assertTrue(names.contains(SCAN_PACKAGE + ".ScanEntity"));
            assertTrue(names.contains(SCAN_PACKAGE + ".ScanBothEntity"));
            assertTrue(names.contains(SCAN_PACKAGE + ".nested.ScanNestedEntity"));
            assertFalse(names.contains(SCAN_PACKAGE + ".ScanAbstract"));
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    /** 提取字段名集合，便于断言。 */
    private static Set<String> fieldNames(List<Field> fields) {
        return fields.stream().map(Field::getName).collect(Collectors.toSet());
    }

    /** 提取方法名集合，便于断言。 */
    private static Set<String> methodNames(List<Method> methods) {
        return methods.stream().map(Method::getName).collect(Collectors.toSet());
    }

    /** 提取类全限定名集合，便于断言。 */
    private static Set<String> classNames(List<Class<?>> classes) {
        Set<String> names = new HashSet<>();
        for (Class<?> clazz : classes) {
            names.add(clazz.getName());
        }
        return names;
    }

    /**
     * 将指定目录打包为临时 JAR，用于覆盖 jar 协议扫描路径。
     */
    private static void packDirectoryToJar(File directory, String entryPrefix, File jarFile) throws IOException {
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile))) {
            addDirectoryToJar(directory, entryPrefix, jarOutputStream);
        }
    }

    /**
     * 递归把目录写入 JAR，并保留包路径前缀。
     */
    private static void addDirectoryToJar(File directory, String entryPrefix, JarOutputStream jarOutputStream) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String entryName = entryPrefix + "/" + file.getName();
            if (file.isDirectory()) {
                addDirectoryToJar(file, entryName, jarOutputStream);
                continue;
            }
            jarOutputStream.putNextEntry(new JarEntry(entryName));
            Files.copy(file.toPath(), jarOutputStream);
            jarOutputStream.closeEntry();
        }
    }

    /** 字段过滤注解 A，用于验证单注解匹配。 */
    @Retention(RetentionPolicy.RUNTIME)
    private @interface FieldA {
    }

    /** 字段过滤注解 B，与 {@link FieldA} 组合用于验证“同时具备全部注解”。 */
    @Retention(RetentionPolicy.RUNTIME)
    private @interface FieldB {
    }

    /** 方法过滤注解 A，用于验证单注解匹配。 */
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MethodA {
    }

    /** 方法过滤注解 B，与 {@link MethodA} 组合用于验证“同时具备全部注解”。 */
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MethodB {
    }

    /** 无参构造夹具。 */
    public static class NoArgBean {
    }

    /** 有参构造夹具：String + int。 */
    public static class ArgsBean {
        public final String name;
        public final int age;

        public ArgsBean(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    /** 基本类型构造夹具：形参为 int，用于验证包装类型 Integer 可匹配。 */
    public static class PrimitiveBean {
        public final int value;

        public PrimitiveBean(int value) {
            this.value = value;
        }
    }

    /** 父类型构造夹具：形参为 Number，用于验证 Integer 可按可赋值类型匹配。 */
    public static class NumberBean {
        public final Number value;

        public NumberBean(Number value) {
            this.value = value;
        }
    }

    /** 私有构造夹具：验证 setAccessible 后仍可实例化。 */
    public static class PrivateCtorBean {
        public final String value;

        private PrivateCtorBean(String value) {
            this.value = value;
        }
    }

    /** null 实参夹具：非基本类型构造器可接收 null。 */
    public static class NullArgBean {
        public final String value;

        public NullArgBean(String value) {
            this.value = value;
        }
    }

    /**
     * 歧义构造夹具：CharSequence 与 Serializable 都能接收 String 或 null，
     * 用于验证多个匹配构造器时抛出异常。
     */
    public static class AmbiguousBean {
        public AmbiguousBean(CharSequence value) {
        }

        public AmbiguousBean(Serializable value) {
        }
    }

    /**
     * 父类夹具：同时声明实例/静态字段与方法，部分带注解，
     * 用于验证收集结果会向上遍历父类。
     */
    public static class ParentType {
        public String parentInstance;
        public static String parentStatic;
        @FieldA
        protected String parentAnnoA;

        public void parentInstanceMethod() {
        }

        public static void parentStaticMethod() {
        }

        @MethodA
        protected void parentAnnoAMethod() {
        }
    }

    /**
     * 子类夹具：补充实例/静态成员，以及单注解、双注解字段和方法，
     * 用于验证继承收集与注解 AND 过滤。
     */
    public static class ChildType extends ParentType {
        public String childInstance;
        public static String childStatic;
        @FieldA
        @FieldB
        private String childBoth;
        @FieldA
        String childOnlyA;

        public void childInstanceMethod() {
        }

        public static void childStaticMethod() {
        }

        @MethodA
        @MethodB
        private void childBothMethod() {
        }

        @MethodA
        void childOnlyAMethod() {
        }
    }

    /** 泛型父类：覆写后会在子类上生成 bridge 方法。 */
    public static class GenericParent<T> {
        public T identity(T value) {
            return value;
        }
    }

    /** 泛型子类：用于验证 ClassModel 会排除 bridge/synthetic 方法。 */
    public static class GenericChild extends GenericParent<String> {
        @Override
        public String identity(String value) {
            return value;
        }
    }
}
