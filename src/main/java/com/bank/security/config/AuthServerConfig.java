package com.bank.security.config;

import com.bank.security.JpaUserAuthConverter;
import com.bank.security.JwtTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final TokenStore tokenStore;

    private final JwtAccessTokenConverter tokenConverter;

    private final JwtTokenEnhancer tokenEnhancer;

    @Autowired
    public AuthServerConfig(AuthenticationManager authenticationManager,
                            UserDetailsService userDetailsService,
                            PasswordEncoder passwordEncoder,
                            TokenStore tokenStore,
                            JwtAccessTokenConverter tokenConverter,
                            JwtTokenEnhancer tokenEnhancer) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
        this.tokenConverter = tokenConverter;
        this.tokenEnhancer = tokenEnhancer;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("ing-client")
                .secret(passwordEncoder.encode("client-pwd"))
                .authorizedGrantTypes(
                        "refresh_token",
                        "password")
                .scopes("webclient");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        var defaultAccessTokenConverter =
                (DefaultAccessTokenConverter) tokenConverter.getAccessTokenConverter();
        var userAuthenticationConverter = new JpaUserAuthConverter();

        userAuthenticationConverter.setUserDetailsService(userDetailsService);
        defaultAccessTokenConverter.setUserTokenConverter(userAuthenticationConverter);

        var tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer, tokenConverter));

        endpoints
                .reuseRefreshTokens(false)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(tokenStore)
                .accessTokenConverter(tokenConverter)
                .tokenEnhancer(tokenEnhancerChain)
                .exceptionTranslator(loggingExceptionTranslator());
    }

    @Bean
    public WebResponseExceptionTranslator<OAuth2Exception> loggingExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                e.printStackTrace();

                ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
                HttpHeaders headers = new HttpHeaders();
                headers.setAll(responseEntity.getHeaders().toSingleValueMap());
                OAuth2Exception exceptionBody = responseEntity.getBody();
                return new ResponseEntity<>(exceptionBody, headers, responseEntity.getStatusCode());
            }
        };
    }

}
