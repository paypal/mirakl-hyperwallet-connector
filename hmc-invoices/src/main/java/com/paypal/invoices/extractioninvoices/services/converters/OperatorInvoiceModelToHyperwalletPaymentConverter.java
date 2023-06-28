package com.paypal.invoices.extractioninvoices.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioninvoices.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.infrastructure.hyperwallet.services.PaymentHyperwalletSDKService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import static com.paypal.infrastructure.hyperwallet.constants.HyperWalletConstants.PAYMENT_OPERATOR_SUFFIX;

/**
 * Class to convert operator invoices from {@link InvoiceModel} to
 * {@link HyperwalletPayment}
 */
@Service
public class OperatorInvoiceModelToHyperwalletPaymentConverter implements Converter<InvoiceModel, HyperwalletPayment> {

	private static final String PURPOSE = "OTHER";

	protected final PaymentHyperwalletSDKService invoicesPaymentHyperwalletSDKService;

	protected final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig;

	protected final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration;

	public OperatorInvoiceModelToHyperwalletPaymentConverter(
			final PaymentHyperwalletSDKService invoicesPaymentHyperwalletSDKService,
			final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig,
			final HyperwalletProgramsConfiguration hyperwalletProgramsConfiguration) {
		this.invoicesPaymentHyperwalletSDKService = invoicesPaymentHyperwalletSDKService;
		this.invoicesOperatorCommissionsConfig = invoicesOperatorCommissionsConfig;
		this.hyperwalletProgramsConfiguration = hyperwalletProgramsConfiguration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletPayment convert(@NonNull final InvoiceModel source) {
		if (NumberUtils.DOUBLE_ZERO.equals(source.getTransferAmountToOperator())) {
			return null;
		}

		final var target = new HyperwalletPayment();
		target.setProgramToken(invoicesPaymentHyperwalletSDKService
				.getProgramTokenByHyperwalletProgram(source.getHyperwalletProgram()));
		target.setDestinationToken(hyperwalletProgramsConfiguration
				.getProgramConfiguration(source.getHyperwalletProgram()).getBankAccountToken());
		target.setClientPaymentId(source.getInvoiceNumber() + PAYMENT_OPERATOR_SUFFIX);
		target.setAmount(source.getTransferAmountToOperator());
		target.setCurrency(source.getCurrencyIsoCode());
		target.setPurpose(PURPOSE);

		return target;
	}

}
