package com.ms.fxcashsnt.markservice.sentinel.dao;

import com.ms.fxcashsnt.markservice.sentinel.util.SaveCSVToDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedList;
import java.util.List;

//@ContextConfiguration(locations = {"classpath:spring-core.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
public class SaveCSVToDatabaseTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SaveCSVToDatabase saveCSVToDatabase;
//    @Test
    public void testReadLine(){
        String [] regionList = {"hk", "ln", "ny", "tk"};
        String [] timeList = {"2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018"};
        for(int i = 0; i < regionList.length; i++){
            for(int j = 0; j < timeList.length; j++){
                saveCSVToDatabase.save("C:\\Users\\carwu\\Documents\\curve_2011-2018\\curves\\"+regionList[i]+"_"+timeList[j]+".csv",regionList[i].toUpperCase()+"FRM");
                System.out.println(regionList[i]+" "+timeList[j]);

            }
        }
    }
//    @Test
    public void showAllRecords(){
        String sql = "SELECT * FROM FwdPointTable WHERE region = 'HK' AND currencyPair = 'AUD' AND tenor = '58D'";

        List result = jdbcTemplate.queryForList(sql);
        result.forEach(System.out::println);
        System.out.println(result.size());
    }
}
