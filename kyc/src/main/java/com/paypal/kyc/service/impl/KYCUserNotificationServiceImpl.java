package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCUserNotificationService;
import com.paypal.kyc.strategies.documents.flags.impl.KYCUserDocumentFlagsExecutor;
import com.paypal.kyc.strategies.status.impl.KYCUserStatusExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of interface {@link KYCUserNotificationService}
 */
@Slf4j
@Service
public class KYCUserNotificationServiceImpl implements KYCUserNotificationService {

	private final KYCUserStatusExecutor kyCUserNotificationExecutor;

	private final KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutor;

	private final Converter<Object, KYCUserStatusNotificationBodyModel> hyperWalletObjectToKycUserNotificationBodyModelConverter;

	private final Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter;

	public KYCUserNotificationServiceImpl(final KYCUserStatusExecutor kyCUserNotificationExecutor,
			final KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutor,
			final Converter<Object, KYCUserStatusNotificationBodyModel> hyperWalletObjectToKycUserNotificationBodyModelConverter,
			final Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter) {
		this.kyCUserNotificationExecutor = kyCUserNotificationExecutor;
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
		kyCUserNotificationExecutor.execute(kycUserNotification);
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
