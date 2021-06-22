package com.paypal.invoices.invoicesextract.converter;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Class to convert payee invoices from {@link InvoiceModel} to {@link HyperwalletPayment}
 */
@Service
public class PayeeInvoiceModelToHyperwalletPaymentConverter implements Converter<InvoiceModel, HyperwalletPayment> {

	private static final String PURPOSE = "OTHER";

	protected final HyperwalletSDKService invoicesHyperwalletSDKService;

	public PayeeInvoiceModelToHyperwalletPaymentConverter(final HyperwalletSDKService invoicesHyperwalletSDKService) {
		this.invoicesHyperwalletSDKService = invoicesHyperwalletSDKService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletPayment convert(@NonNull final InvoiceModel source) {
		final HyperwalletPayment target = new HyperwalletPayment();
		target.setProgramToken(
				invoicesHyperwalletSDKService.getProgramTokenByHyperwalletProgram(source.getHyperwalletProgram()));
		target.setDestinationToken(source.getDestinationToken());
		target.setClientPaymentId(source.getInvoiceNumber());
		target.setAmount(source.getTransferAmount());
		target.setCurrency(source.getCurrencyIsoCode());
		target.setPurpose(PURPOSE);

		return target;
	}

}
