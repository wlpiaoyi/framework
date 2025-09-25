package org.wlpiaoyi.framework.lab.space.threeD;

import lombok.Builder;
import lombok.Getter;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-09-12 10:13:16</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Getter
@Builder
public class Line3D {

    //线段起点
    private final Coordinate3D startPoint;
    //线段终点
    private final Coordinate3D endPoint;

    //向量（终点相对于起点的偏移量）
    private Coordinate3D vector;
    //水平面（XY平面）的夹角
    @Builder.Default
    private double horizontalShadowLen = Double.MIN_VALUE;
    //垂直面（XZ平面）的夹角（偏航角）
    @Builder.Default
    private double verticalShadowLen = Double.MIN_VALUE;


    @Override
    public String toString() {
        return String.format("p1:%s p2:%s", startPoint, endPoint);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 检查向量
     * </p>
     *
     * <p><b>{@code @param:}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/9/12 10:26</p>
     * <p><b>{@code @return:}</b>{@link Line3D}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Line3D checkVector(){
        if(this.vector != null){
            return this;
        }
        if(this.startPoint == null){
            throw new IllegalArgumentException("startPint is null");
        }
        if(this.endPoint == null){
            throw new IllegalArgumentException("endPoint is null");
        }
        // 计算坐标差（终点相对于起点的偏移量）
        this.vector = this.endPoint.sub(this.startPoint, false);
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 检查投影
     * </p>
     *
     * <p><b>{@code @param:}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/9/12 10:27</p>
     * <p><b>{@code @return:}</b>{@link Line3D}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Line3D checkShadow(){
        if(this.vector == null){
            this.checkVector();
        }
        if(this.vector == null){
            throw new IllegalArgumentException("diffPoint is null");
        }
        if(this.horizontalShadowLen > Double.MIN_VALUE || this.verticalShadowLen > Double.MIN_VALUE){
            return this;
        }
        // 计算偏移点相对于原点的水平旋转角度（方位角）
        final RotateDegrees originRa = SpaceBaseUtils.originDegrees(vector);

        // 计算偏移点与原点的距离（即线段的长度）
        final double distance = vector.magnitude();

        // 计算偏移点在水平面上的投影长度（X-Y平面上的投影）
        this.horizontalShadowLen = Math.abs(Math.cos(Math.toRadians(originRa.getHorizontal())) * distance);

        // 计算偏移点在垂直面上的投影长度（X-Z平面上的投影）
        this.verticalShadowLen = Math.abs(Math.cos(Math.toRadians(originRa.getVertical())) * distance);

        return this;
    }

//    public static void main(String[] args) {
//        // 测试用例1：题目中的例子
//        Line3D line1 = Line3D.builder()
//                .startPoint(Coordinate3D.builder().x(0).y(0).z(0).build())
//                .endPoint(Coordinate3D.builder().x(4).y(1).z(5).build())
//                .build();
//        Line3D line2 = Line3D.builder()
//                .startPoint(Coordinate3D.builder().x(0).y(0).z(0).build())
//                .endPoint(Coordinate3D.builder().x(1).y(3.5).z(4).build())
//                .build();
//        System.out.println("===== 测试用例1 =====");
//        System.out.printf("两条直线(%s, %s)的夹角: %.2f°%n", line1, line2, line1.angleByLine(line2));
//
//
//        // 测试用例2：题目中的例子
//        Line3D line3 = Line3D.builder()
//                .startPoint(Coordinate3D.builder().x(0).y(0).z(0).build())
//                .endPoint(Coordinate3D.builder().x(4).y(1).z(0).build())
//                .build();
//        Line3D line4 = Line3D.builder()
//                .startPoint(Coordinate3D.builder().x(0).y(0).z(0).build())
//                .endPoint(Coordinate3D.builder().x(1).y(3.5).z(0).build())
//                .build();
//        System.out.println("===== 测试用例2 =====");
//        System.out.printf("两条直线(%s, %s)的夹角: %.2f°%n", line3, line4, line4.angleByLine(line3));
//    }
}
