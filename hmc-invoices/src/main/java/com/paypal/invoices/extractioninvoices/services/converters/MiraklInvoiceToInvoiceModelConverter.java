package com.paypal.invoices.extractioninvoices.services.converters;

import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleShop;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleSummary;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class MiraklInvoiceToInvoiceModelConverter implements Converter<MiraklSellerBillingCycle, InvoiceModel> {

	@Override
	public InvoiceModel convert(final MiraklSellerBillingCycle source) {

		if (Objects.isNull(source)) {
			return null;
		}

		//@formatter:off
		return InvoiceModel.builder()
			.shopId(Optional.ofNullable(source.getShop())
				.map(MiraklSellerBillingCycleShop::getShopId)
				.map(String::valueOf)
				.orElse(null))
			.transferAmount(Optional.ofNullable(source.getAmountTransferredToSeller())
				.map(Number::doubleValue)
				.orElse(null))
			.transferAmountToOperator(Optional.ofNullable(source.getAmountTransferredToOperator())
				.map(Number::doubleValue)
				.orElse(null))
			.subscriptionAmountVat(Optional.ofNullable(source.getSummary())
				.map(MiraklSellerBillingCycleSummary::getTotalSubscriptionIT)
				.map(Number::doubleValue)
				.map(Math::abs)
				.orElse(null))
			.orderCommissionAmountVat(Optional.ofNullable(source.getSummary())
				.map(MiraklSellerBillingCycleSummary::getTotalCommissionsIT)
				.map(Number::doubleValue)
				.map(Math::abs)
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
