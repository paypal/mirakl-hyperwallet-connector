package com.paypal.invoices.extractioncommons.services;

import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklPayOutState;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleSeekSort;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycles;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklGetSellerBillingCyclesRequest;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
		final List<MiraklSellerBillingCycle> billingCycles = getBillingCyclesForDateAndType(delta, includePaid);

		//@formatter:off
		return billingCycles.stream()
			.map(getMiraklInvoiceToAccountingModelConverter()::convert)
			.toList();
        //@formatter:on
	}

	@NonNull
	protected MiraklGetSellerBillingCyclesRequest createAccountingDocumentRequest(final Date delta) {
		return createAccountingDocumentRequest(delta, false);
	}

	@NonNull
	protected MiraklGetSellerBillingCyclesRequest createAccountingDocumentRequest(final Date delta,
			final boolean includePaid) {
		final MiraklGetSellerBillingCyclesRequest request = new MiraklGetSellerBillingCyclesRequest();
		request.setStartDate(delta.toInstant());
		if (!includePaid) {
			request.setPayOutStates(Collections.singleton(MiraklPayOutState.TO_PAY));
		}
		request.setLimit(MIRAKL_MAX_RESULTS_PER_PAGE);
		request.setOrderBy(MiraklSellerBillingCycleSeekSort.DATE_CREATED.asc());

		return request;
	}

	protected List<MiraklSellerBillingCycle> getBillingCyclesForDateAndType(final Date delta) {
		return getBillingCyclesForDateAndType(delta, false);
	}

	protected List<MiraklSellerBillingCycle> getBillingCyclesForDateAndType(final Date delta,
			final boolean includePaid) {

		final List<MiraklSellerBillingCycle> billingCycles = new ArrayList<>();

		final MiraklGetSellerBillingCyclesRequest request = createAccountingDocumentRequest(delta, includePaid);
		String pageToken = null;
		do {
			request.setPageToken(pageToken);
			final MiraklSellerBillingCycles received = miraklMarketplacePlatformOperatorApiClient
				.getSellerBillingCycles(request);
			billingCycles.addAll(received.getData());
			pageToken = received.getNextPageToken();
		}
		while (pageToken != null);

		return billingCycles;
	}

	@Override
	public Collection<T> extractAccountingDocuments(final List<String> ids) {
		final List<MiraklSellerBillingCycle> billingCycles = getBillingCyclesForDateAndType(
				getTimeRangeForFindByIdInvoices());

		//@formatter:off
		return billingCycles.stream()
			.filter(billingCycle -> ids.contains(billingCycle.getId().toString()))
			.map(getMiraklInvoiceToAccountingModelConverter()::convert)
			.toList();
        //@formatter:on
	}

	private Date getTimeRangeForFindByIdInvoices() {
		return Date.from(LocalDateTime.now().minusMinutes(maxNumberOfDaysForInvoiceIdSearch).toInstant(ZoneOffset.UTC));
	}

	protected abstract InvoiceTypeEnum getInvoiceType();

	protected abstract Converter<MiraklSellerBillingCycle, T> getMiraklInvoiceToAccountingModelConverter();

}
