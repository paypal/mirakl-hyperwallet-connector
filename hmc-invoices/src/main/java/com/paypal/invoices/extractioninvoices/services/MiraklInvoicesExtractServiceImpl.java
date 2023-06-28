package com.paypal.invoices.extractioninvoices.services;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import com.paypal.invoices.extractioncommons.services.AbstractAccountingDocumentsExtractServiceImpl;
import com.paypal.invoices.extractioncommons.services.AccountingDocumentsLinksService;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MiraklInvoicesExtractServiceImpl extends AbstractAccountingDocumentsExtractServiceImpl<InvoiceModel> {

	private final Converter<MiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverter;

	public MiraklInvoicesExtractServiceImpl(final MiraklClient miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopAccountingDocumentModelConverter,
			final MailNotificationUtil invoicesMailNotificationUtil,
			final Converter<MiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverter,
			final AccountingDocumentsLinksService accountingDocumentsLinksService) {
		super(miraklShopAccountingDocumentModelConverter, miraklMarketplacePlatformOperatorApiClient,
				accountingDocumentsLinksService, invoicesMailNotificationUtil);
		this.miraklInvoiceToInvoiceModelConverter = miraklInvoiceToInvoiceModelConverter;
	}

	@Override
	protected InvoiceTypeEnum getInvoiceType() {
		return InvoiceTypeEnum.AUTO_INVOICE;
	}

	@Override
	protected Converter<MiraklInvoice, InvoiceModel> getMiraklInvoiceToAccountingModelConverter() {
		return miraklInvoiceToInvoiceModelConverter;
	}

}
