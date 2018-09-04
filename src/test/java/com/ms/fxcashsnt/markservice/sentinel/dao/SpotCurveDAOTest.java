package com.ms.fxcashsnt.markservice.sentinel.dao;

import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotCurve;
import com.ms.fxcashsnt.markservice.sentinel.model.spot.SpotPoint;
import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.List;
import java.util.Map;
/**
 * user: Carl,Wu
 * date: 7/20/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SpotCurveDAOTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SpotCurveDAO spotCurveDAO;
    @Autowired
    private MarkCurveDownloader markCurveDownloader;
    @Test
    public void testQuery(){
        String currencyPair = "USD_RSD";
        String context = "LNFRM";
        Instant startTimestamp = Instant.parse("2018-07-20T00:00:00Z");
        Instant endTimestamp = Instant.parse("2018-07-27T23:37:14.922Z");
        markCurveDownloader.downloadEODMarkCurve();
        SpotCurve spotCurve=spotCurveDAO.query(currencyPair,context,startTimestamp,endTimestamp);

        System.out.println("CurrencyPair is "+spotCurve.getCurrencyPair()+" context is "+spotCurve.getContext());
        for(SpotPoint spotPoint:spotCurve.getSpotPointList()){
            System.out.println(spotPoint.getPositionDate()+","+spotPoint.getSpotDate()+","+spotPoint.getSpotRate()+","+spotPoint.getTimestamp());
        }
    }

    @Test
    public void testQueryForList(){
        Instant startTimestamp = Instant.parse("2018-07-25T00:00:00.922Z");
        Instant endTimestamp = Instant.parse("2018-07-25T23:37:14.922Z");
        List<SpotCurve> spotCurveList=spotCurveDAO.queryForList(startTimestamp,endTimestamp);
        for(SpotCurve spotCurve:spotCurveList){
            System.out.println("CurrencyPair is "+spotCurve.getCurrencyPair()+" context is "+spotCurve.getContext());
            for(SpotPoint spotPoint:spotCurve.getSpotPointList()){
                System.out.println(spotPoint.getPositionDate()+","+spotPoint.getSpotDate()+","+spotPoint.getSpotRate()+","+spotPoint.getTimestamp());
            }
        }
    }

    @Test
    public void testQueryUniqueCurrencyPairAndContext(){
        List<String> list= spotCurveDAO.queryUniqueCurrencyPair();
    }

    @Test
    public void validateDataTest() {
//        markCurveDownloader.writeResponseListIntoDatabase(markCurveDownloader.downloadMarkCurve(MarkServiceConstants.EndContextList,Collections.singletonList(LocalDate.now())));
        String sql = "SELECT CurrencyPair,count (*) FROM SpotTable GROUP BY CurrencyPair";
        List result = jdbcTemplate.queryForList(sql);
        result.forEach(System.out::println);
    }
}
