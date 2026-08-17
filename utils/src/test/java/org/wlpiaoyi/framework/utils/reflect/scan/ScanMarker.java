package org.wlpiaoyi.framework.utils.reflect.scan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包扫描夹具注解：标记应被 {@code ClassModel.scanClasses} 命中的实体类。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScanMarker {
}
