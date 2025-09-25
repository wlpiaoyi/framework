package org.wlpiaoyi.framework.lab.space;

import org.wlpiaoyi.framework.lab.space.threeD.Coordinate3D;
import org.wlpiaoyi.framework.lab.space.threeD.RotateDegrees;
import org.wlpiaoyi.framework.lab.space.threeD.SpaceUtil;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import java.util.logging.Logger;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-08-27 10:14:43</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class AppRun {

    private static final Logger LOGGER = Logger.getLogger(AppRun.class.getName());

    public static void main(String[] args) {
        String string = DataUtils.readFile(DataUtils.USER_DIR + "/config/mqtt/1.txt");
        String strs[] = string.split("\\|");
        Double[] vs0 = ValueUtils.toDoubleArray(strs[0]);
        Double[] vs1 = ValueUtils.toDoubleArray(strs[1]);
        Coordinate3D vector1 = Coordinate3D.builder().x(vs0[0]).y(vs0[1]).z(vs0[2]).build();
        Coordinate3D vector2 = Coordinate3D.builder().x(vs1[0]).y(vs1[1]).z(vs1[2]).build();
        RotateDegrees res = SpaceUtil.rotateByVectors(vector1, vector2);
        System.out.println("rotateByVectors vector1: (" + vector1.getX() + ", " + vector1.getY() + ", " + vector1.getZ() + ") distance: " + vector1.magnitude());
        System.out.println("rotateByVectors vector2: (" + vector2.getX() + ", " + vector2.getY() + ", " + vector2.getZ() + ") distance: " + vector2.magnitude());
        System.out.println("rotateByVectors res: (" + res.getHorizontal() + ", " + res.getVertical() + ")");
        System.out.println();
    }
}