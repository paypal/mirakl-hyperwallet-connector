package com.paypal.infrastructure.configuration;

import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Class that contains all parameters needed for encryption
 */

@Getter
@PropertySource({ "classpath:application.properties" })
public abstract class AbstractHyperwalletEncryptionApiConfig {

	@Value("#{'${hyperwallet.api.encryptionAlgorithm}'}")
	protected String encryptionAlgorithm;

	@Value("#{'${hyperwallet.api.signAlgorithm}'}")
	protected String signAlgorithm;

	@Value("#{'${hyperwallet.api.encryptionMethod}'}")
	protected String encryptionMethod;

	@Value("#{'${hyperwallet.api.expirationMinutes}'}")
	protected Integer expirationMinutes;

	@Value("#{'${hyperwallet.api.hwKeySetLocation}'}")
	protected String hwKeySetLocation;

	@Value("#{'${hyperwallet.api.hmcKeySetLocation}'}")
	protected String hmcKeySetLocation;

	@Value("#{'${hyperwallet.api.qaKeySetLocation}'}")
	protected String qaKeySetLocation;

	@Primary
	@Bean(name = "hyperwalletEncryptionWrapper")
	public HyperwalletEncryption hyperwalletEncryptionWrapper() {
		//@formatter:off
		return new HyperwalletEncryption(JWEAlgorithm.parse(getEncryptionAlgorithm()), JWSAlgorithm.parse(getSignAlgorithm()),
				EncryptionMethod.parse(getEncryptionMethod()), getHmcKeySetLocation(), getHwKeySetLocation(),
				getExpirationMinutes());
		//@formatter:on
	}

	@Profile({ "qaEncrypted" })
	@Bean(name = "hyperwalletQAEncryptionWrapper")
	public HyperwalletEncryption hyperwalletQAEncryptionWrapper() {
		//@formatter:off
		return new HyperwalletEncryption(JWEAlgorithm.parse(getEncryptionAlgorithm()), JWSAlgorithm.parse(getSignAlgorithm()),
				EncryptionMethod.parse(getEncryptionMethod()), getHmcKeySetLocation(), getQaKeySetLocation(),
				getExpirationMinutes());
		//@formatter:on
	}

}
