package com.paypal.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.infrastructure.configuration.PublicKeysHyperwalletApiConfig;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.paypal.infrastructure.constants.HyperWalletConstants.JWKSET_ERROR_FILENAME;

/**
 * Optional controller that allows to expose your public keys for encryption
 */

@Slf4j
@RestController
@RequestMapping("/jwkset")
@Profile({ "encrypted" })
public class JwkSetController {

	private final PublicKeysHyperwalletApiConfig publicKeysHyperwalletApiConfig;

	private final ObjectMapper mapper;

	@Value("classpath:" + JWKSET_ERROR_FILENAME)
	protected Resource errorResource;

	public JwkSetController(final PublicKeysHyperwalletApiConfig publicKeysHyperwalletApiConfig,
			final ObjectMapper objectMapper) {
		this.publicKeysHyperwalletApiConfig = publicKeysHyperwalletApiConfig;
		this.mapper = objectMapper;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JSONObject getPublicKeys() throws IOException {
		try {
			final InputStream publicKeysStream = getPublicKeysFromFile();
			return this.mapper.readValue(publicKeysStream, JSONObject.class);
		}
		catch (final FileNotFoundException ex) {
			log.error(
					"File that contains public keys for encryption [{}] has an incorrect name or it's not defined: {}",
					this.publicKeysHyperwalletApiConfig.getHmcPublicKeyLocation(), ex.getMessage());
		}
		return this.mapper.readValue(this.errorResource.getInputStream(), JSONObject.class);
	}

	protected InputStream getPublicKeysFromFile() throws FileNotFoundException {
		return new FileInputStream(this.publicKeysHyperwalletApiConfig.getHmcPublicKeyLocation());
	}

}
