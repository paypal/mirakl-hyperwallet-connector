package com.paypal.kyc.incomingnotifications.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of interface {@link KYCUserNotificationService}
 */
@Slf4j
@Service
public class KYCUserNotificationServiceImpl implements KYCUserNotificationService {

	private final KYCUserStatusExecutor kyCUserStatusExecutor;

	private final KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutor;

	private final Converter<Object, KYCUserStatusNotificationBodyModel> hyperWalletObjectToKycUserNotificationBodyModelConverter;

	private final Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter;

	public KYCUserNotificationServiceImpl(final KYCUserStatusExecutor kyCUserStatusExecutor,
			final KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutor,
			final Converter<Object, KYCUserStatusNotificationBodyModel> hyperWalletObjectToKycUserNotificationBodyModelConverter,
			final Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter) {
		this.kyCUserStatusExecutor = kyCUserStatusExecutor;
		this.kycUserDocumentFlagsExecutor = kycUserDocumentFlagsExecutor;
		this.hyperWalletObjectToKycUserNotificationBodyModelConverter = hyperWalletObjectToKycUserNotificationBodyModelConverter;
		this.hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter = hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUserKYCStatus(final HyperwalletWebhookNotification incomingNotification) {
		final KYCUserStatusNotificationBodyModel kycUserNotification = hyperWalletObjectToKycUserNotificationBodyModelConverter
				.convert(incomingNotification.getObject());
		kyCUserStatusExecutor.execute(kycUserNotification);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUserDocumentsFlags(final HyperwalletWebhookNotification incomingNotification) {
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter
				.convert(incomingNotification.getObject());
		kycUserDocumentFlagsExecutor.execute(kycUserDocumentFlagsNotificationBodyModel);
	}

}
