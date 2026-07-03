package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.core.security.MiraklBearerToken;
import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycles;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.domain.version.MiraklVersion;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.documents.MiraklDocumentsConfigurations;
import com.mirakl.client.mmp.operator.domain.payment.MiraklTransactionsLines;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.additionalfield.MiraklGetAdditionalFieldRequest;
import com.mirakl.client.mmp.operator.request.documents.MiraklGetDocumentsConfigurationRequest;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklConfirmSellerBillingCyclePaymentRequest;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklGetSellerBillingCyclesRequest;
import com.mirakl.client.mmp.operator.request.payment.transaction.MiraklTransactionLineRequest;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklDeleteShopDocumentRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklDownloadShopsDocumentsRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mirakl.client.filter.ShopsFilter;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DirectMiraklClient implements MiraklClient {

	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	private final List<ShopsFilter> shopsFilter;

	private final MiraklApiClientConfig config;

	public DirectMiraklClient(final MiraklApiClientConfig config, final List<ShopsFilter> shopsFilter) {
		this.config = config;
		this.shopsFilter = shopsFilter;
		reloadHttpConfiguration();
	}

	@Override
	public MiraklVersion getVersion() {
		return miraklMarketplacePlatformOperatorApiClient.getVersion();
	}

	@Override
	public MiraklShops getShops(final MiraklGetShopsRequest request) {
		final MiraklShops shops = getUnfilteredMiraklShops(request);

		shopsFilter.forEach(filter -> filter.filterShops(shops));

		return shops;
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
	public MiraklSellerBillingCycles getSellerBillingCycles(
			final MiraklGetSellerBillingCyclesRequest sellerBillingCyclesRequest) {
		return miraklMarketplacePlatformOperatorApiClient.getSellerBillingCycles(sellerBillingCyclesRequest);
	}

	@Override
	public MiraklTransactionsLines getTransactionLines(
			final MiraklTransactionLineRequest miraklTransactionLineRequest) {
		return miraklMarketplacePlatformOperatorApiClient.getTransactionLines(miraklTransactionLineRequest);
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
	public void confirmSellerBillingCyclePayment(
			final MiraklConfirmSellerBillingCyclePaymentRequest paymentConfirmationRequest) {
		miraklMarketplacePlatformOperatorApiClient.confirmSellerBillingCyclePayment(paymentConfirmationRequest);
	}

	@Override
	public void reloadHttpConfiguration() {
		final var credential = StringUtils.isNotBlank(config.getOperatorAccessToken())
				? new MiraklBearerToken(config.getOperatorAccessToken())
				: new MiraklCredential(config.getOperatorApiKey());
		this.miraklMarketplacePlatformOperatorApiClient = new MiraklMarketplacePlatformOperatorApiClient(
				config.getEnvironment(), credential);
	}

	protected MiraklShops getUnfilteredMiraklShops(final MiraklGetShopsRequest request) {
		return miraklMarketplacePlatformOperatorApiClient.getShops(request);
	}

}
