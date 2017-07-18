package com.demo.security;

import com.demo.bean.UserDetail;
import com.demo.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

        Jws<Claims> claimsJws = null;
        try {
            claimsJws = jwtService.verify(token);
            System.out.println(claimsJws);
            final String subject = claimsJws.getBody().getSubject();

            System.out.println("subject : "+ subject);
        } catch (ExpiredJwtException e ) {
			throw new ExpiredJwtException(claimsJws.getHeader(), claimsJws.getBody(), e.getMessage(), e);
		}
        catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT Token", e);
        }

//		final UserDetail userDetail = logInService.findLoginByToken(token);

//		final UserDetail userDetail = new UserDetail("john", "deo", token);

        final Claims body = claimsJws.getBody();
        final List<String> roles = (List<String>) body.get("roles");
        List<GrantedAuthority> authorities = roles.stream().map(v -> new SimpleGrantedAuthority(v)).collect(Collectors.toList());

        final UserDetail userDetail = new UserDetail(body.getSubject(), "", token, authorities);
//		userDetail.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_USER"));

		return new UsernamePasswordAuthenticationToken(userDetail, token, userDetail.getAuthorities());
	}


	@Override
	public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
