package com.paypal.invoices.invoicesextract.converter;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.springframework.stereotype.Service;

@Service
public class PayeeCreditNoteModelToHyperwalletPaymentConverter
		implements Converter<CreditNoteModel, HyperwalletPayment> {

	private static final String PURPOSE = "OTHER";

	protected final HyperwalletSDKService creditNotesHyperwalletSDKService;

	public PayeeCreditNoteModelToHyperwalletPaymentConverter(
			final HyperwalletSDKService creditNotesHyperwalletSDKService) {
		this.creditNotesHyperwalletSDKService = creditNotesHyperwalletSDKService;
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
		target.setProgramToken(
				creditNotesHyperwalletSDKService.getProgramTokenByHyperwalletProgram(source.getHyperwalletProgram()));
		target.setDestinationToken(source.getDestinationToken());
		target.setClientPaymentId(source.getInvoiceNumber());
		target.setAmount(source.getCreditAmount());
		target.setCurrency(source.getCurrencyIsoCode());
		target.setPurpose(PURPOSE);

		return target;
	}

}
