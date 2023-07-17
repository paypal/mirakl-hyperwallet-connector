package com.paypal.invoices.extractioncreditnotes.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import com.paypal.infrastructure.hyperwallet.services.PaymentHyperwalletSDKService;
import org.springframework.stereotype.Service;

@Service
public class PayeeCreditNoteModelToHyperwalletPaymentConverter
		implements Converter<CreditNoteModel, HyperwalletPayment> {

	private static final String PURPOSE = "OTHER";

	protected final PaymentHyperwalletSDKService creditNotesPaymentHyperwalletSDKService;

	public PayeeCreditNoteModelToHyperwalletPaymentConverter(
			final PaymentHyperwalletSDKService creditNotesPaymentHyperwalletSDKService) {
		this.creditNotesPaymentHyperwalletSDKService = creditNotesPaymentHyperwalletSDKService;
	}

	/**
	 * Method that retrieves a {@link CreditNoteModel} and returns a
	 * {@link HyperwalletPayment}
	 * @param source the source object {@link CreditNoteModel}
	 * @return the returned object {@link HyperwalletPayment}
	 */
	@Override
	public HyperwalletPayment convert(final CreditNoteModel source) {
		final HyperwalletPayment target = new HyperwalletPayment();
		target.setProgramToken(creditNotesPaymentHyperwalletSDKService
				.getProgramTokenByHyperwalletProgram(source.getHyperwalletProgram()));
		target.setDestinationToken(source.getDestinationToken());
		target.setClientPaymentId(source.getInvoiceNumber());
		target.setAmount(source.getCreditAmount());
		target.setCurrency(source.getCurrencyIsoCode());
		target.setPurpose(PURPOSE);

		return target;
	}

}
