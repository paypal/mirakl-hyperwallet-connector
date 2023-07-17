package com.paypal.invoices.extractioninvoices.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.infrastructure.hyperwallet.services.PaymentHyperwalletSDKService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Class to convert payee invoices from {@link InvoiceModel} to {@link HyperwalletPayment}
 */
@Service
public class PayeeInvoiceModelToHyperwalletPaymentConverter implements Converter<InvoiceModel, HyperwalletPayment> {

	private static final String PURPOSE = "OTHER";

	protected final PaymentHyperwalletSDKService invoicesPaymentHyperwalletSDKService;

	public PayeeInvoiceModelToHyperwalletPaymentConverter(
			final PaymentHyperwalletSDKService invoicesPaymentHyperwalletSDKService) {
		this.invoicesPaymentHyperwalletSDKService = invoicesPaymentHyperwalletSDKService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletPayment convert(@NonNull final InvoiceModel source) {
		final HyperwalletPayment target = new HyperwalletPayment();
		target.setProgramToken(invoicesPaymentHyperwalletSDKService
				.getProgramTokenByHyperwalletProgram(source.getHyperwalletProgram()));
		target.setDestinationToken(source.getDestinationToken());
		target.setClientPaymentId(source.getInvoiceNumber());
		target.setAmount(source.getTransferAmount());
		target.setCurrency(source.getCurrencyIsoCode());
		target.setPurpose(PURPOSE);

		return target;
	}

}
