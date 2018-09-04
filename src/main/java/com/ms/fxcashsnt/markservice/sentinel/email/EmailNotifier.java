package com.ms.fxcashsnt.markservice.sentinel.mail;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.Writer;
import java.util.Date;

/**
 * user: Carl,Wu
 * date: 8/3/2018
 * this class is to send the email
 */
public class EmailNotifier {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotifier.class);

    private String toAddress;

    private static final String FROM = "daily-detect";
    private static final String SUBJECT_PATTERN = "cross_region_report_report - %s/%s";
    private static final String BODY = "Attached is a report of the outliers cross region";

    public EmailNotifier(String toAddress) {
        this.toAddress = toAddress;
    }

    // use SMTP
    public void sendEmail(Session session, Region region, LocalDate date, Writer out){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

//            msg.setFrom(new InternetAddress("no_reply@example.com"));

//            msg.setReplyTo(InternetAddress.parse("Carl.Wu@morganstanley.com", false));

            msg.setSubject(getSubject(region, date), "UTF-8");

            msg.setContent(out.toString(), "text/html");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get region and date
     */
    private String getSubject(Region region, LocalDate date) {
        return String.format(SUBJECT_PATTERN, region.name().toUpperCase(), date.toString());
    }

}
