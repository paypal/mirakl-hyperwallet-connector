package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoices;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionLogs;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.documents.MiraklDocumentsConfigurations;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.additionalfield.MiraklGetAdditionalFieldRequest;
import com.mirakl.client.mmp.operator.request.documents.MiraklGetDocumentsConfigurationRequest;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.invoice.MiraklConfirmAccountingDocumentPaymentRequest;
import com.mirakl.client.mmp.request.payment.MiraklGetTransactionLogsRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklDeleteShopDocumentRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklDownloadShopsDocumentsRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mirakl.client.filter.IgnoredShopsFilter;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DirectMiraklClient implements MiraklClient {

	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	private final IgnoredShopsFilter ignoredShopsFilter;

	private final MiraklApiClientConfig config;

	public DirectMiraklClient(final MiraklApiClientConfig config, final IgnoredShopsFilter ignoredShopsFilter) {
		this.config = config;
		this.ignoredShopsFilter = ignoredShopsFilter;
		reloadHttpConfiguration();
	}

	@Override
	public MiraklVersion getVersion() {
		return miraklMarketplacePlatformOperatorApiClient.getVersion();
	}

	@Override
	public MiraklShops getShops(final MiraklGetShopsRequest request) {
		final MiraklShops shops = getUnfilteredMiraklShops(request);

		return ignoredShopsFilter.filterIgnoredShops(shops);
	}

	@Override
	public MiraklUpdatedShops updateShops(final MiraklUpdateShopsRequest miraklUpdateShopsRequest) {
		return miraklMarketplacePlatformOperatorApiClient.updateShops(miraklUpdateShopsRequest);
	}

	@Override
	public List<MiraklFrontOperatorAdditionalField> getAdditionalFields(
			final MiraklGetAdditionalFieldRequest miraklGetAdditionalFieldRequest) {
		return miraklMarketplacePlatformOperatorApiClient.getAdditionalFields(miraklGetAdditionalFieldRequest);
	}

	@Override
	public MiraklInvoices getInvoices(final MiraklGetInvoicesRequest accountingDocumentRequest) {
		return miraklMarketplacePlatformOperatorApiClient.getInvoices(accountingDocumentRequest);
	}

	@SuppressWarnings("java:S1874")
	@Override
	public MiraklTransactionLogs getTransactionLogs(
			final MiraklGetTransactionLogsRequest miraklGetTransactionLogsRequest) {
		return miraklMarketplacePlatformOperatorApiClient.getTransactionLogs(miraklGetTransactionLogsRequest);
	}

	@Override
	public MiraklDocumentsConfigurations getDocumentsConfiguration(
			final MiraklGetDocumentsConfigurationRequest miraklGetDocumentsConfigurationRequest) {
		return miraklMarketplacePlatformOperatorApiClient
				.getDocumentsConfiguration(miraklGetDocumentsConfigurationRequest);
	}

	@Override
	public List<MiraklShopDocument> getShopDocuments(
			final MiraklGetShopDocumentsRequest getShopBusinessStakeholderDocumentsRequest) {
		return miraklMarketplacePlatformOperatorApiClient.getShopDocuments(getShopBusinessStakeholderDocumentsRequest);
	}

	@Override
	public FileWrapper downloadShopsDocuments(
			final MiraklDownloadShopsDocumentsRequest miraklDownloadShopsDocumentsRequest) {
		return miraklMarketplacePlatformOperatorApiClient.downloadShopsDocuments(miraklDownloadShopsDocumentsRequest);
	}

	@Override
	public void deleteShopDocument(final MiraklDeleteShopDocumentRequest miraklDeleteShopDocumentRequest) {
		miraklMarketplacePlatformOperatorApiClient.deleteShopDocument(miraklDeleteShopDocumentRequest);
	}

	@Override
	public void confirmAccountingDocumentPayment(
			final MiraklConfirmAccountingDocumentPaymentRequest paymentConfirmationRequest) {
		miraklMarketplacePlatformOperatorApiClient.confirmAccountingDocumentPayment(paymentConfirmationRequest);
	}

	@Override
	public void reloadHttpConfiguration() {
		this.miraklMarketplacePlatformOperatorApiClient = new MiraklMarketplacePlatformOperatorApiClient(
				config.getEnvironment(), new MiraklCredential(config.getOperatorApiKey()));
	}

	protected MiraklShops getUnfilteredMiraklShops(final MiraklGetShopsRequest request) {
		return miraklMarketplacePlatformOperatorApiClient.getShops(request);
	}

}
