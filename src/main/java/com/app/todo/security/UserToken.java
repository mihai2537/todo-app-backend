package com.app.todo.security;

import com.app.todo.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

/**
 * This is the Authentication instance used when the user is logged-in via Jwt token.
 * The configuration is done via CustomAuthenticationConverter
 * The main benefit of this class is accessing the logged-in user model via getPrincipal()
 */
public class UserToken extends AbstractAuthenticationToken {

    private final Jwt jwt;
    private final User user;

    public UserToken(Jwt jwt, Collection<GrantedAuthority> authorities, User user) {
        super(authorities);

        this.jwt = jwt;
        this.user = user;

    }

    @Override
    public Object getCredentials() {
        return this.jwt;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
