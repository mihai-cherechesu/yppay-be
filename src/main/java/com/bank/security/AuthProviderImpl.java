package com.bank.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthProviderImpl implements AuthenticationProvider {

    private final UserDetailsImpl userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthProviderImpl(UserDetailsImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(email);

        if (passwordEncoder.matches(password, securityUser.getPassword())) {
            return new UsernamePasswordAuthenticationToken(securityUser, password, null);
        }

        throw new BadCredentialsException("Credentials don't match.");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.equals(aClass);
    }

}
