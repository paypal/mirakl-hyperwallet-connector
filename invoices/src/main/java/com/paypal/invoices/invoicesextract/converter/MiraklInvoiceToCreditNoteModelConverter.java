package com.paypal.invoices.invoicesextract.converter;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MiraklInvoiceToCreditNoteModelConverter implements Converter<MiraklInvoice, CreditNoteModel> {

	/**
	 * Method that retrieves a {@link MiraklInvoice} and returns a {@link CreditNoteModel}
	 * @param source the source object {@link MiraklInvoice}
	 * @return the returned object {@link CreditNoteModel}
	 */
	@Override
	public CreditNoteModel convert(final MiraklInvoice source) {
		if (Objects.isNull(source)) {
			return null;
		}

		//@formatter:off
		return CreditNoteModel.builder()
				.shopId(String.valueOf(source.getShopId()))
				.creditAmount(source.getTotalChargedAmount().doubleValue())
				.invoiceNumber(source.getId())
				.currencyIsoCode(source.getCurrencyIsoCode().name())
				.build();
		//@formatter:on
	}

}
