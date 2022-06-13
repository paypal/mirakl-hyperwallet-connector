package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentPaymentStatus;
import com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentType;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.request.payment.invoice.MiraklAccountingDocumentState;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoices;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import com.paypal.invoices.invoicesextract.service.hmc.AccountingDocumentsLinksService;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.paypal.infrastructure.constants.HyperWalletConstants.MIRAKL_MAX_RESULTS_PER_PAGE;

@Slf4j
@Service
public abstract class AbstractAccountingDocumentsExtractServiceImpl<T extends AccountingDocumentModel>
		implements MiraklAccountingDocumentExtractService<T> {

	protected final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter;

	protected final MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient;

	protected final AccountingDocumentsLinksService accountingDocumentsLinksService;

	protected final MailNotificationUtil invoicesMailNotificationUtil;

	@Value("${invoices.searchinvoices.maxdays}")
	protected int maxNumberOfDaysForInvoiceIdSearch;

	protected AbstractAccountingDocumentsExtractServiceImpl(
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
			final MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClient,
			AccountingDocumentsLinksService accountingDocumentsLinksService,
			final MailNotificationUtil invoicesMailNotificationUtil) {
		this.miraklShopToAccountingModelConverter = miraklShopToAccountingModelConverter;
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
		this.accountingDocumentsLinksService = accountingDocumentsLinksService;
		this.invoicesMailNotificationUtil = invoicesMailNotificationUtil;
	}

	@Override
	public List<T> extractAccountingDocuments(final Date delta) {
		final List<HMCMiraklInvoice> invoices = getInvoicesForDateAndType(delta, getInvoiceType());

		//@formatter:off
		return invoices.stream()
				.map(getMiraklInvoiceToAccountingModelConverter()::convert)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@NonNull
	protected MiraklGetInvoicesRequest createAccountingDocumentRequest(final Date delta,
			final InvoiceTypeEnum invoiceType) {
		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = new MiraklGetInvoicesRequest();
		miraklGetInvoicesRequest.setStartDate(delta);
		miraklGetInvoicesRequest.setPaymentStatus(MiraklAccountingDocumentPaymentStatus.PENDING);
		miraklGetInvoicesRequest.addState(MiraklAccountingDocumentState.COMPLETE);
		miraklGetInvoicesRequest.setType(EnumUtils.getEnum(MiraklAccountingDocumentType.class, invoiceType.name()));
		miraklGetInvoicesRequest.setMax(MIRAKL_MAX_RESULTS_PER_PAGE);

		return miraklGetInvoicesRequest;
	}

	protected List<HMCMiraklInvoice> getInvoicesForDateAndType(final Date delta, final InvoiceTypeEnum invoiceType) {
		final List<HMCMiraklInvoice> invoices = new ArrayList<>();

		int offset = 0;
		final MiraklGetInvoicesRequest accountingDocumentRequest = createAccountingDocumentRequest(delta, invoiceType);
		while (true) {
			accountingDocumentRequest.setOffset(offset);
			final HMCMiraklInvoices receivedInvoices = miraklMarketplacePlatformOperatorApiClient
					.getInvoices(accountingDocumentRequest);
			invoices.addAll(receivedInvoices.getHmcInvoices());

			if (receivedInvoices.getTotalCount() <= invoices.size()) {
				break;
			}
			offset += MIRAKL_MAX_RESULTS_PER_PAGE;
		}

		return invoices;
	}

	@Override
	public Collection<T> extractAccountingDocuments(List<String> ids) {
		final List<HMCMiraklInvoice> invoices = getInvoicesForDateAndType(getTimeRangeForFindByIdInvoices(),
				getInvoiceType());

		//@formatter:off
		return invoices.stream()
				.filter(invoice -> ids.contains(invoice.getId()))
				.map(getMiraklInvoiceToAccountingModelConverter()::convert)
				.collect(Collectors.toList());
		//@formatter:on
	}

	private Date getTimeRangeForFindByIdInvoices() {
		return Date.from(LocalDateTime.now().minusMinutes(maxNumberOfDaysForInvoiceIdSearch).toInstant(ZoneOffset.UTC));
	}

	protected abstract InvoiceTypeEnum getInvoiceType();

	protected abstract Converter<HMCMiraklInvoice, T> getMiraklInvoiceToAccountingModelConverter();

}
