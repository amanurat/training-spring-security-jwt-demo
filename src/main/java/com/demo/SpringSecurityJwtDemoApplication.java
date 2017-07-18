package com.demo;

import com.demo.domain.User;
import com.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityJwtDemoApplication {

	@Autowired private UserRepository userRepository;

	@Bean
	public CommandLineRunner initCommandLineRunner() {
		return args -> {
			final User entity = new User("assanai", "manurat");
			userRepository.save(entity);

			final User userResult = userRepository.findByUsername("assanai");
			System.out.println(userResult.getUsername() + " "+ userResult.getPassword());
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtDemoApplication.class, args);
	}
}


