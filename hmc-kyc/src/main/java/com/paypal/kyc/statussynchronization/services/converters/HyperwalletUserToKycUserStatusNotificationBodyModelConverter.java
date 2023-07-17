package com.paypal.kyc.statussynchronization.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletUserToKycUserStatusNotificationBodyModelConverter
		extends AbstractKycNotificationBodyModelConverter<KYCUserStatusNotificationBodyModel>
		implements Converter<HyperwalletUser, KYCUserStatusNotificationBodyModel> {

	public HyperwalletUserToKycUserStatusNotificationBodyModelConverter(
			final Converter<Object, KYCUserStatusNotificationBodyModel> toNotificationBodyModelConverter) {
		super(toNotificationBodyModelConverter);
	}

}
