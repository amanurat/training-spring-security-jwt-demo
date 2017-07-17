package com.demo.service;

import com.demo.bean.LoginRequest;
import com.demo.security.SecretProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtService {

    @Autowired private SecretProvider secretProvider;

    public String tokenFor(LoginRequest loginRequest) {

        byte[] key = secretProvider.getKey();

        Date exp = Date.from(LocalDateTime.now(ZoneOffset.UTC).minusHours(1).toInstant(ZoneOffset.UTC));
        final String compact = Jwts.builder()
                .setExpiration(exp)
                .setSubject(loginRequest.getUsername())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        return compact;
    }

    public Jws<Claims> verify(String token) {
        return Jwts.parser().setSigningKey(secretProvider.getKey()).parseClaimsJws(token);

    }
}
