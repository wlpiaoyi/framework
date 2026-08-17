package org.wlpiaoyi.framework.utils.reflect.scan;

/** 带 @ScanMarker 的可实例化实体，扫描单个注解时应命中。 */
@ScanMarker
public class ScanEntity {
}

/** 同时带 @ScanMarker 与 @ScanOther，扫描两个注解时才应命中。 */
@ScanMarker
@ScanOther
class ScanBothEntity {
}

/** 未标注任何扫描注解，不应被扫描结果包含。 */
class ScanUnmarked {
}

/** 抽象类即使带 @ScanMarker 也应被排除。 */
@ScanMarker
abstract class ScanAbstract {
}

/** 接口即使带 @ScanMarker 也应被排除。 */
@ScanMarker
interface ScanInterface {
}

/** 枚举即使带 @ScanMarker 也应被排除。 */
@ScanMarker
enum ScanEnum {
    A
}
