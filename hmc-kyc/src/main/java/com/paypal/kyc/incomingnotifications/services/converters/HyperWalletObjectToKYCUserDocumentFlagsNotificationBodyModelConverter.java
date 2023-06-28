package com.paypal.kyc.incomingnotifications.services.converters;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Converts Notification Object into {@link KYCUserDocumentFlagsNotificationBodyModel}
 */
@Slf4j
@Service
public class HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverter
		implements Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> {

	@Value("#{'${hmc.hyperwallet.programs.names}'}")
	private String hyperwalletPrograms;

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	public HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverter(
			final UserHyperwalletSDKService userHyperwalletSDKService) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public KYCUserDocumentFlagsNotificationBodyModel convert(final Object source) {
		if (source instanceof Map) {
			final Map<String, String> notificationDetails = (Map<String, String>) source;

			//@formatter:off
			return KYCUserDocumentFlagsNotificationBodyModel.builder()
					.userToken(Optional.ofNullable(notificationDetails.get("token"))
							.orElse(null))
					.profileType(EnumUtils.getEnum(HyperwalletUser.ProfileType.class,
							Optional.ofNullable(notificationDetails.get("profileType")).
									orElse(null)))
					.clientUserId(Optional.ofNullable(notificationDetails.get("clientUserId"))
							.orElse(null))
					.businessStakeholderVerificationStatus(EnumUtils.getEnum(HyperwalletUser.BusinessStakeholderVerificationStatus.class,
							Optional.ofNullable(notificationDetails.get("businessStakeholderVerificationStatus"))
									.orElse(null)))
					.verificationStatus(EnumUtils.getEnum(HyperwalletUser.VerificationStatus.class,
							Optional.ofNullable(notificationDetails.get("verificationStatus"))
									.orElse(null)))
					.hyperwalletProgram(Optional.ofNullable(notificationDetails.get("programToken"))
							.map(this::getHyperwalletProgramForProgramToken)
							.orElse(null))
					.build();
			//@formatter:on

		}

		log.warn("The notification body looks empty");
		return null;
	}

	private String getHyperwalletProgramForProgramToken(final String programToken) {
		for (final String program : getHyperwalletPrograms().split(",")) {
			try {
				final Hyperwallet instance = userHyperwalletSDKService
						.getHyperwalletInstanceByHyperwalletProgram(program);
				instance.getProgram(programToken);
				return program;
			}
			catch (final HyperwalletException e) {
				// Program not found for token, continue until one is found
			}
		}

		log.info("No programs were found for program token {}", programToken);
		return null;
	}

	protected String getHyperwalletPrograms() {
		return hyperwalletPrograms;
	}

}
