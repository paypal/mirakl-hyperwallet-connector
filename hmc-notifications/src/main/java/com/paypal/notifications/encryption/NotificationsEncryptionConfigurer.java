package com.paypal.notifications.encryption;

import com.paypal.notifications.encryption.httpconverters.JWEConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class NotificationsEncryptionConfigurer implements WebMvcConfigurer {

	private final JWEConverter jweConverter;

	public NotificationsEncryptionConfigurer(final JWEConverter jweConverter) {
		this.jweConverter = jweConverter;
	}

	@Override
	public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
		converters.add(0, jweConverter);
	}

}
