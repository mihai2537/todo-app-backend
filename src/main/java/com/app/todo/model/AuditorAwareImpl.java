package com.app.todo.model;

import com.app.todo.security.UserToken;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * This class is in charge of retrieving the auditor name when we need to populate the @CreatedBy fields
 */
@NonNullApi
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return Optional.empty();

        // When there is no user logged-in (typically when users register)
        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of("system");
        }

        if (authentication instanceof UserToken) {
            User user = (User)authentication.getPrincipal();
            return Optional.of(user.getEmail());
        }

        return Optional.empty();
    }
}
