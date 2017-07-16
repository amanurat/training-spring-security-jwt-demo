package com.demo.security;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class SecretProvider {

	public byte[] getKey()  {
		try {
			return Files.readAllBytes(Paths.get(this.getClass().getResource("/secret.key").toURI()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
