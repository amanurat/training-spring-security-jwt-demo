package com.demo.service;

import com.demo.bean.LoginRequest;
import com.demo.security.SecretProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    @Autowired private SecretProvider secretProvider;

    public String tokenFor(LoginRequest loginRequest) {

        byte[] key = secretProvider.getKey();

        final String compact = Jwts.builder()
                .setSubject(loginRequest.getUsername())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        return compact;
    }
}
