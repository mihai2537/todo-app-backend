package com.app.todo;

import com.app.todo.model.AuditorAwareImpl;
import com.app.todo.model.User;
import com.app.todo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(UserRepository userRepository) {
		return args -> {
			userRepository.save(new User("admin@admin.com", "pass", "ADMIN"));
			userRepository.save(new User("user@user.com", "pass", "USER"));
		};
	}

	/**
	 * Creates the bean in charge of populating the @CreatedBy and @UpdatedBy fields
	 * @return the custom AuditorAware object
	 */
	@Bean
	AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}

}
