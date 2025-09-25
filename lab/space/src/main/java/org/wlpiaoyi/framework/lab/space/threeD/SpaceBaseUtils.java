package org.wlpiaoyi.framework.lab.space.threeD;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-09-12 10:02:03</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class SpaceBaseUtils {

    /**
     * <p><b>{@code @description:}</b>
     * 计算线段相对于水平面和垂直面的夹角
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>p1</b>
     * {@link Coordinate3D}
     * 起点坐标
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>p2</b>
     * {@link Coordinate3D}
     * 终点坐标
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/9/12 10:02</p>
     * <p><b>{@code @return:}</b>{@link RotateDegrees}</p>
     * 包含两个角度 [水平面夹角, 垂直面夹角]（单位：度）
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static RotateDegrees originDegrees(Coordinate3D vector) {
        final double distance = vector.magnitude();
        final double dx = vector.getX();
        final double dy = vector.getY();
        final double dz = vector.getZ();

        // 1. 计算相对于水平面（XY平面）的夹角（俯仰角）
        double hShadowL = Math.sqrt(dx * dx + dy * dy);
        double horizontalAngle = Math.toDegrees(Math.acos(hShadowL / distance));

        // 2. 计算相对于垂直面（XZ平面）的夹角（偏航角）
        double vShadowL = Math.sqrt(dx * dx + dz * dz);
        double verticalAngle = Math.toDegrees(Math.acos(vShadowL / distance));

        return RotateDegrees.builder().horizontal(horizontalAngle).vertical(verticalAngle).build();
    }


    /**
     * <p><b>{@code @description:}</b>
     * 获取向量之间的夹角
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>vector1</b>
     * {@link Coordinate3D}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>vector2</b>
     * {@link Coordinate3D}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/9/14 18:20</p>
     * <p><b>{@code @return:}</b>{@link double}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static double degreesByVectors(Coordinate3D vector1, Coordinate3D vector2) {

        // 计算两个方向向量的点积（内积）
        // 公式：a·b = a.xb.x + a.yb.y + a.z*b.z
        double dotProduct = vector1.dotProduct(vector2);

        // 计算第一个向量的模（长度）
        // 公式：|a| = √(a.x² + a.y² + a.z²)
        double magnitude1 = vector1.magnitude();
        // 计算第二个向量的模（长度）
        double magnitude2 = vector2.magnitude();

        // 计算两个向量夹角的余弦值
        // 公式：cosθ = (a·b) / (|a|*|b|)
        double cosTheta = dotProduct / (magnitude1 * magnitude2);

        // 处理浮点数计算可能导致的精度问题，确保cosTheta在[-1,1]范围内
        // 因为acos函数的参数必须在这个范围内
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));

        // 计算反余弦得到弧度值，然后转换为度数
        // 注意：这里返回的是两条直线的最小夹角（0°~90°）
        return Math.toDegrees(Math.acos(cosTheta));
    }


//    public RotateDegrees[] rotateByVectors() throws Exception {
//        Coordinate3D vector1 = Coordinate3D.builder().x(3).y(-3.99).z(5).build();
//        double hDegrees = 10;
//        Coordinate3D vector2 = SpaceUtil.vectorToHRotate(vector1, hDegrees);
//        double circleDegrees = 181;
////        Coordinate3D vector3 = Coordinate3D.builder().x(1.11520381751429150).y(-0.09756769155333354).z(5).build();//
//
//        Coordinate3D vector3 = SpaceUtil.vectorToCircle(vector2, circleDegrees);
//        vector1 = Coordinate3D.builder().x(2.6).y(6.0).z(1.06).build();
//        vector2 = Coordinate3D.builder().x(7.17).y(0).z(2.3).build().sub(vector1, false);
//        vector3 = Coordinate3D.builder().x(7.17).y(5.95).z(2.4).build().sub(vector1, false);
//        RotateDegrees res = SpaceUtil.rotateByVectors(vector2, vector3);
//        System.out.println("rotateByVectors vector1: (" + vector1.getX() + ", " + vector1.getY() + ", " + vector1.getZ() + ") distance: " + vector1.magnitude());
//        System.out.println("rotateByVectors vector2: (" + vector2.getX() + ", " + vector2.getY() + ", " + vector2.getZ() + ") distance: " + vector2.magnitude());
//        System.out.println("rotateByVectors vector3: (" + vector3.getX() + ", " + vector3.getY() + ", " + vector3.getZ() + ") distance: " + vector3.magnitude());
//
//        System.out.println("rotateByVectors res: (" + res.getHorizontal() + ", " + res.getVertical() + ")");
//        RotateDegrees diffRotate = RotateDegrees.builder().horizontal(9).vertical(0).build();
//        RotateDegrees relaLLRotate = RotateDegrees.builder().horizontal(res.getHorizontal() + diffRotate.getHorizontal()).vertical((diffRotate.getVertical() - res.getVertical() + 360) % 360).build();
//        RotateDegrees relaRURotate = RotateDegrees.builder().horizontal(relaLLRotate.getHorizontal()  - 4).vertical(relaLLRotate.getVertical() + 10).build();
//        System.out.println("rotateByVectors resLL: " + relaLLRotate + " resRU:" + relaRURotate);
//        System.out.println();
//    }

}
