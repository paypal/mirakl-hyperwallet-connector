package com.paypal.invoices.paymentnotifications.services;

import static com.paypal.infrastructure.hyperwallet.constants.HyperWalletConstants.PAYMENT_OPERATOR_SUFFIX;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklPayOutState;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCyclePaymentConfirmation;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklConfirmSellerBillingCyclePaymentRequest;
import com.paypal.infrastructure.hyperwallet.constants.HyperWalletConstants;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.invoices.paymentnotifications.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Strategy to update the status of an accepted payment in Mirakl
 */
@Slf4j
@Service
public class AcceptedPaymentNotificationStrategy implements Strategy<PaymentNotificationBodyModel, Void> {

	@Resource
	private PaymentNotificationConfig paymentNotificationConfig;

	@Resource
	private MiraklClient miraklClient;

	/**
	 * Executes the business logic based on the content of
	 * {@code paymentNotificationBodyModel} and returns a {@link Void} class based on a
	 * set of strategies
	 * @param paymentNotificationBodyModel the paymentNotificationBodyModel object of type
	 * {@link PaymentNotificationBodyModel}
	 * @return the converted object of type {@link Void}
	 */
	@Override
	public Void execute(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		final MiraklConfirmSellerBillingCyclePaymentRequest paymentConfirmationRequest = createPaymentConfirmationRequest(
				paymentNotificationBodyModel);

		miraklClient.confirmSellerBillingCyclePayment(paymentConfirmationRequest);

		return null;
	}

	protected MiraklConfirmSellerBillingCyclePaymentRequest createPaymentConfirmationRequest(
			final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		final MiraklSellerBillingCyclePaymentConfirmation miraklSellerBillingCyclePaymentConfirmation = new MiraklSellerBillingCyclePaymentConfirmation();
		miraklSellerBillingCyclePaymentConfirmation
			.setAmountTransferredToSeller(new BigDecimal(paymentNotificationBodyModel.getAmount()));
		miraklSellerBillingCyclePaymentConfirmation.setCurrencyIsoCode(
				EnumUtils.getEnum(MiraklIsoCurrencyCode.class, paymentNotificationBodyModel.getCurrency(), null));
		miraklSellerBillingCyclePaymentConfirmation
			.setId(UUID.fromString(paymentNotificationBodyModel.getClientPaymentId()));
		final Date date = DateUtil.convertToDate(paymentNotificationBodyModel.getCreatedOn(),
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, DateUtil.TIME_UTC);
		miraklSellerBillingCyclePaymentConfirmation.setTransactionDate(date != null ? date.toInstant() : null);
		miraklSellerBillingCyclePaymentConfirmation.setState(MiraklPayOutState.PAID);

		log.info("Creating payment confirmation request for seller billing cycle ID {} and amount {}",
				miraklSellerBillingCyclePaymentConfirmation.getId(),
				miraklSellerBillingCyclePaymentConfirmation.getAmountTransferredToSeller());

		return new MiraklConfirmSellerBillingCyclePaymentRequest(List.of(miraklSellerBillingCyclePaymentConfirmation));
	}

	/**
	 * Checks whether the strategy must be executed based on the
	 * {@code paymentNotificationBodyModel}
	 * @param paymentNotificationBodyModel the {@code paymentNotificationBodyModel} object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		return Objects.nonNull(paymentNotificationBodyModel)
				&& paymentNotificationConfig.getAcceptedStatuses().contains(paymentNotificationBodyModel.getStatus())
				&& !paymentNotificationBodyModel.getClientPaymentId().endsWith(PAYMENT_OPERATOR_SUFFIX);
	}

}
