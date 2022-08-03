package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import com.paypal.kyc.service.KYCUserNotificationService;
import com.paypal.kyc.strategies.status.impl.KYCBusinessStakeholderStatusExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of interface {@link KYCUserNotificationService}
 */
@Slf4j
@Service
public class KYCBusinessStakeholderNotificationServiceImpl implements KYCBusinessStakeholderNotificationService {

	private final KYCBusinessStakeholderStatusExecutor kycBusinessStakeholderStatusExecutor;

	private final Converter<HyperwalletWebhookNotification, KYCBusinessStakeholderStatusNotificationBodyModel> hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter;

	public KYCBusinessStakeholderNotificationServiceImpl(
			final KYCBusinessStakeholderStatusExecutor kycBusinessStakeholderStatusExecutor,
			final Converter<HyperwalletWebhookNotification, KYCBusinessStakeholderStatusNotificationBodyModel> hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter) {
		this.kycBusinessStakeholderStatusExecutor = kycBusinessStakeholderStatusExecutor;
		this.hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter = hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateBusinessStakeholderKYCStatus(final HyperwalletWebhookNotification incomingNotification) {
		final KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderNotification = hyperWalletObjectToKycBusinessStakeholderStatusNotificationBodyModelConverter
				.convert(incomingNotification);
		try {
			kycBusinessStakeholderStatusExecutor.execute(kycBusinessStakeholderNotification);
		}
		// Rethrow exception to handle it in AbstractNotificationListener
		catch (final HyperwalletException ex) {
			logException(incomingNotification, HyperwalletLoggingErrorsUtil.stringify(ex), ex);
			throw ex;
		}
		catch (final MiraklException ex) {
			logException(incomingNotification, MiraklLoggingErrorsUtil.stringify(ex), ex);
			throw ex;
		}
		catch (final RuntimeException ex) {
			logException(incomingNotification, ex.getMessage(), ex);
			throw ex;
		}
	}

	private void logException(final HyperwalletWebhookNotification incomingNotification, final String exceptionMessage,
			Exception e) {
		log.error(String.format(
				"Notification [%s] could not be processed - the KYC Letter of authorization for a business stakeholder could not be updated.%n%s",
				incomingNotification.getToken(), exceptionMessage), e);
	}

}
