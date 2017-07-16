package com.demo;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

public class JwtTest {

    @Test
    public void generateToken() throws Exception {
        Date expiration = Date.from(LocalDateTime.now(UTC).plusHours(2).toInstant(UTC));
        final String result = Jwts.builder()
                .setSubject("admin")
                .setExpiration(expiration)
                .setIssuer("in.sdqali.jwt")
                .signWith(SignatureAlgorithm.HS512, "secretKey")
                .compact();

        System.out.println(result);



        //to verify  ถ้า parse token แล้วเกิด exception แสดงว่า ​​token invalid
        Jws<Claims> claims = Jwts.parser().setSigningKey("secretKey").parseClaimsJws("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTUwMDE5NDA4NCwiaXNzIjoiaW4uc2RxYWxpLmp3dCJ9.GDqdcJgEPN3qk7Dsw-ObQQIDl4iS2A2-qzzeol0K6VFM8k_7Xp3KpCd3HHZSrzR8nX_NLLE2czeVfoFQ6gE78w\n\n");
        final JwsHeader header = claims.getHeader();
        final Claims body = claims.getBody();
        final String subject = body.getSubject().toString();
        System.out.println(header);
        System.out.println(subject);

    }
}
