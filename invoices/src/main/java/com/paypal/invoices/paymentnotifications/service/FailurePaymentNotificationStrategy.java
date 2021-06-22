package com.paypal.invoices.paymentnotifications.service;

import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.invoices.infraestructure.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * Class that handles the error on payment notifications
 */
@Service
public class FailurePaymentNotificationStrategy implements Strategy<PaymentNotificationBodyModel, Optional<Void>> {

	@Resource
	private PaymentNotificationConfig paymentNotificationConfig;

	@Resource
	private MailNotificationUtil mailNotificationUtil;

	/**
	 * Executes the business logic based on the content of
	 * {@code paymentNotificationBodyModel} and returns a {@link Void} class based on a
	 * set of strategies
	 * @param paymentNotificationBodyModel the paymentNotificationBodyModel object of type
	 * {@link PaymentNotificationBodyModel}
	 * @return the converted object of type {@link Void}
	 */
	@Override
	public Optional<Void> execute(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		mailNotificationUtil.sendPlainTextEmail(
				String.format("Payment Issue - %s", paymentNotificationBodyModel.getClientPaymentId()),
				String.format(
						"There was an issue with payment of %s invoice. The payment status is %s. Please login to Hyperwallet to view an resolve the payment issue.",
						paymentNotificationBodyModel.getClientPaymentId(), paymentNotificationBodyModel.getStatus()));
		return Optional.empty();
	}

	/**
	 * Checks whether the strategy must be executed based on the
	 * {@code paymentNotificationBodyModel}
	 * @param paymentNotificationBodyModel the paymentNotificationBodyModel object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		return Objects.nonNull(paymentNotificationBodyModel)
				&& paymentNotificationConfig.getFailureStatuses().contains(paymentNotificationBodyModel.getStatus());
	}

}
