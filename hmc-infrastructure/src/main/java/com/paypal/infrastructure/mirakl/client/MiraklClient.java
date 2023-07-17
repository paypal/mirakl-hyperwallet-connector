package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoices;
import com.mirakl.client.mmp.domain.payment.MiraklTransactionLogs;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.domain.version.MiraklVersion;
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

import java.util.List;

public interface MiraklClient {

	MiraklVersion getVersion();

	MiraklShops getShops(MiraklGetShopsRequest request);

	MiraklUpdatedShops updateShops(MiraklUpdateShopsRequest miraklUpdateShopsRequest);

	List<MiraklFrontOperatorAdditionalField> getAdditionalFields(
			MiraklGetAdditionalFieldRequest miraklGetAdditionalFieldRequest);

	MiraklInvoices getInvoices(MiraklGetInvoicesRequest accountingDocumentRequest);

	@SuppressWarnings("java:S1874")
	MiraklTransactionLogs getTransactionLogs(MiraklGetTransactionLogsRequest miraklGetTransactionLogsRequest);

	MiraklDocumentsConfigurations getDocumentsConfiguration(
			MiraklGetDocumentsConfigurationRequest miraklGetDocumentsConfigurationRequest);

	List<MiraklShopDocument> getShopDocuments(MiraklGetShopDocumentsRequest getShopBusinessStakeholderDocumentsRequest);

	FileWrapper downloadShopsDocuments(MiraklDownloadShopsDocumentsRequest miraklDownloadShopsDocumentsRequest);

	void deleteShopDocument(MiraklDeleteShopDocumentRequest miraklDeleteShopDocumentRequest);

	void confirmAccountingDocumentPayment(MiraklConfirmAccountingDocumentPaymentRequest paymentConfirmationRequest);

	void reloadHttpConfiguration();

}
