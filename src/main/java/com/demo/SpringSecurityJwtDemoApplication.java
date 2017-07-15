package com.demo;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static java.time.ZoneOffset.UTC;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@SpringBootApplication
public class SpringSecurityJwtDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtDemoApplication.class, args);
	}
}

@RestController
class TestController {

	void generateKey() throws IOException {
		Key key = MacProvider.generateKey();
		System.out.println("key : "+ key);
		Files.write(Paths.get("xxx.key"), key.getEncoded());
	}

	@Autowired private SecretProvider secretProvider;

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


	@RequestMapping(value = "/public/1", method = GET)
	public void test() throws IOException, URISyntaxException {
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
				.antMatchers("/resources/**", "/public/**", "/refresh/**");
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
				.antMatchers("/login").permitAll()
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

@Component
class SecretProvider {

	public byte[] getKey() throws URISyntaxException, IOException {
		return Files.readAllBytes(Paths.get(this.getClass().getResource("/secret.key").toURI()));
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

