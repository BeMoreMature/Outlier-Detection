package com.ms.fxcashsnt.markservice.sentinel;

import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardDataSet;
import com.ms.fxcashsnt.markservice.sentinel.model.forward.ForwardDataSetBuilder;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotDataSet;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotDataSetBuilder;
import com.ms.fxcashsnt.markservice.sentinel.util.DatasetToCSV;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * user: yandongl
 * date: 7/30/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CreateDataSetTest {

    @Autowired
    SpotDataSetBuilder spotDataSetBuilder;

    @Autowired
    ForwardDataSetBuilder forwardDataSetBuilder;

    @Test
    public void createSpotDataSetTest() {
        SpotDataSet spotDataSet = spotDataSetBuilder
                .setCurrencyPair("UBD")
                .setContext("HK")
                .setTrainStartTimestamp(Instant.parse("2011-01-01T00:00:00Z"))
                .setTrainEndTimestamp(Instant.parse("2018-12-31T00:00:00Z"))
                .setTestStartTimestamp(Instant.parse("2017-01-01T00:00:00Z"))
                .setTestEndTimestamp(Instant.parse("2018-12-31T00:00:00Z")).build();
        System.out.println(spotDataSet.getTestPointList());
    }

    @Test
    public void createForwardDataSetTest() {
        ForwardDataSet forwardDataSet = forwardDataSetBuilder
                .setCurrencyPair("USD_RSD")
                .setContext("ASIA")
                .setTrainStartTimestamp(Instant.now())
                .setTrainEndTimestamp(Instant.now())
                .setTestStartTimestamp(Instant.now().minus(10, ChronoUnit.DAYS))
                .setTestEndTimestamp(Instant.now()).build();
        System.out.println(forwardDataSet.getTestPointListMap());
    }

    @Test
    public void toJsonTest() {
        SpotDataSet spotDataSet = spotDataSetBuilder
                .setCurrencyPair("USD_IQD")
                .setContext("LNFRM")
                .setTrainStartTimestamp(Instant.parse("2011-01-01T00:00:00Z"))
                .setTrainEndTimestamp(Instant.parse("2018-12-31T00:00:00Z"))
                .setTestStartTimestamp(Instant.parse("2017-01-01T00:00:00Z"))
                .setTestEndTimestamp(Instant.parse("2018-12-31T00:00:00Z")).build();
        System.out.println(spotDataSet.getTrainPointList());

        DatasetToCSV datasetToCSV = new DatasetToCSV();
        datasetToCSV.writeToCSV(spotDataSet);
    }
}
