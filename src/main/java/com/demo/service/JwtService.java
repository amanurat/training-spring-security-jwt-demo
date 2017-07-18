package com.demo.service;

import com.demo.domain.User;
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
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtService {

    @Autowired private SecretProvider secretProvider;

    public String tokenFor(User user) {

        byte[] key = secretProvider.getKey();

        Date exp = Date.from(LocalDateTime.now(ZoneOffset.UTC).plusHours(10).toInstant(ZoneOffset.UTC));
        List<String> roles = user.getAuthorities().stream().map(v -> v.getAuthority()).collect(Collectors.toList());
//        map.put("roles", roles);
        final String compact = Jwts.builder()
                .setExpiration(exp)
                .setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS512, key)
                .claim("roles", roles)
                .compact();

        return compact;
    }

    public Jws<Claims> verify(String token) {
        return Jwts.parser().setSigningKey(secretProvider.getKey()).parseClaimsJws(token);

    }
}
