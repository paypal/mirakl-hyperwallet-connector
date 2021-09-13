package com.paypal.kyc.converter;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
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
		implements Converter<Object, KYCBusinessStakeholderStatusNotificationBodyModel> {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public KYCBusinessStakeholderStatusNotificationBodyModel convert(final Object source) {
		if (source instanceof Map) {
			final Map<String, Object> notificationDetails = (Map<String, Object>) source;

			//@formatter:off
			return KYCBusinessStakeholderStatusNotificationBodyModel.builder()
							.verificationStatus(EnumUtils.getEnum(HyperwalletUser.VerificationStatus.class,
											Optional.ofNullable((String) notificationDetails.get("verificationStatus"))
															.orElse(null)))
							.token(Optional.ofNullable((String) notificationDetails.get("token"))
											.orElse(null))
							.userToken(Optional.ofNullable((String) notificationDetails.get("userToken"))
											.orElse(null))
							.profileType(EnumUtils.getEnum(HyperwalletUser.ProfileType.class,
											Optional.ofNullable((String) notificationDetails.get("profileType"))
															.orElse(null)))
							.build();
			//@formatter:on
		}
		log.warn("The notification body looks empty");
		return null;
	}

}
