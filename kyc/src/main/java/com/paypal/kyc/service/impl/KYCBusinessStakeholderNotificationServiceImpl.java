package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import com.paypal.kyc.service.KYCUserNotificationService;
import com.paypal.kyc.strategies.status.impl.KYCBusinessStakeholderStatusFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of interface {@link KYCUserNotificationService}
 */
@Slf4j
@Service
public class KYCBusinessStakeholderNotificationServiceImpl implements KYCBusinessStakeholderNotificationService {

	private final KYCBusinessStakeholderStatusFactory kycBusinessStakeholderStatusFactory;

	private final Converter<Object, KYCBusinessStakeholderStatusNotificationBodyModel> hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter;

	public KYCBusinessStakeholderNotificationServiceImpl(
			final KYCBusinessStakeholderStatusFactory kycBusinessStakeholderStatusFactory,
			final Converter<Object, KYCBusinessStakeholderStatusNotificationBodyModel> hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter) {
		this.kycBusinessStakeholderStatusFactory = kycBusinessStakeholderStatusFactory;
		this.hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter = hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateBusinessStakeholderKYCStatus(final HyperwalletWebhookNotification incomingNotification) {
		final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderNotification = hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter
				.convert(incomingNotification.getObject());
		kycBusinessStakeholderStatusFactory.execute(kycBusinessStakeholderNotification);
	}

}
