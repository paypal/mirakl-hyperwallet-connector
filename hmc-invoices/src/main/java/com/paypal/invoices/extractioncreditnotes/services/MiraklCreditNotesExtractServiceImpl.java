package com.paypal.invoices.extractioncreditnotes.services;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.services.AbstractAccountingDocumentsExtractServiceImpl;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import com.paypal.invoices.extractioncommons.services.AccountingDocumentsLinksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MiraklCreditNotesExtractServiceImpl
		extends AbstractAccountingDocumentsExtractServiceImpl<CreditNoteModel> {

	private final Converter<MiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverter;

	public MiraklCreditNotesExtractServiceImpl(final MiraklClient miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
			final Converter<MiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverter,
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
	protected Converter<MiraklInvoice, CreditNoteModel> getMiraklInvoiceToAccountingModelConverter() {
		return miraklInvoiceToCreditNoteModelConverter;
	}

}
