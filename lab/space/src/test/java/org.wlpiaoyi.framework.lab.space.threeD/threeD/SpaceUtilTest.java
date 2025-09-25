package org.wlpiaoyi.framework.lab.space.threeD.threeD;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.lab.space.threeD.Coordinate3D;
import org.wlpiaoyi.framework.lab.space.threeD.RotateDegrees;
import org.wlpiaoyi.framework.lab.space.threeD.SpaceUtil;

import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-09-15 09:18:20</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class SpaceUtilTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void lineToRotate() throws Exception {
        Coordinate3D vector1 = Coordinate3D.builder().x(4).y(-4).z(5).build();
        double rotateDegrees = 80;
        Coordinate3D lineToRotateRes = SpaceUtil.vectorToHRotate(vector1, rotateDegrees);
        System.out.println("lineToRotate vector1: (" + vector1.getX() + ", " + vector1.getY() + ", " + vector1.getZ() + ")");
        System.out.println("lineToRotate lineToRotateRes: (" + lineToRotateRes.getX() + ", " + lineToRotateRes.getY() + ", " + lineToRotateRes.getZ() + ")");
        System.out.println("lineToRotate rotateAngle: " + rotateDegrees + " distance1: " + vector1.magnitude() + " distance2: " + lineToRotateRes.magnitude());
    }

    @Test
    public void vectorToCircle() throws Exception {
        Coordinate3D vector1 = Coordinate3D.builder().x(-3).y(-3.99).z(5).build();
        double rotateDegrees = 180;
        Coordinate3D lineToRotateRes = SpaceUtil.vectorToCircle(vector1, rotateDegrees);
        System.out.println("lineToCircle vector1: (" + vector1.getX() + ", " + vector1.getY() + ", " + vector1.getZ() + ")");
        System.out.println("lineToCircle lineToRotateRes: (" + lineToRotateRes.getX() + ", " + lineToRotateRes.getY() + ", " + lineToRotateRes.getZ() + ")");
        System.out.println("lineToCircle rotateAngle: " + rotateDegrees + " distance1: " + vector1.magnitude() + " distance2: " + lineToRotateRes.magnitude());
    }


    @Test
    public void rotateByVectors() throws Exception {
        Coordinate3D cameraVector = Coordinate3D.builder().x(2.6).y(6.0).z(1.06).build();
        Coordinate3D vector1 = Coordinate3D.builder().x(7.17).y(0).z(2.3).build();
        Coordinate3D vector2 = Coordinate3D.builder().x(7.17).y(5.95).z(2.4).build();
        RotateDegrees diffRotate = RotateDegrees.builder().horizontal(9).vertical(0).build();
        Map<String, RotateDegrees> map = SpaceUtil.rotateByOSDI(cameraVector, vector1, vector2, diffRotate);
        RotateDegrees relaLLRotate = map.get("lowerLeftCoordinate");
        RotateDegrees relaRURotate = map.get("upperRightCoordinate");
//        RotateDegrees relaRURotate = RotateDegrees.builder().horizontal(relaLLRotate.getHorizontal()  - 4).vertical(relaLLRotate.getVertical() + 10).build();
        System.out.println("rotateByVectors resLL: " + relaLLRotate + " resRU:" + relaRURotate);
        System.out.println();
    }



    @After
    public void tearDown() throws Exception {

    }

}
