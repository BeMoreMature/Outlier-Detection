package com.ms.fxcashsnt.markservice.sentinel;

        import com.ms.fxcashsnt.markservice.sentinel.util.MarkCurveDownloader;
        import com.ms.fxcashsnt.markservice.sentinel.util.MarkServiceConstants;
        import msjava.hdom.messaging.HDOMRequestSender;
        import org.junit.After;
        import org.junit.Assert;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.jdbc.core.JdbcTemplate;
        import org.springframework.test.context.ContextConfiguration;
        import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

        import java.io.File;
        import java.time.LocalDate;
        import java.util.Collections;


/**
 * user: yandong.liu
 * date: 7/16/2018
 */
@ContextConfiguration(locations = {"classpath:spring-core-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class MarkCurveDownloaderTest {
    private HDOMRequestSender soapRequestSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkCurveDownloaderTest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public MarkCurveDownloader markCurveDownloader;

    @Test
    public void downloaderTest() {
        markCurveDownloader.writeResponseListIntoDatabase(markCurveDownloader.downloadMarkCurve(MarkServiceConstants.IntraContextList,Collections.singletonList(LocalDate.now())));
    }

    @After
    public void removeSqliteTestDatabaseFile() {
        File sqliteDatabaseFile = new File("mark_history_test.sqlite");
        sqliteDatabaseFile.deleteOnExit();
    }
}
