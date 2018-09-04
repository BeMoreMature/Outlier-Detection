
package com.ms.fxcashsnt.markservice.sentinel.util;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Utility {
    public static String toUpperCase(final String input) {
        return input.toUpperCase();
    }

    public static boolean isBeforeBasedOnDate(Instant instant, Instant compareTo) {
        return instant.truncatedTo(ChronoUnit.DAYS)
                .isBefore(compareTo.truncatedTo(ChronoUnit.DAYS));
    }

    public static boolean equals(double a, double b, double eps) {
        if (a == b) return true;
        return Math.abs(a - b) < eps;
    }

    public static List<Instant> instantLinspace(Instant start, Instant end, int num) {
        if (num <= 1) return Collections.singletonList(start);
        Duration duration = Duration.between(start, end);
        duration = duration.dividedBy(num - 1);

        List<Instant> instantList = new LinkedList<>();
        for (int i = 0; i < num; i++) {
            instantList.add(start);
            start = start.plus(duration);
        }
        return instantList;
    }

    public static List<Point> removeDuplicatedAccordingToTimestamp(List<Point> pointList) {
        Set<Instant> seen = new HashSet<>();
        pointList.removeIf(p -> !seen.add(p.getTimestamp()));
        return pointList;
    }

    // find if all elements in list keep constant
    public static Boolean allElementsTheSame(List<Double> templist){
        boolean flag=true;
        Double firstElement=templist.get(0);
        for(Double ele:templist){
            if(!equals(ele,firstElement,1e-5)) flag=false;
        }
        return flag;
    }

    // find all kinds of pairs in the list
    public static ArrayList<List> findAllPairsInList(List list){
        ArrayList<List> res=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            for(int j=i+1;j<list.size();j++){
                res.add(Arrays.asList(list.get(i),list.get(j)));
            }
        }
        return res;
    }

    // compare if two number change rate larger than certain percent
    public static boolean changeInRange(double x,double y,double percent){
        boolean flag = true;
        if(x == 0.0 || y== 0.0) return flag;
        if (y < x) {
            double temp = y;
            y = x;
            x = temp;
        }
        if((y-x) > percent * Math.abs(x)){
            flag = false;
        }
        return flag;
    }

    // get median in list
    public static double getMedian(List<Double> list) {
        if (list.size() % 2 == 0)
            return (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
        else
            return list.get(list.size() / 2);
    }

    public static double coefficientOfVariationOfPointList(List<Point> pointList) {
        if (pointList == null || pointList.size() < 1) {
            return 0;
        }
        int n = pointList.size();
        double mean = pointList.stream().map(Point::getValue).mapToDouble(Double::doubleValue).sum() / n;
        double var = 0;
        for (int i = 0; i < n; i++) {
            var += (pointList.get(i).getValue() - mean) * (pointList.get(i).getValue() - mean);
        }
        var /= n;
        double std = Math.sqrt(var);
        return std / mean;
    }
}
