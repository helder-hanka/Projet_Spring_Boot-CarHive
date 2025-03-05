package com.ProjetVde.CarHive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class CarHiveApplication {
//	public static String generateSecretKey() {
//		SecureRandom secureRandom = new SecureRandom();
//		byte[] secretKeyBytes = new byte[32]; // 256 bits
//		secureRandom.nextBytes(secretKeyBytes);
//		return Base64.getEncoder().encodeToString(secretKeyBytes);
//	}
	public static void main(String[] args) {
		SpringApplication.run(CarHiveApplication.class, args);
/*		String secretKey = generateSecretKey();
		System.out.println("Generated Secret Key: " + secretKey);*/
	}

}
