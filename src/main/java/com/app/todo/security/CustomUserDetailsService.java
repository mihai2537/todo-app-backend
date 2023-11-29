package com.app.todo.security;

import com.app.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired UserRepository userRepo;

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    return userRepo
        .findByEmail(userName)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + userName));
  }
}
