package com.paypal.kyc.statussynchronization.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;
import org.springframework.stereotype.Component;

@Component
public class HyperwalletUserToKYCUserStatusInfoModelConverter
		implements Converter<HyperwalletUser, KYCUserStatusInfoModel> {

	private final Converter<HyperwalletUser, KYCUserDocumentFlagsNotificationBodyModel> hyperwalletUserToKycUserDocumentFlagsNotificationBodyModel;

	private final Converter<HyperwalletUser, KYCUserStatusNotificationBodyModel> hyperwalletUserToKycUserStatusNotificationBodyModelConverter;

	public HyperwalletUserToKYCUserStatusInfoModelConverter(
			final Converter<HyperwalletUser, KYCUserDocumentFlagsNotificationBodyModel> hyperwalletUserToKycUserDocumentFlagsNotificationBodyModel,
			final Converter<HyperwalletUser, KYCUserStatusNotificationBodyModel> hyperwalletUserToKycUserStatusNotificationBodyModelConverter) {
		this.hyperwalletUserToKycUserDocumentFlagsNotificationBodyModel = hyperwalletUserToKycUserDocumentFlagsNotificationBodyModel;
		this.hyperwalletUserToKycUserStatusNotificationBodyModelConverter = hyperwalletUserToKycUserStatusNotificationBodyModelConverter;
	}

	@Override
	public KYCUserStatusInfoModel convert(final HyperwalletUser source) {
		return new KYCUserStatusInfoModel(hyperwalletUserToKycUserStatusNotificationBodyModelConverter.convert(source),
				hyperwalletUserToKycUserDocumentFlagsNotificationBodyModel.convert(source));
	}

}
