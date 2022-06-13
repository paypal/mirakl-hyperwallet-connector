package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MiraklCreditNotesExtractServiceImpl
		extends AbstractAccountingDocumentsExtractServiceImpl<CreditNoteModel> {

	private final Converter<HMCMiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverter;

	public MiraklCreditNotesExtractServiceImpl(
			final MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
			final Converter<HMCMiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverter,
			final MailNotificationUtil invoicesMailNotificationUtil,
			final AccountingDocumentsLinksService accountingDocumentsLinksService) {
		super(miraklShopToAccountingModelConverter, miraklMarketplacePlatformOperatorApiClient,
				accountingDocumentsLinksService, invoicesMailNotificationUtil);
		this.miraklInvoiceToCreditNoteModelConverter = miraklInvoiceToCreditNoteModelConverter;
	}

	@Override
	protected InvoiceTypeEnum getInvoiceType() {
		return InvoiceTypeEnum.MANUAL_CREDIT;
	}

	@Override
	protected Converter<HMCMiraklInvoice, CreditNoteModel> getMiraklInvoiceToAccountingModelConverter() {
		return miraklInvoiceToCreditNoteModelConverter;
	}

}
