package com.paypal.invoices.invoicesextract.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MiraklInvoiceToInvoiceModelConverter implements Converter<HMCMiraklInvoice, InvoiceModel> {

	@Override
	public InvoiceModel convert(final HMCMiraklInvoice source) {

		if (Objects.isNull(source)) {
			return null;
		}

		//@formatter:off
		return InvoiceModel.builder()
				.shopId(String.valueOf(source.getShopId()))
				.transferAmount(source.getSummary().getAmountTransferred().doubleValue())
				.transferAmountToOperator(source.getSummary().getAmountTransferredToOperator().doubleValue())
				.subscriptionAmountVat(Math.abs(source.getSummary().getTotalSubscriptionIT().doubleValue()))
				.orderCommissionAmountVat(Math.abs(source.getSummary().getTotalCommissionsIT().doubleValue()))
				.invoiceNumber(source.getId()).currencyIsoCode(source.getCurrencyIsoCode().name()).build();
		//@formatter:on
	}

}
