package com.paypal.kyc.incomingnotifications.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.model.KYCDocumentNotificationModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.services.KYCRejectionReasonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
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

	private final Converter<Object, List<KYCDocumentNotificationModel>> objectKYCDocumentNotificationModelListConverter;

	public HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter(
			final KYCRejectionReasonService kycRejectionReasonService,
			final Converter<Object, List<KYCDocumentNotificationModel>> objectKYCDocumentNotificationModelListConverter) {
		this.kycRejectionReasonService = kycRejectionReasonService;
		this.objectKYCDocumentNotificationModelListConverter = objectKYCDocumentNotificationModelListConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public KYCUserStatusNotificationBodyModel convert(final Object source) {
		if (source instanceof Map) {
			final Map<String, Object> notificationDetails = (Map<String, Object>) source;

			//@formatter:off
			return KYCUserStatusNotificationBodyModel.builder()
					.profileType(EnumUtils.getEnum(HyperwalletUser.ProfileType.class,
							Optional.ofNullable((String) notificationDetails.get("profileType")).
									orElse(null)))
					.clientUserId(Optional.ofNullable((String) notificationDetails.get("clientUserId"))
							.orElse(null))
					.verificationStatus(EnumUtils.getEnum(HyperwalletUser.VerificationStatus.class,
							Optional.ofNullable((String) notificationDetails.get("verificationStatus"))
									.orElse(null)))
					.businessStakeholderVerificationStatus(EnumUtils.getEnum(HyperwalletUser.BusinessStakeholderVerificationStatus.class,
							Optional.ofNullable((String) notificationDetails.get("businessStakeholderVerificationStatus"))
									.orElse(null)))
					.letterOfAuthorizationStatus(EnumUtils.getEnum(HyperwalletUser.LetterOfAuthorizationStatus.class,
							Optional.ofNullable((String) notificationDetails.get("letterOfAuthorizationStatus"))
									.orElse(null)))
					.reasonsType(kycRejectionReasonService.getReasonTypes(source))
					.documents(objectKYCDocumentNotificationModelListConverter.convert(source))
					.businessName(stringValue(notificationDetails, "businessName"))
					.businessOperatingName(stringValue(notificationDetails, "businessOperatingName"))
					.businessRegistrationId(stringValue(notificationDetails, "businessRegistrationId"))
					.firstName(stringValue(notificationDetails, "firstName"))
					.lastName(stringValue(notificationDetails, "lastName"))
					.email(stringValue(notificationDetails, "email"))
					.phoneNumber(stringValue(notificationDetails, "phoneNumber"))
					.mobileNumber(stringValue(notificationDetails, "mobileNumber"))
					.addressLine1(stringValue(notificationDetails, "addressLine1"))
					.addressLine2(stringValue(notificationDetails, "addressLine2"))
					.city(stringValue(notificationDetails, "city"))
					.stateProvince(stringValue(notificationDetails, "stateProvince"))
					.country(stringValue(notificationDetails, "country"))
					.postalCode(stringValue(notificationDetails, "postalCode"))
					.build();
			//@formatter:on

		}
		log.warn("The notification body looks empty");
		return null;
	}

	private static String stringValue(final Map<String, Object> details, final String key) {
		final Object value = details.get(key);
		if (value instanceof String text && StringUtils.isNotBlank(text)) {
			return text;
		}
		return null;
	}

}
