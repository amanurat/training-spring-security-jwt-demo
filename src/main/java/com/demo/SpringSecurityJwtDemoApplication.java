package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@SpringBootApplication
public class SpringSecurityJwtDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtDemoApplication.class, args);
	}
}

@RestController
class TestController {

	@RequestMapping(value = "/public/1", method = GET)
	public void test() {
		System.out.println("-- public --");
	}

	@RequestMapping(value = "/private/1", method = GET)
	public void test2() {
		System.out.println("-- private --");
	}


}


@RestController
class ResourcesController {

	@RequestMapping(value = "/resources/1", method = GET)
	public void resources() {
		System.out.println("-- resources --");
	}

}


@Configuration
@EnableWebSecurity
class Config extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				// Spring Security should completely ignore URLs starting with /resources/
				.antMatchers("/resources/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*http.authorizeRequests().antMatchers("/public*//**").permitAll().anyRequest()
				.hasRole("USER").and()
				// Possibly more configuration ...
				.formLogin() // enable form based log in
				// set permitAll for all URLs associated with Form Login
				.permitAll();*/

		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		http.addFilterBefore(mdcFilter, BasicAuthenticationFilter.class);
		http.addFilterBefore(new TokenAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers("/public/**").permitAll()
//				.antMatchers(publicPaths()).permitAll()
				.and()
				.httpBasic();
	}
/*
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				// enable in memory based authentication with a user named "user" and "admin"
				.inMemoryAuthentication().withUser("user").password("password").roles("USER")
				.and().withUser("admin").password("password").roles("USER", "ADMIN");
	}

	*/


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
	}

	// Possibly more overridden methods ...
}

class TokenAuthenticationFilter extends OncePerRequestFilter {

	private AuthenticationManager authManager;

	public TokenAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		if(request.getMethod().equals(HttpMethod.OPTIONS.name())) {
			filterChain.doFilter(request, response);
			return;
		}

		String xAuth = request.getHeader("X-Authorization");

		System.out.println("xAuth : "+ xAuth);
		try {

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(null, xAuth);

			SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(auth));

			filterChain.doFilter(request, response);

		} catch (BadCredentialsException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}
}

@Component
class UserTokenAuthenticationProvider implements AuthenticationProvider {

//	private LogInService logInService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException("Token is required ");
		}

		final String token = (String)authentication.getCredentials();
//		final UserDetail userDetail = logInService.findLoginByToken(token);

		final UserDetail userDetail = new UserDetail("john", "deo", token);


		return new UsernamePasswordAuthenticationToken(userDetail, token, null);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == UsernamePasswordAuthenticationToken.class;
	}
}

class UserDetail {

	private final String john;
	private final String deo;
	private String username;
	private String password;
	private String token;

	public UserDetail(String john, String deo, String token) {

		this.john = john;
		this.deo = deo;
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

