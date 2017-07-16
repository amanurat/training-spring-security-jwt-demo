package com.demo.security;

import com.demo.bean.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
class UserTokenAuthenticationProvider implements AuthenticationProvider {

//	private LogInService logInService;

	@Autowired
	private SecretProvider secretProvider;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException("Token is required ");
		}

		final String token = (String)authentication.getCredentials();
		System.out.println("token is : "+ token);

		try {
			final Jws<Claims> claimsJws = claimsJwt(token);

			System.out.println(claimsJws);
			final String subject = claimsJws.getBody().getSubject();

			System.out.println("subject : "+ subject);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}


//		final UserDetail userDetail = logInService.findLoginByToken(token);

		final UserDetail userDetail = new UserDetail("john", "deo", token);

		return new UsernamePasswordAuthenticationToken(userDetail, token, null);
	}

	private Jws<Claims> claimsJwt(String token) throws IOException, URISyntaxException {

		try {


			return Jwts.parser().setSigningKey(secretProvider.getKey()).parseClaimsJws(token);

			//OK, we can trust this JWT

		} catch (SignatureException e) {

			//don't trust the JWT!
			throw new BadCredentialsException("cannot parse jwt ");
		}

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == UsernamePasswordAuthenticationToken.class;
	}
}
