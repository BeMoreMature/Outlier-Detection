package com.ms.fxcashsnt.markservice.sentinel.strategy;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;

import java.util.List;

public interface Strategy {
    Strategy fit(List<Point> pointList);
    List<Boolean> predict(List<Point> pointList);
}
