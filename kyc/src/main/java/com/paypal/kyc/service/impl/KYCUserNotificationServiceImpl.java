package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCUserNotificationService;
import com.paypal.kyc.strategies.documents.flags.impl.KYCUserDocumentFlagsFactory;
import com.paypal.kyc.strategies.status.impl.KYCUserStatusFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of interface {@link KYCUserNotificationService}
 */
@Slf4j
@Service
public class KYCUserNotificationServiceImpl implements KYCUserNotificationService {

	private final KYCUserStatusFactory kyCUserNotificationFactory;

	private final KYCUserDocumentFlagsFactory kycUserDocumentFlagsFactory;

	private final Converter<Object, KYCUserStatusNotificationBodyModel> hyperWalletObjectToKycUserNotificationBodyModelConverter;

	private final Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter;

	public KYCUserNotificationServiceImpl(final KYCUserStatusFactory kyCUserNotificationFactory,
			final KYCUserDocumentFlagsFactory kycUserDocumentFlagsFactory,
			final Converter<Object, KYCUserStatusNotificationBodyModel> hyperWalletObjectToKycUserNotificationBodyModelConverter,
			final Converter<Object, KYCUserDocumentFlagsNotificationBodyModel> hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter) {
		this.kyCUserNotificationFactory = kyCUserNotificationFactory;
		this.kycUserDocumentFlagsFactory = kycUserDocumentFlagsFactory;
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
		kyCUserNotificationFactory.execute(kycUserNotification);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUserDocumentsFlags(final HyperwalletWebhookNotification incomingNotification) {
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = hyperWalletObjectToKycUserDocumentFlagsNotificationBodyModelConverter
				.convert(incomingNotification.getObject());
		kycUserDocumentFlagsFactory.execute(kycUserDocumentFlagsNotificationBodyModel);
	}

}
