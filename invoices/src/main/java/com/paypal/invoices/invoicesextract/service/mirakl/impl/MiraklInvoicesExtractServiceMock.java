package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.invoices.infraestructure.testing.TestingInvoicesSessionDataHelper;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile({ "!prod" })
@Service("miraklInvoicesExtractServiceImpl")
public class MiraklInvoicesExtractServiceMock extends MiraklInvoicesExtractServiceImpl {

	protected final TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelper;

	public MiraklInvoicesExtractServiceMock(
			final MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient,
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopAccountingDocumentModelConverter,
			final MailNotificationUtil invoicesMailNotificationUtil,
			final Converter<HMCMiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverter,
			final TestingInvoicesSessionDataHelper testingInvoicesSessionDataHelper) {
		super(miraklMarketplacePlatformOperatorApiClient, miraklShopAccountingDocumentModelConverter,
				invoicesMailNotificationUtil, miraklInvoiceToInvoiceModelConverter);
		this.testingInvoicesSessionDataHelper = testingInvoicesSessionDataHelper;
	}

	@Override
	protected List<InvoiceModel> getAccountingDocument(final Date delta) {
		//@formatter:off
        return Stream.ofNullable(testingInvoicesSessionDataHelper.getInvoices())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        //@formatter:on
	}

}
