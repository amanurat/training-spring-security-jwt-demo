package com.demo.security;

import com.demo.bean.UserDetail;
import com.demo.service.JwtService;
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
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
class UserTokenAuthenticationProvider implements AuthenticationProvider {

//	private LogInService logInService;

	@Autowired
	private SecretProvider secretProvider;

	@Autowired
	private JwtService jwtService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException("Token is required ");
		}

		final String token = (String)authentication.getCredentials();
		System.out.println("token is : "+ token);

        final Jws<Claims> claimsJws = jwtService.verify(token);

        System.out.println(claimsJws);
        final String subject = claimsJws.getBody().getSubject();

        System.out.println("subject : "+ subject);

//		final UserDetail userDetail = logInService.findLoginByToken(token);

		final UserDetail userDetail = new UserDetail("john", "deo", token);
		userDetail.setAuthorities(AuthorityUtils.createAuthorityList("ADMIN", "USER"));

		return new UsernamePasswordAuthenticationToken(userDetail, token, userDetail.getAuthorities());
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
