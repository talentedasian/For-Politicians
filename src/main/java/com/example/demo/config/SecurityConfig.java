package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.savedrequest.NullRequestCache;

import com.example.demo.oauth2.AuthorizedRequestsRepository;
import com.example.demo.oauth2.Oauth2CustomSuccessHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.requestCache()
				.requestCache(new NullRequestCache())
			.and()
			.csrf()
				.disable()
			.httpBasic()
				.disable()
				.oauth2Client();
				
	}
	
	@Bean
	public OAuth2AuthorizedClientService clientService() {
		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
	}
	
	@Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }
	
	@Bean
	public OAuth2AuthorizedClientRepository authorizedClientRepo() {
		return new AuthorizedRequestsRepository();
	}
	

	private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("facebook")
            .clientId("697702354184763")
            .clientSecret("88e0d00193984f18f0a21f234091702d")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/facebook")
            .authorizationUri("https://www.facebook.com/dialog/oauth")
            .tokenUri("https://graph.facebook.com/v10.0/oauth/access_token")
            .userInfoUri("https://graph.facebook.com/me")
            .userNameAttributeName("id,email")
            .clientName("Facebook")
            .build();
	    }

}
