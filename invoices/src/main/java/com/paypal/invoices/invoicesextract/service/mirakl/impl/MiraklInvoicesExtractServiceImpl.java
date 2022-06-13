package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({ "prod" })
public class MiraklInvoicesExtractServiceImpl extends AbstractAccountingDocumentsExtractServiceImpl<InvoiceModel> {

	private final Converter<HMCMiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverter;

	public MiraklInvoicesExtractServiceImpl(
			final MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopAccountingDocumentModelConverter,
			final MailNotificationUtil invoicesMailNotificationUtil,
			final Converter<HMCMiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverter,
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
	protected Converter<HMCMiraklInvoice, InvoiceModel> getMiraklInvoiceToAccountingModelConverter() {
		return miraklInvoiceToInvoiceModelConverter;
	}

}
