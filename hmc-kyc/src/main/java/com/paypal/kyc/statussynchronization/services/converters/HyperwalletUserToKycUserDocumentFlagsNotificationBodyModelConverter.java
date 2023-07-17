package com.paypal.kyc.statussynchronization.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletUserToKycUserDocumentFlagsNotificationBodyModelConverter
		extends AbstractKycNotificationBodyModelConverter<KYCUserDocumentFlagsNotificationBodyModel>
		implements Converter<HyperwalletUser, KYCUserDocumentFlagsNotificationBodyModel> {

	public HyperwalletUserToKycUserDocumentFlagsNotificationBodyModelConverter(
			final Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> toNotificationBodyModelConverter) {
		super(toNotificationBodyModelConverter);
	}

}
