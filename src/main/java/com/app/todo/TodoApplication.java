package com.app.todo;

import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import com.app.todo.security.RsaKeyProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(RsaKeyProperties.class)
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(UserRepository userRepository) {
		return args -> {
			userRepository.save(new User("admin@admin.com", "pass", "ROLE_ADMIN,ROLE_USER"));
			userRepository.save(new User("user@user.com", "pass", "ROLE_USER"));
		};
	}

}
