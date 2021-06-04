package com.bank.security.config;

import com.bank.security.JwtTokenEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
public class JwtTokenStoreConfig {

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public JwtAccessTokenConverter tokenConverter() {
        String keyStoreKeyFactoryPassword = "udzVTE49YCFnrgjVnpvhSttqS9mYtjUWn5ecMZwd4hus434GEpWRhvEdKDdZLdZkGnYC5R" +
                                                        "GUztVykUduPFH9m9u9wXVtG5Vk3HAZVT8p8pAaYufYeKnMVStPFAthNscp";

        var keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"),
                                       keyStoreKeyFactoryPassword.toCharArray());

        var converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        var defaultTokenServices =
                new DefaultTokenServices();

        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setReuseRefreshToken(false);
        return defaultTokenServices;
    }

    @Bean
    public JwtTokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }

}
