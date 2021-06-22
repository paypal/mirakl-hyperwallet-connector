package com.paypal.invoices.invoicesextract.converter;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MiraklInvoiceToInvoiceModelConverter implements Converter<MiraklInvoice, InvoiceModel> {

	@Override
	public InvoiceModel convert(final MiraklInvoice source) {

		if (Objects.isNull(source)) {
			return null;
		}

		//@formatter:off
		return InvoiceModel.builder()
				.shopId(String.valueOf(source.getShopId()))
				.transferAmount(source.getSummary().getAmountTransferred().doubleValue())
				.subscriptionAmountVat(Math.abs(source.getSummary().getTotalSubscriptionIT().doubleValue()))
				.orderCommissionAmountVat(Math.abs(source.getSummary().getTotalCommissionsIT().doubleValue()))
				.invoiceNumber(source.getId()).currencyIsoCode(source.getCurrencyIsoCode().name()).build();
		//@formatter:on
	}

}
