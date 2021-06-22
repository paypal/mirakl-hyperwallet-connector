package com.paypal.invoices.invoicesextract.converter;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.infraestructure.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.paypal.infrastructure.constants.HyperWalletConstants.PAYMENT_OPERATOR_SUFFIX;

/**
 * Class to convert operator invoices from {@link InvoiceModel} to
 * {@link HyperwalletPayment}
 */
@Service
public class OperatorInvoiceModelToHyperwalletPaymentConverter implements Converter<InvoiceModel, HyperwalletPayment> {

	private static final String PURPOSE = "OTHER";

	protected final HyperwalletSDKService invoicesHyperwalletSDKService;

	protected final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig;

	public OperatorInvoiceModelToHyperwalletPaymentConverter(final HyperwalletSDKService invoicesHyperwalletSDKService,
			final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig) {
		this.invoicesHyperwalletSDKService = invoicesHyperwalletSDKService;
		this.invoicesOperatorCommissionsConfig = invoicesOperatorCommissionsConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletPayment convert(@NonNull final InvoiceModel source) {
		final var amount = BigDecimal
				.valueOf(Optional.ofNullable(source.getSubscriptionAmountVat()).orElse(0.0D)
						+ Optional.ofNullable(source.getOrderCommissionAmountVat()).orElse(0.0))
				.setScale(2, RoundingMode.HALF_EVEN).doubleValue();

		if (NumberUtils.DOUBLE_ZERO.equals(amount)) {
			return null;
		}

		final var target = new HyperwalletPayment();
		target.setProgramToken(
				invoicesHyperwalletSDKService.getProgramTokenByHyperwalletProgram(source.getHyperwalletProgram()));
		target.setDestinationToken(
				invoicesOperatorCommissionsConfig.getBankAccountToken(source.getHyperwalletProgram()));
		target.setClientPaymentId(source.getInvoiceNumber() + PAYMENT_OPERATOR_SUFFIX);
		target.setAmount(amount);
		target.setCurrency(source.getCurrencyIsoCode());
		target.setPurpose(PURPOSE);

		return target;
	}

}
