package com.paypal.notifications.httpconverters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Optional;

/**
 * Converts a JWE payload into the specific {@link HyperwalletWebhookNotification}
 */
@Slf4j
@Component
@Profile({ "encrypted" })
public class JWEConverter extends AbstractHttpMessageConverter<Object> {

	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	private final HyperwalletEncryption hyperwalletEncryption;

	private final ObjectMapper objectMapper;

	public JWEConverter(final HyperwalletEncryption hyperwalletEncryption, final ObjectMapper objectMapper) {
		super(new MediaType("application", "jose+json", DEFAULT_CHARSET));
		this.hyperwalletEncryption = hyperwalletEncryption;
		this.objectMapper = objectMapper;
	}

	@Override
	protected boolean supports(final Class<?> clazz) {
		return clazz.equals(HyperwalletWebhookNotification.class);
	}

	@Override
	protected Object readInternal(final Class<?> clazz, final HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return this.objectMapper.readValue(decrypt(inputMessage.getBody()), clazz);
	}

	@Override
	protected void writeInternal(final Object object, final HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		outputMessage.getBody().write(this.objectMapper.writeValueAsBytes(object));
	}

	/**
	 * requests params of any API
	 * @param inputStream inputStream
	 * @return inputStream
	 */
	protected InputStream decrypt(final InputStream inputStream) {
		final String body = getBodyAsString(inputStream);
		String decrypt = null;
		try {
			decrypt = this.hyperwalletEncryption.decrypt(body);
		}
		catch (final ParseException | IOException | JOSEException e) {
			log.error("Something went wrong decrypting the JWE", e);
		}

		return new ByteArrayInputStream(
				Optional.ofNullable(decrypt).orElse(StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8));
	}

	protected String getBodyAsString(final InputStream inputStream) {
		try {
			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
		catch (final IOException e) {
			log.error("Something went wrong converting inputStream into string", e);
			return null;
		}
	}

}
