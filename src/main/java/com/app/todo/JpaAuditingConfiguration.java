package com.app.todo;

import com.app.todo.model.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * This configuration has to be separated from TodoApplication class
 * because otherwise it will be loaded by @WebMvcTest
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class JpaAuditingConfiguration {

    /**
     * Creates the bean in charge of populating the @CreatedBy and @UpdatedBy fields
     * @return the custom AuditorAware object
     */
    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
