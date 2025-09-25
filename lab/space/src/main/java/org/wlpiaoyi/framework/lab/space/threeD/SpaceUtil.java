package org.wlpiaoyi.framework.lab.space.threeD;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static org.wlpiaoyi.framework.lab.space.threeD.RotateDegrees.MIN_ABS_DOUBLE;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 空间信息
 * </p>
 * <p><b>{@code @date:}</b>2025-09-10 10:38:37</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class SpaceUtil {


    /**
     * <p><b>{@code @description:}</b>
     * 根据给定的起点和终点坐标，计算线段上某一点绕线段旋转指定角度后的新坐标。
     * 该方法主要用于在三维空间中模拟线段画圈的效果。
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>vector</b>
     * {@link Coordinate3D}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>degrees</b>
     * {@link double}
     * 旋转角度（以度为单位），正值为逆时针方向，负值为顺时针方向
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/9/11 18:16</p>
     * <p><b>{@code @return:}</b>{@link Coordinate3D}
     * 旋转后的新向量
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Coordinate3D vectorToCircle(Coordinate3D vector, double degrees) {
        degrees = ((int) degrees) % 360 + (degrees - (int) degrees);
//        Coordinate3D usePoint = Coordinate3D.builder()
//                .x(Math.abs(vector.getX())).y(Math.abs(vector.getY())).z(Math.abs(vector.getZ())).build();
        // 计算偏移点在水平面上的投影长度（X-Y平面上的投影）
        final double hShadowL = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getY(), 2));

        // 计算偏移点相对于X轴的原始角度（通过反余弦函数计算）
        double originDegrees = Math.toDegrees(Math.acos(vector.getX() / hShadowL));
        if (vector.getY() > 0) {

        } else if (vector.getY() < 0) {
            originDegrees = - originDegrees;
        }

        // 计算旋转后的总角度（原始角度加上指定的旋转角度）
        double relaDegrees = degrees + originDegrees;

        // 根据旋转后的角度计算新的X和Y坐标（在X-Y平面上的旋转）
        double x = Math.cos(Math.toRadians(relaDegrees)) * hShadowL;
        double y = Math.sin(Math.toRadians(relaDegrees)) * hShadowL;

        // 构建并返回新的坐标点（加上原始起点的坐标，并保持Z轴不变）
        return Coordinate3D.builder()
                .z(vector.getZ())
                .x(x)
                .y(y)
                .build();
    }



    /**
     * <p><b>{@code @description:}</b>
     * 旋转线段 p1-p2 绕 Z 轴旋转 a 度，再绕 Y 轴旋转 b 度，返回新的坐标 p3 和 p4
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>vector</b>
     * {@link Coordinate3D}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>diffRa</b>
     * {@link double}
     * 垂直面（XZ平面）的夹角（偏航角）
     * </p>
     * <p><b>{@code @date:}</b>2025/9/10 14:41</p>
     * <p>
     * <b>{@code @return:}</b>
     * {@link Coordinate3D}
     * 旋转后的新坐标
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Coordinate3D vectorToVRotate(Coordinate3D vector, double degrees) {
        return vectorToHRotate(Coordinate3D.builder().x(vector.getX()).y(vector.getZ()).z(vector.getY()).build(), degrees);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 旋转线段 p1-p2 绕 Z 轴旋转 a 度，再绕 Y 轴旋转 b 度，返回新的坐标 p3 和 p4
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>vector</b>
     * {@link Coordinate3D}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>diffRa</b>
     * {@link double}
     * 垂直转角度（度）- 与水平面（XY平面）的夹角
     * </p>
     * <p><b>{@code @date:}</b>2025/9/10 14:41</p>
     * <p>
     * <b>{@code @return:}</b>
     * {@link Coordinate3D}
     * 旋转后的新坐标
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Coordinate3D vectorToHRotate(Coordinate3D vector, double degrees) {
        degrees = ((((int) degrees) % 360 + 360) % 360) + (degrees - (int) degrees);
        Coordinate3D usePoint = Coordinate3D.builder()
                .x(Math.abs(vector.getX())).y(Math.abs(vector.getY())).z(Math.abs(vector.getZ())).build();
        final RotateDegrees originRa = SpaceBaseUtils.originDegrees(vector);
        final double relaDegrees = originRa.getHorizontal() + degrees;
        if(Math.abs(relaDegrees) > MIN_ABS_DOUBLE){
            final double distance = usePoint.magnitude();

            double dx = usePoint.getX();
            double dy = usePoint.getY();
            double dz;
            // 计算垂直方向上的投影长度
            final double hShadowL = Math.abs(Math.cos(Math.toRadians(relaDegrees)) * distance);
            if(Math.abs(dx) > MIN_ABS_DOUBLE && Math.abs(dy) > MIN_ABS_DOUBLE){
                final double xyV = dx / dy;
                dy = Math.sqrt(Math.pow(hShadowL, 2) / (xyV * xyV + 1));
                dx = xyV * dy;
            }else if(Math.abs(dx) > MIN_ABS_DOUBLE && !(Math.abs(dy) > MIN_ABS_DOUBLE)){
                dx = Math.abs(hShadowL);
                dy = 0;
            }else if(Math.abs(dy) > MIN_ABS_DOUBLE && !(Math.abs(dx) > MIN_ABS_DOUBLE)){
                dy = Math.abs(hShadowL);
                dx = 0;
            }else{
                throw new RuntimeException("area point are both 0");
            }
            dz = (distance * distance) - ((dx * dx) + (dy * dy));
            if((!(dz > MIN_ABS_DOUBLE))){
                dz = 0;
            }else {
                dz = Math.sqrt(dz);
            }
            switch (((int) relaDegrees / 90) % 4) {
                case 0:{}
                break;
                case 1: {
                    dx = dx * -1.d;
                    dy = dy * -1.d;
                }
                break;
                case 2: {
                    dx = dx * -1.d;
                    dy = dy * -1.d;
                    dz = dz * -1.d;
                }
                break;
                case 3: {
                    dz = dz * -1.d;
                }
                break;
            }
            usePoint = Coordinate3D.builder().x(dx).y(dy).z(dz).build();
        }
        double xd = vector.getX() > 0 ? 1.d : -1.d;
        double yd = vector.getY() > 0 ? 1.d : -1.d;
        double zd = vector.getZ() > 0 ? 1.d : -1.d;
        return Coordinate3D.builder()
                .x(usePoint.getX() * xd)
                .y(usePoint.getY() * yd)
                .z(usePoint.getZ() * zd).build();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 推算v1 到 v2 需要的旋转的垂直和水平角度
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
     * <p><b>{@code @date:}</b>2025/9/15 10:14</p>
     * <p><b>{@code @return:}</b>{@link RotateDegrees}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static RotateDegrees rotateByVectors(Coordinate3D vector1, Coordinate3D vector2) {
        Coordinate3D shadowVector1 = Coordinate3D.builder().x(vector1.getX()).y(vector1.getY()).z(0).build();
        Coordinate3D shadowVector2 = Coordinate3D.builder().x(vector2.getX()).y(vector2.getY()).z(0).build();
        double shadowP = Math.sqrt(Math.pow(vector1.getX(), 2) + Math.pow(vector1.getY(), 2)) / Math.sqrt(Math.pow(vector2.getX(), 2) + Math.pow(vector2.getY(), 2));
        double circleDegrees = SpaceBaseUtils.degreesByVectors(shadowVector1, shadowVector2) *
                ((vector1.getX() + vector1.getY()) > ((vector2.getX() + vector2.getY()) * shadowP) ? -1.d : 1.d);
        double hDegrees = SpaceBaseUtils.degreesByVectors(vector2, shadowVector2) - SpaceBaseUtils.degreesByVectors(vector1, shadowVector1);
        return RotateDegrees.builder().horizontal(hDegrees).vertical(circleDegrees).build();
    }


    public static Map<String, RotateDegrees> rotateByOSDI(Coordinate3D cameraVector, Coordinate3D dev1, Coordinate3D dev2, RotateDegrees diffRotate){
        Coordinate3D vector1 = Coordinate3D.builder().x(dev1.getX()).y(dev1.getY()).z(dev1.getZ()).build().sub(cameraVector, false);
        Coordinate3D vector2 = Coordinate3D.builder().x(dev2.getX()).y(dev2.getY()).z(dev2.getZ()).build().sub(cameraVector, false);
        RotateDegrees res = SpaceUtil.rotateByVectors(vector1, vector2);
        log.info("rotateByOSDI cameraVector: ({}, {}, {}) distance: {}", cameraVector.getX(), cameraVector.getY(), cameraVector.getZ(), cameraVector.magnitude());
        log.info("rotateByOSDI vector2: ({}, {}, {}) distance: {}", vector1.getX(), vector1.getY(), vector1.getZ(), vector1.magnitude());
        log.info("rotateByOSDI vector3: ({}, {}, {}) distance: {}", vector2.getX(), vector2.getY(), vector2.getZ(), vector2.magnitude());
        log.info("rotateByOSDI res: ({}, {})", res.getHorizontal(), res.getVertical());
//        RotateDegrees diffRotate = RotateDegrees.builder().horizontal(9).vertical(0).build();
        RotateDegrees relaLLRotate = RotateDegrees.builder().horizontal(res.getHorizontal() + diffRotate.getHorizontal()).vertical((diffRotate.getVertical() - res.getVertical() + 360) % 360).build();
        RotateDegrees relaRURotate = RotateDegrees.builder().horizontal(relaLLRotate.getHorizontal()  - 4).vertical(relaLLRotate.getVertical() + 10).build();
        log.info("rotateByOSDI rotateByVectors resLL: {} resRU:{}", relaLLRotate, relaRURotate);
        return new HashMap<>(){{
            put("lowerLeftCoordinate", relaLLRotate);
            put("upperRightCoordinate", relaRURotate);
        }};
    }

}
