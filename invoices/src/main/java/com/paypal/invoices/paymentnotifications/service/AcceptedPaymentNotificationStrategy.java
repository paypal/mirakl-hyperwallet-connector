package com.paypal.invoices.paymentnotifications.service;

import com.mirakl.client.mmp.core.MiraklMarketplacePlatformFrontOperatorApi;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.invoice.MiraklAccountingDocumentPaymentConfirmation;
import com.mirakl.client.mmp.request.invoice.MiraklConfirmAccountingDocumentPaymentRequest;
import com.paypal.infrastructure.constants.HyperWalletConstants;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.invoices.infraestructure.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.paypal.infrastructure.constants.HyperWalletConstants.PAYMENT_OPERATOR_SUFFIX;

/**
 * Strategy to update the status of an accepted payment in Mirakl
 */
@Profile({ "!qa" })
@Service
public class AcceptedPaymentNotificationStrategy implements Strategy<PaymentNotificationBodyModel, Optional<Void>> {

	@Resource
	private PaymentNotificationConfig paymentNotificationConfig;

	@Resource
	private MiraklMarketplacePlatformFrontOperatorApi miraklMarketplacePlatformFrontOperatorApi;

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
		miraklMarketplacePlatformFrontOperatorApi
				.confirmAccountingDocumentPayment(createPaymentRequest(paymentNotificationBodyModel));

		return Optional.empty();
	}

	protected MiraklConfirmAccountingDocumentPaymentRequest createPaymentRequest(
			final PaymentNotificationBodyModel paymentNotificationBodyModel) {
		final MiraklAccountingDocumentPaymentConfirmation miraklAccountingDocumentPaymentConfirmation = new MiraklAccountingDocumentPaymentConfirmation();
		miraklAccountingDocumentPaymentConfirmation.setAmount(new BigDecimal(paymentNotificationBodyModel.getAmount()));
		miraklAccountingDocumentPaymentConfirmation.setCurrencyIsoCode(
				EnumUtils.getEnum(MiraklIsoCurrencyCode.class, paymentNotificationBodyModel.getCurrency(), null));
		miraklAccountingDocumentPaymentConfirmation.setInvoiceId(paymentNotificationBodyModel.getClientPaymentId());
		miraklAccountingDocumentPaymentConfirmation
				.setTransactionDate(DateUtil.convertToDate(paymentNotificationBodyModel.getCreatedOn(),
						HyperWalletConstants.HYPERWALLET_DATE_FORMAT, DateUtil.TIME_UTC));

		return new MiraklConfirmAccountingDocumentPaymentRequest(List.of(miraklAccountingDocumentPaymentConfirmation));
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
				&& paymentNotificationConfig.getAcceptedStatuses().contains(paymentNotificationBodyModel.getStatus())
				&& !paymentNotificationBodyModel.getClientPaymentId().endsWith(PAYMENT_OPERATOR_SUFFIX);
	}

}
