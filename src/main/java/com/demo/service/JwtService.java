package com.demo.service;

import com.demo.bean.LoginRequest;
import com.demo.security.SecretProvider;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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

    public Jws<Claims> verify(String token) {
        try {
            return Jwts.parser().setSigningKey(secretProvider.getKey()).parseClaimsJws(token);
            //OK, we can trust this JWT
        } catch (SignatureException e) {

            //don't trust the JWT!
            throw new BadCredentialsException("cannot parse jwt ");
        }

    }
}
