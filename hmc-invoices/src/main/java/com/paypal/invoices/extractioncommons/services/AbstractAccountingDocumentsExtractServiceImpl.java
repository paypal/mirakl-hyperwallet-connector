package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentPaymentStatus;
import com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentType;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoices;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.request.payment.invoice.MiraklAccountingDocumentState;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
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

import static com.paypal.infrastructure.hyperwallet.constants.HyperWalletConstants.MIRAKL_MAX_RESULTS_PER_PAGE;

@Slf4j
@Service
public abstract class AbstractAccountingDocumentsExtractServiceImpl<T extends AccountingDocumentModel>
		implements MiraklAccountingDocumentExtractService<T> {

	protected final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter;

	protected final MiraklClient miraklMarketplacePlatformOperatorApiClient;

	protected final AccountingDocumentsLinksService accountingDocumentsLinksService;

	protected final MailNotificationUtil invoicesMailNotificationUtil;

	@Value("${hmc.jobs.settings.search-invoices-maxdays}")
	protected int maxNumberOfDaysForInvoiceIdSearch;

	protected AbstractAccountingDocumentsExtractServiceImpl(
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
			final MiraklClient miraklMarketplacePlatformOperatorApiClient,
			final AccountingDocumentsLinksService accountingDocumentsLinksService,
			final MailNotificationUtil invoicesMailNotificationUtil) {
		this.miraklShopToAccountingModelConverter = miraklShopToAccountingModelConverter;
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
		this.accountingDocumentsLinksService = accountingDocumentsLinksService;
		this.invoicesMailNotificationUtil = invoicesMailNotificationUtil;
	}

	@Override
	public List<T> extractAccountingDocuments(final Date delta) {
		return extractAccountingDocuments(delta, false);
	}

	@Override
	public List<T> extractAccountingDocuments(final Date delta, final boolean includePaid) {
		final List<MiraklInvoice> invoices = getInvoicesForDateAndType(delta, getInvoiceType(), includePaid);

		//@formatter:off
		return invoices.stream()
				.map(getMiraklInvoiceToAccountingModelConverter()::convert)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@NonNull
	protected MiraklGetInvoicesRequest createAccountingDocumentRequest(final Date delta,
			final InvoiceTypeEnum invoiceType) {
		return createAccountingDocumentRequest(delta, invoiceType, false);
	}

	@NonNull
	protected MiraklGetInvoicesRequest createAccountingDocumentRequest(final Date delta,
			final InvoiceTypeEnum invoiceType, final boolean includePaid) {
		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = new MiraklGetInvoicesRequest();
		miraklGetInvoicesRequest.setStartDate(delta);
		if (!includePaid) {
			miraklGetInvoicesRequest.setPaymentStatus(MiraklAccountingDocumentPaymentStatus.PENDING);
		}
		miraklGetInvoicesRequest.addState(MiraklAccountingDocumentState.COMPLETE);
		miraklGetInvoicesRequest.setType(EnumUtils.getEnum(MiraklAccountingDocumentType.class, invoiceType.name()));
		miraklGetInvoicesRequest.setMax(MIRAKL_MAX_RESULTS_PER_PAGE);

		return miraklGetInvoicesRequest;
	}

	protected List<MiraklInvoice> getInvoicesForDateAndType(final Date delta, final InvoiceTypeEnum invoiceType) {
		return getInvoicesForDateAndType(delta, invoiceType, false);
	}

	protected List<MiraklInvoice> getInvoicesForDateAndType(final Date delta, final InvoiceTypeEnum invoiceType,
			final boolean includePaid) {

		final List<MiraklInvoice> invoices = new ArrayList<>();

		int offset = 0;
		final MiraklGetInvoicesRequest accountingDocumentRequest = createAccountingDocumentRequest(delta, invoiceType,
				includePaid);
		while (true) {
			accountingDocumentRequest.setOffset(offset);
			final MiraklInvoices receivedInvoices = miraklMarketplacePlatformOperatorApiClient
					.getInvoices(accountingDocumentRequest);
			invoices.addAll(receivedInvoices.getInvoices());

			if (receivedInvoices.getTotalCount() <= invoices.size()) {
				break;
			}
			offset += MIRAKL_MAX_RESULTS_PER_PAGE;
		}

		return invoices;
	}

	@Override
	public Collection<T> extractAccountingDocuments(final List<String> ids) {
		final List<MiraklInvoice> invoices = getInvoicesForDateAndType(getTimeRangeForFindByIdInvoices(),
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

	protected abstract Converter<MiraklInvoice, T> getMiraklInvoiceToAccountingModelConverter();

}
