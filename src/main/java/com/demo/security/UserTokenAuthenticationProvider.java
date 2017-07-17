package com.demo.security;

import com.demo.bean.UserDetail;
import com.demo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class UserTokenAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private JwtService jwtService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException("Token is required ");
		}

		final String token = (String)authentication.getCredentials();
		System.out.println("token is : "+ token);

        try {
            final Jws<Claims> claimsJws = jwtService.verify(token);
            System.out.println(claimsJws);
            final String subject = claimsJws.getBody().getSubject();

            System.out.println("subject : "+ subject);
        } catch (Exception e) {
            throw new BadCredentialsException("Failed to verify token", e);
        }

//		final UserDetail userDetail = logInService.findLoginByToken(token);

		final UserDetail userDetail = new UserDetail("john", "deo", token);
		userDetail.setAuthorities(AuthorityUtils.createAuthorityList("ADMIN", "USER"));

		return new UsernamePasswordAuthenticationToken(userDetail, token, userDetail.getAuthorities());
	}


	@Override
	public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
