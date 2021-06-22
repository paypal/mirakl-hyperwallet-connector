package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCRejectionReasonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Converts Notification object into {@link KYCUserStatusNotificationBodyModel}
 */
@Slf4j
@Service
public class HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter
		implements Converter<Object, KYCUserStatusNotificationBodyModel> {

	private final KYCRejectionReasonService kycRejectionReasonService;

	public HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter(
			final KYCRejectionReasonService kycRejectionReasonService) {
		this.kycRejectionReasonService = kycRejectionReasonService;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public KYCUserStatusNotificationBodyModel convert(final Object source) {
		if (source instanceof Map) {
			final Map<String, String> notificationDetails = (Map<String, String>) source;

			//@formatter:off
            return KYCUserStatusNotificationBodyModel.builder()
                    .profileType(EnumUtils.getEnum(HyperwalletUser.ProfileType.class,
                            Optional.ofNullable(notificationDetails.get("profileType")).
                                    orElse(null)))
                    .clientUserId(Optional.ofNullable(notificationDetails.get("clientUserId"))
                            .orElse(null))
                    .verificationStatus(EnumUtils.getEnum(HyperwalletUser.VerificationStatus.class,
                            Optional.ofNullable(notificationDetails.get("verificationStatus"))
                                    .orElse(null)))
                    .businessStakeholderVerificationStatus(EnumUtils.getEnum(HyperwalletUser.BusinessStakeholderVerificationStatus.class,
                            Optional.ofNullable(notificationDetails.get("businessStakeholderVerificationStatus"))
                                    .orElse(null)))
                    .letterOfAuthorizationStatus(EnumUtils.getEnum(HyperwalletUser.LetterOfAuthorizationStatus.class,
                            Optional.ofNullable(notificationDetails.get("letterOfAuthorizationStatus"))
                                    .orElse(null)))
                    .reasonsType(kycRejectionReasonService.getReasonTypes(source))
                    .build();
            //@formatter:on

		}
		log.warn("The notification body looks empty");
		return null;

	}

}
