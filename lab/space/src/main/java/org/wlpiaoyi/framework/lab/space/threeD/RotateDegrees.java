package org.wlpiaoyi.framework.lab.space.threeD;

import lombok.Builder;
import lombok.Getter;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 旋转角度
 * </p>
 * <p><b>{@code @date:}</b>2025-09-10 14:52:10</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Getter
@Builder
public class RotateDegrees {

    public static final double MIN_ABS_DOUBLE = 0.0000000001d;

    //水平面（XY平面）的夹角
    @Builder.Default
    private double horizontal = 0;
    //垂直面（XZ平面）的夹角（偏航角）
    @Builder.Default
    private double vertical = 0;

    @Override
    public String toString() {
        return "(h:" + horizontal + ", v:" + vertical + ")";
    }

    public RotateDegrees sub(RotateDegrees rotateDegrees, boolean isAbs){
        return RotateDegrees.builder()
                .horizontal(
                        isAbs ? Math.abs(horizontal - rotateDegrees.horizontal) : horizontal - rotateDegrees.horizontal
                )
                .vertical(
                        isAbs ? Math.abs(vertical - rotateDegrees.vertical) : vertical - rotateDegrees.vertical
                )
                .build();
    }

    public RotateDegrees plus(RotateDegrees rotateDegrees){
        return RotateDegrees.builder()
                .horizontal(horizontal + rotateDegrees.horizontal)
                .vertical(vertical + rotateDegrees.vertical)
                .build();
    }
}
