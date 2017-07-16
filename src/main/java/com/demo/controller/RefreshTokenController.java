package com.demo.controller;

import com.demo.security.SecretProvider;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static java.time.ZoneOffset.UTC;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
class RefreshTokenController {

	void generateKey() throws IOException {
		Key key = MacProvider.generateKey();
		System.out.println("key : "+ key);
		Files.write(Paths.get("xxx.key"), key.getEncoded());
	}

	@Autowired
    private SecretProvider secretProvider;

	@RequestMapping(value = "/refresh/1", method = GET)
	public void refreshToken() throws IOException, URISyntaxException {
		//refresh token server must return error then client will call server for refresh token
		//client sent access token again

		byte[] secretKey = secretProvider.getKey();
		Date expiration = Date.from(LocalDateTime.now(UTC).plusHours(2).toInstant(UTC));


		final JwtBuilder builder = Jwts.builder();

		final String compactJws = builder
				.setSubject("john")
				.setExpiration(expiration)
				.setIssuer("john")
				.setAudience("audience1")
				.claim("roles", Arrays.asList("USER", "ADMIN"))
				.signWith(SignatureAlgorithm.HS512, secretKey)

				.compact();
		System.out.println("refresh token compactJws : "+ compactJws);
	}






}
