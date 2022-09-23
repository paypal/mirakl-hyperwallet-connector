package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
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
public class HyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverter
		implements Converter<HyperwalletWebhookNotification, KYCBusinessStakeholderStatusNotificationBodyModel> {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public KYCBusinessStakeholderStatusNotificationBodyModel convert(final HyperwalletWebhookNotification source) {
		if (source.getObject() instanceof Map) {
			final Map<String, Object> notificationDetails = (Map<String, Object>) source.getObject();

			//@formatter:off
			final KYCBusinessStakeholderStatusNotificationBodyModel.KYCBusinessStakeholderStatusNotificationBodyModelBuilder<?, ?> builder
					= KYCBusinessStakeholderStatusNotificationBodyModel.builder();

			Optional.ofNullable((Boolean) notificationDetails.get("isDirector")).ifPresent(builder::isDirector);
			Optional.ofNullable((Boolean) notificationDetails.get("isBusinessContact")).ifPresent(builder::isBusinessContact);
			Optional.ofNullable((String) notificationDetails.get("verificationStatus"))
					.map(verificationStatus -> EnumUtils.getEnum(HyperwalletUser.VerificationStatus.class, verificationStatus))
					.ifPresent(builder::verificationStatus);
			Optional.ofNullable((String) notificationDetails.get("profileType"))
					.map(profileType -> EnumUtils.getEnum(HyperwalletUser.ProfileType.class, profileType))
					.ifPresent(builder::profileType);
			Optional.ofNullable((String) notificationDetails.get("token")).ifPresent(builder::token);
			Optional.ofNullable((String) notificationDetails.get("userToken")).ifPresent(builder::userToken);
			Optional.ofNullable(source.getType()).ifPresent(builder::hyperwalletWebhookNotificationType);

			return builder.build();
			//@formatter:on
		}
		log.warn("The notification body looks empty");
		return null;
	}

}
