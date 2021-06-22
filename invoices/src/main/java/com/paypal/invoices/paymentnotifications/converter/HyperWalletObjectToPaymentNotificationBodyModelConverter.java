package com.paypal.invoices.paymentnotifications.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Class to convert from {@link Object} to {@link PaymentNotificationBodyModel}
 */
@Slf4j
@Service
public class HyperWalletObjectToPaymentNotificationBodyModelConverter
		implements Converter<Object, PaymentNotificationBodyModel> {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PaymentNotificationBodyModel convert(final Object source) {
		if (source instanceof Map) {
			final Map<String, String> notificationDetails = (Map<String, String>) source;
			//@formatter:off
			return PaymentNotificationBodyModel.builder()
					.token(Optional.ofNullable(notificationDetails.get("token")).orElse(null))
					.status(Optional.ofNullable(notificationDetails.get("status")).orElse(null))
					.createdOn(Optional.ofNullable(notificationDetails.get("createdOn")).orElse(null))
					.amount(Optional.ofNullable(notificationDetails.get("amount")).orElse(null))
					.currency(Optional.ofNullable(notificationDetails.get("currency")).orElse(null))
					.clientPaymentId(Optional.ofNullable(notificationDetails.get("clientPaymentId")).orElse(null))
					.notes(Optional.ofNullable(notificationDetails.get("notes")).orElse(null))
					.purpose(Optional.ofNullable(notificationDetails.get("purpose")).orElse(null))
					.releaseOn(Optional.ofNullable(notificationDetails.get("releaseOn")).orElse(null))
					.expiresOn(Optional.ofNullable(notificationDetails.get("expiresOn")).orElse(null))
					.destinationToken(Optional.ofNullable(notificationDetails.get("destinationToken")).orElse(null))
					.build();
			//@formatter:on
		}

		log.warn("The notification body looks empty");
		return null;
	}

}
