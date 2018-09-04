package com.ms.fxcashsnt.markservice.sentinel;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import com.ms.fxcashsnt.markservice.sentinel.util.Utility;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static com.ms.fxcashsnt.markservice.sentinel.util.Utility.allElementsTheSame;

/**
 * user: yandong.liu
 * date: 7/23/2018
 */
public class UtilityTest {
    @Test
    public void instantLinspaceTest() {
        Instant now = Instant.now();
        Instant minute = now.plusSeconds(60);
        List<Instant> instantList = Utility.instantLinspace(now, minute, 3);
        System.out.println(instantList);
        Assert.assertEquals(instantList.size(), 3);
        Assert.assertEquals(now, instantList.get(0));
    }

    @Test
    public void allElementsTheSameTest(){
        List<Double>list=new LinkedList<>();
        list.add(0.89);
        list.add(0.89);
        list.add(0.89);
        list.add(0.89);
        Assert.assertTrue(allElementsTheSame(list));
//        System.out.print(allElementsTheSame(list));
    }

    @Test
    public void covTest() {
        List<Point> pointList = new LinkedList<>();
        pointList.add(new Point(1, null));
        pointList.add(new Point(1.001, null));
        pointList.add(new Point(1.003, null));
        double cov = Utility.coefficientOfVariationOfPointList(pointList);
        System.out.println(cov);
    }

}
