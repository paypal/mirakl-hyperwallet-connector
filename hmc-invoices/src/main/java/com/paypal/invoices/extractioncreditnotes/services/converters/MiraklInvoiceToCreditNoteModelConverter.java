package com.paypal.invoices.extractioncreditnotes.services.converters;

import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleShop;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class MiraklInvoiceToCreditNoteModelConverter implements Converter<MiraklSellerBillingCycle, CreditNoteModel> {

	@Override
	public CreditNoteModel convert(final MiraklSellerBillingCycle source) {
		if (Objects.isNull(source)) {
			return null;
		}

		//@formatter:off
		return CreditNoteModel.builder()
			.shopId(Optional.ofNullable(source.getShop())
				.map(MiraklSellerBillingCycleShop::getShopId)
				.map(String::valueOf)
				.orElse(null))
			.creditAmount(Optional.ofNullable(source.getAmountTransferredToSeller())
					.map(Number::doubleValue)
					.orElse(null))
			.invoiceNumber(Optional.ofNullable(source.getId())
					.map(String::valueOf)
					.orElse(null))
			.currencyIsoCode(Optional.ofNullable(source.getCurrencyIsoCode())
					.map(Enum::name)
					.orElse(null))
			.build();
		//@formatter:on
	}

}
