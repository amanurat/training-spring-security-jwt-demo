package com.demo;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

public class DateTest {


    @Test
    public void convertLocalDateTimeToDate() throws Exception {
        Date expiration = Date.from(LocalDateTime.now(UTC).toInstant(UTC));
        System.out.println("Date : " + expiration);
        System.out.println("LocalDateTime : " + LocalDateTime.now());

    }
    @Test
    public void convertLocalDateTimeToDateAndPlusHour() throws Exception {
        Date expiration = Date.from(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC));
        System.out.println("Date : " + expiration);
        System.out.println("LocalDateTime : " + LocalDateTime.now());
    }

}
