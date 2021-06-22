package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
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
                    .build();
            //@formatter:on

		}
		log.warn("The notification body looks empty");
		return null;
	}

}
