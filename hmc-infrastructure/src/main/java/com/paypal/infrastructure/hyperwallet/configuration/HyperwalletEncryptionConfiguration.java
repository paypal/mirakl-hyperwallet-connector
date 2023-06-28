package com.paypal.infrastructure.hyperwallet.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class HyperwalletEncryptionConfiguration {

	@Value("#{'${hmc.hyperwallet.encryption.encryptionEnabled}'}")
	protected boolean enabled;

	@Value("#{'${hmc.hyperwallet.encryption.encryptionAlgorithm}'}")
	protected String encryptionAlgorithm;

	@Value("#{'${hmc.hyperwallet.encryption.signAlgorithm}'}")
	protected String signAlgorithm;

	@Value("#{'${hmc.hyperwallet.encryption.encryptionMethod}'}")
	protected String encryptionMethod;

	@Value("#{'${hmc.hyperwallet.encryption.expirationMinutes}'}")
	protected Integer expirationMinutes;

	@Value("#{'${hmc.hyperwallet.encryption.hwKeySetLocation}'}")
	protected String hwKeySetLocation;

	@Value("#{'${hmc.hyperwallet.encryption.hmcKeySetLocation}'}")
	protected String hmcKeySetLocation;

	@Value("#{'${hmc.hyperwallet.encryption.hmcPublicKeyLocation}'}")
	protected String hmcPublicKeyLocation;

}
