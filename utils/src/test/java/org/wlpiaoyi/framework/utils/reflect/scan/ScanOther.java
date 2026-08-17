package org.wlpiaoyi.framework.utils.reflect.scan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包扫描夹具注解：与 {@link ScanMarker} 组合，验证必须同时具备全部注解才会命中。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScanOther {
}
