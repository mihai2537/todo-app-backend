package com.app.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

//	@Bean
//	CommandLineRunner commandLineRunner(UserRepository userRepository) {
//		return args -> {
//			userRepository.save(new User("admin@admin.com", "pass", "ADMIN"));
//			userRepository.save(new User("user@user.com", "pass", "USER"));
//		};
//	}
}
