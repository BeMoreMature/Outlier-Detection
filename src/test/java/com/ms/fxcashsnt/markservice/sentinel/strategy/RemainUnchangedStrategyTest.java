package com.ms.fxcashsnt.markservice.sentinel.strategy;

import com.ms.fxcashsnt.markservice.sentinel.model.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RemainUnchangedStrategyTest {

    @Test
    public void testRemainUnchangedStrategy(){
            RemainUnchangedStrategy remainUnchangedStrategy = new RemainUnchangedStrategy();
            List<Point> list = new LinkedList<>();
            list.add(new Point(1.0, Instant.now()));
            list.add(new Point(2.0, Instant.now()));
            list.add(new Point(1.1, Instant.now()));
            list.add(new Point(1.1, Instant.now()));
            list.add(new Point(1.0, Instant.now()));
            remainUnchangedStrategy.fit(list);
    }
}
