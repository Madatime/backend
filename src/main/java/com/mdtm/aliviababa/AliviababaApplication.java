package com.mdtm.aliviababa;

import javax.crypto.SecretKey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@SpringBootApplication
public class AliviababaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AliviababaApplication.class, args);

		SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
		String secret = Encoders.BASE64.encode(key.getEncoded());

		System.out.println("Clave Secreta = " + secret);

	}

}
