package org.wlpiaoyi.framework.lab.space.threeD;

import lombok.Builder;
import lombok.Getter;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 三维坐标
 * </p>
 * <p><b>{@code @date:}</b>2025-09-10 10:42:19</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Builder
@Getter
public class Coordinate3D {

    /**
     * 原点
     */
    public static final Coordinate3D ORIGIN = Coordinate3D.builder().x(0).y(0).z(0).build();

    private double x;
    private double y;
    private double z;

    public Coordinate3D copy(){
        return Coordinate3D.builder()
                .x(this.x)
                .y(this.y)
                .z(this.z)
                .build();
    }
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 三维坐标减法
     * </p>
     *
     * <p><b>@param</b> <b>coordinate3D</b>
     * {@link Coordinate3D}
     * </p>
     *
     * <p><b>@param</b> <b>isAbs</b>
     * {@link boolean}
     * 是否取绝对值
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/9/10 10:46</p>
     * <p><b>{@code @return:}</b>{@link Coordinate3D}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Coordinate3D sub(Coordinate3D coordinate3D, boolean isAbs){
        if(isAbs){
            return Coordinate3D.builder()
                    .x(Math.abs(this.x - coordinate3D.x))
                    .y(Math.abs(this.y - coordinate3D.y))
                    .z(Math.abs(this.z - coordinate3D.z))
                    .build();
        }else{
            return Coordinate3D.builder()
                    .x(this.x - coordinate3D.x)
                    .y(this.y - coordinate3D.y)
                    .z(this.z - coordinate3D.z)
                    .build();
        }
    }


    /**
     * <p><b>{@code @description:}</b>
     * 向量的模（长度）两点之间的距离
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>coordinate</b>
     * {@link Coordinate3D}
     * 终点坐标
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/9/10 14:37</p>
     * <p><b>{@code @return:}</b>
     * {@link double}
     * 两点之间的距离
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public double magnitude(Coordinate3D coordinate) {
        Coordinate3D sub = coordinate.sub(this, false);
        return sub.magnitude();
    }
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }


    /**
     * <p><b>{@code @description:}</b>
     * 计算两个方向向量的点积（内积）
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>vector</b>
     * {@link Coordinate3D}
     * </p>
     * <p><b>{@code @date:}</b>2025/9/12 17:15</p>
     * <p><b>{@code @return:}</b>{@link double}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public double dotProduct(Coordinate3D vector) {
        return this.getX() * vector.getX() +
                this.getY() * vector.getY() +
                this.getZ() * vector.getZ();
    }

}
