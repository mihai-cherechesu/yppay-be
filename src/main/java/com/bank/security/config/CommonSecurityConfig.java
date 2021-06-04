package com.bank.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CommonSecurityConfig extends AbstractHttpConfigurer<CommonSecurityConfig, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
    }

    public static CommonSecurityConfig commonSecurityConfiguration() {
        return new CommonSecurityConfig();
    }

}
