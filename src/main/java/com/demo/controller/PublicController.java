package com.demo.controller;

import com.demo.security.SecretProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class PublicController {


    @Autowired private SecretProvider secretProvider;

    @RequestMapping(value = "/public/1", method = GET)
    public void publicPath() throws IOException, URISyntaxException {
        System.out.println("-- public --");
        //		byte[] secretKey = secretKeyProvider.getKey();
        // We need a signing key, so we'll create one just for this example. Usually
// the key would be read from your application configuration instead.
        byte[] key = secretProvider.getKey();

        byte[] secretKey = secretProvider.getKey();
        Date expiration = Date.from(LocalDateTime.now(UTC).plusHours(2).toInstant(UTC));


        final String compactJws = Jwts.builder()
                .setSubject("john")
                .setExpiration(expiration)
//				.setIssuer(ISSUER)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        System.out.println("compactJws : "+ compactJws);

    }

}
