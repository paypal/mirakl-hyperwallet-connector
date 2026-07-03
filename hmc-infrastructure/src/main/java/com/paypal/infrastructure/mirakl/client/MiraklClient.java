package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycles;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.domain.version.MiraklVersion;
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
import java.util.List;

public interface MiraklClient {

	MiraklVersion getVersion();

	MiraklShops getShops(MiraklGetShopsRequest request);

	MiraklUpdatedShops updateShops(MiraklUpdateShopsRequest miraklUpdateShopsRequest);

	List<MiraklFrontOperatorAdditionalField> getAdditionalFields(
			MiraklGetAdditionalFieldRequest miraklGetAdditionalFieldRequest);

	MiraklSellerBillingCycles getSellerBillingCycles(MiraklGetSellerBillingCyclesRequest sellerBillingCyclesRequest);

	MiraklTransactionsLines getTransactionLines(MiraklTransactionLineRequest miraklGetTransactionLinesRequest);

	MiraklDocumentsConfigurations getDocumentsConfiguration(
			MiraklGetDocumentsConfigurationRequest miraklGetDocumentsConfigurationRequest);

	List<MiraklShopDocument> getShopDocuments(MiraklGetShopDocumentsRequest getShopBusinessStakeholderDocumentsRequest);

	FileWrapper downloadShopsDocuments(MiraklDownloadShopsDocumentsRequest miraklDownloadShopsDocumentsRequest);

	void deleteShopDocument(MiraklDeleteShopDocumentRequest miraklDeleteShopDocumentRequest);

	void confirmSellerBillingCyclePayment(MiraklConfirmSellerBillingCyclePaymentRequest paymentConfirmationRequest);

	void reloadHttpConfiguration();

}
