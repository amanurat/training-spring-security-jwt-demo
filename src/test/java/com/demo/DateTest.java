package com.demo;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

public class SimpleTest {


    @Test
    public void name() throws Exception {
        Date expiration = Date.from(LocalDateTime.now().toInstant(UTC));
        System.out.println(expiration);

        System.out.println(LocalDateTime.now());
    }
}
