package com.paypal.invoices.paymentnotifications.service;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Class that logs that a notification is empty
 */
@Service
@Slf4j
public class EmptyPaymentNotificationStrategy implements Strategy<PaymentNotificationBodyModel, Optional<Void>> {

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
		log.warn("Payment notification received with a null object.");
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
		return Objects.isNull(paymentNotificationBodyModel);
	}

}
