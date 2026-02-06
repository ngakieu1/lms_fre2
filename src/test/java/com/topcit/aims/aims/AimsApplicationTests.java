package com.topcit.aims.aims;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AimsApplicationTests {

//	@Test
//	void contextLoads() {
//	}
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "mypassword123";
		String encodePassword = encoder.encode(rawPassword);
		System.out.print("Raw:"+rawPassword);
		System.out.print("Encode:"+encodePassword);
//		String encodePassword2 = encoder.encode(rawPassword);
//		System.out.print("Encode 2nd times:"+encodePassword2);
		boolean matches = encoder.matches(rawPassword, encodePassword);
		System.out.print("Matches:"+matches);
	}

}
