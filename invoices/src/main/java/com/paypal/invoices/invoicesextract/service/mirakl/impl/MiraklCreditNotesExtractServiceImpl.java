package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MiraklCreditNotesExtractServiceImpl
		extends AbstractAccountingDocumentsExtractServiceImpl<CreditNoteModel> {

	private final Converter<MiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverter;

	public MiraklCreditNotesExtractServiceImpl(
			final MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
			final Converter<MiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverter,
			final MailNotificationUtil invoicesMailNotificationUtil) {
		super(miraklShopToAccountingModelConverter, miraklMarketplacePlatformOperatorApiClient,
				invoicesMailNotificationUtil);
		this.miraklInvoiceToCreditNoteModelConverter = miraklInvoiceToCreditNoteModelConverter;
	}

	@NonNull
	protected List<CreditNoteModel> associateBillingDocumentsWithTokens(final List<CreditNoteModel> invoices,
			final Map<String, Pair<String, String>> mapShopDestinationToken) {
		final List<CreditNoteModel> creditNotesFiltered = filterOnlyMappableDocuments(invoices,
				mapShopDestinationToken.keySet());
		//@formatter:off
        return creditNotesFiltered.stream()
                .map(invoiceModel -> invoiceModel.toBuilder()
                        .destinationToken(mapShopDestinationToken.get(invoiceModel.getShopId()).getLeft())
                        .hyperwalletProgram(mapShopDestinationToken.get(invoiceModel.getShopId()).getRight())
                        .build())
                .collect(Collectors.toList());
        //@formatter:on
	}

	@Override
	protected List<CreditNoteModel> getAccountingDocuments(final Date delta) {
		final List<HMCMiraklInvoice> invoices = getInvoicesForDateAndType(delta, InvoiceTypeEnum.MANUAL_CREDIT);

		//@formatter:off
        return invoices.stream()
                .map(miraklInvoiceToCreditNoteModelConverter::convert)
                .collect(Collectors.toList());
        //@formatter:on
	}

}
