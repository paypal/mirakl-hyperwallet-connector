package com.paypal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

//@formatter:off
@Configuration
public class SecurityConfiguration {

	@Value("${hmc.toggle-features.management-api}")
	private boolean managementEnabled;

	@Bean
	public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		if (!managementEnabled) {
			http.authorizeHttpRequests(authorization -> authorization
					.requestMatchers("/management/**").denyAll()
					.requestMatchers("/**").permitAll());
		}
		else {
			http.authorizeHttpRequests(authorization -> authorization
					.requestMatchers("/**").permitAll());
		}

		http.csrf().disable(); //NOSONAR Stateless REST API
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			throw new UsernameNotFoundException("-"); //NOSONAR Unsafe user enumeration false positive
		};
	}

}
