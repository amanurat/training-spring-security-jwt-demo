package com.demo.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private AuthenticationManager authManager;

	public TokenAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

/*
		if(request.getMethod().equals(HttpMethod.OPTIONS.name())) {
			filterChain.doFilter(request, response);
			return;
		}
*/
		String xAuth = request.getHeader("Authorization");

		System.out.println("xAuth : "+ xAuth);

		String token = xAuth.replaceAll("Bearer", "");

		try {

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(null, token);

			SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(auth));

			filterChain.doFilter(request, response);

		} catch (BadCredentialsException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}
}
