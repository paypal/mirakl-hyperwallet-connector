package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile({ "prod" })
public class MiraklInvoicesExtractServiceImpl extends AbstractAccountingDocumentsExtractServiceImpl<InvoiceModel> {

	private final Converter<HMCMiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverter;

	public MiraklInvoicesExtractServiceImpl(
			final MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopAccountingDocumentModelConverter,
			final MailNotificationUtil invoicesMailNotificationUtil,
			final Converter<HMCMiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverter) {
		super(miraklShopAccountingDocumentModelConverter, miraklMarketplacePlatformOperatorApiClient,
				invoicesMailNotificationUtil);
		this.miraklInvoiceToInvoiceModelConverter = miraklInvoiceToInvoiceModelConverter;
	}

	@NonNull
	protected List<InvoiceModel> associateBillingDocumentsWithTokens(final List<InvoiceModel> invoices,
			final Map<String, Pair<String, String>> mapShopDestinationToken) {

		final List<InvoiceModel> filteredInvoices = filterOnlyMappableDocuments(invoices,
				mapShopDestinationToken.keySet());
		//@formatter:off
        return filteredInvoices.stream()
                .map(invoiceModel -> invoiceModel.toBuilder()
                        .destinationToken(mapShopDestinationToken.get(invoiceModel.getShopId()).getLeft())
                        .hyperwalletProgram(mapShopDestinationToken.get(invoiceModel.getShopId()).getRight())
                        .build())
                .collect(Collectors.toList());
        //@formatter:on
	}

	@Override
	protected List<InvoiceModel> getAccountingDocument(final Date delta) {
		final MiraklGetInvoicesRequest accountingDocumentRequest = createAccountingDocumentRequest(delta,
				InvoiceTypeEnum.AUTO_INVOICE);
		final HMCMiraklInvoices invoices = miraklMarketplacePlatformOperatorApiClient
				.getInvoices(accountingDocumentRequest);

		//@formatter:off
		return Optional.ofNullable(Optional.ofNullable(invoices).orElse(new HMCMiraklInvoices()).getHmcInvoices())
						.orElse(List.of())
						.stream()
						.map(miraklInvoiceToInvoiceModelConverter::convert)
						.collect(Collectors.toList());
		//@formatter:on
	}

}
