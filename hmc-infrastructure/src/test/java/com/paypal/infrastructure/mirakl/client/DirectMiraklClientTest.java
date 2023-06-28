package com.paypal.infrastructure.mirakl.client;

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
import com.mirakl.client.mmp.request.payment.MiraklGetTransactionLogsRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklDeleteShopDocumentRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklDownloadShopsDocumentsRequest;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mirakl.client.filter.IgnoredShopsFilter;
import com.paypal.infrastructure.mirakl.configuration.MiraklApiClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectMiraklClientTest {

	private DirectMiraklClient testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private IgnoredShopsFilter ignoredShopsFilterMock;

	@BeforeEach
	void setUp() {
		final MiraklApiClientConfig config = new MiraklApiClientConfig();
		config.setOperatorApiKey("OPERATOR-KEY");
		config.setEnvironment("environment");
		testObj = Mockito.spy(new DirectMiraklClient(config, ignoredShopsFilterMock));
		ReflectionTestUtils.setField(testObj, "miraklMarketplacePlatformOperatorApiClient",
				miraklMarketplacePlatformOperatorApiClientMock);
	}

	@Test
	void getVersion_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklVersion miraklVersion = mock(MiraklVersion.class);
		when(miraklMarketplacePlatformOperatorApiClientMock.getVersion()).thenReturn(miraklVersion);

		// when
		final MiraklVersion result = testObj.getVersion();

		// then
		assertThat(result).isEqualTo(miraklVersion);
		verify(miraklMarketplacePlatformOperatorApiClientMock).getVersion();
	}

	@Test
	void getShops_shouldCallSdkClient_andFilterShops() {
		// given
		final MiraklShops unfilteredMiraklShops = mock(MiraklShops.class);
		final MiraklShops filteredMiraklShops = mock(MiraklShops.class);
		when(ignoredShopsFilterMock.filterIgnoredShops(unfilteredMiraklShops)).thenReturn(filteredMiraklShops);
		final MiraklGetShopsRequest miraklGetShopsRequest = mock(MiraklGetShopsRequest.class);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequest))
				.thenReturn(unfilteredMiraklShops);

		// when
		final MiraklShops result = testObj.getShops(miraklGetShopsRequest);

		// then
		assertThat(result).isEqualTo(filteredMiraklShops);
		verify(miraklMarketplacePlatformOperatorApiClientMock).getShops(miraklGetShopsRequest);
	}

	@Test
	void updateShops_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklUpdateShopsRequest miraklUpdateShopsRequest = mock(MiraklUpdateShopsRequest.class);
		final MiraklUpdatedShops miraklUpdatedShops = mock(MiraklUpdatedShops.class);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(miraklUpdateShopsRequest))
				.thenReturn(miraklUpdatedShops);

		// when
		final MiraklUpdatedShops result = testObj.updateShops(miraklUpdateShopsRequest);

		// then
		assertThat(result).isEqualTo(miraklUpdatedShops);
		verify(miraklMarketplacePlatformOperatorApiClientMock).updateShops(miraklUpdateShopsRequest);
	}

	@Test
	void getAdditionalFields_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklGetAdditionalFieldRequest miraklGetAdditionalFieldRequest = mock(
				MiraklGetAdditionalFieldRequest.class);
		final List<MiraklFrontOperatorAdditionalField> additionalFields = List
				.of(mock(MiraklFrontOperatorAdditionalField.class));
		when(miraklMarketplacePlatformOperatorApiClientMock.getAdditionalFields(miraklGetAdditionalFieldRequest))
				.thenReturn(additionalFields);

		// when
		final List<MiraklFrontOperatorAdditionalField> result = testObj
				.getAdditionalFields(miraklGetAdditionalFieldRequest);

		// then
		assertThat(result).isEqualTo(additionalFields);
		verify(miraklMarketplacePlatformOperatorApiClientMock).getAdditionalFields(miraklGetAdditionalFieldRequest);
	}

	@Test
	void getInvoices_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = mock(MiraklGetInvoicesRequest.class);
		final MiraklInvoices miraklInvoices = mock(MiraklInvoices.class);
		when(miraklMarketplacePlatformOperatorApiClientMock.getInvoices(miraklGetInvoicesRequest))
				.thenReturn(miraklInvoices);

		// when
		final MiraklInvoices result = testObj.getInvoices(miraklGetInvoicesRequest);

		// then
		assertThat(result).isEqualTo(miraklInvoices);
		verify(miraklMarketplacePlatformOperatorApiClientMock).getInvoices(miraklGetInvoicesRequest);
	}

	@SuppressWarnings("java:S1874")
	@Test
	void getTransactionLogs_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklGetTransactionLogsRequest miraklGetTransactionLogsRequest = mock(
				MiraklGetTransactionLogsRequest.class);
		final MiraklTransactionLogs miraklTransactionLogs = mock(MiraklTransactionLogs.class);
		when(miraklMarketplacePlatformOperatorApiClientMock.getTransactionLogs(miraklGetTransactionLogsRequest))
				.thenReturn(miraklTransactionLogs);

		// when
		final MiraklTransactionLogs result = testObj.getTransactionLogs(miraklGetTransactionLogsRequest);

		// then
		assertThat(result).isEqualTo(miraklTransactionLogs);
		verify(miraklMarketplacePlatformOperatorApiClientMock).getTransactionLogs(miraklGetTransactionLogsRequest);
	}

	@Test
	void getDocumentsConfiguration_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklGetDocumentsConfigurationRequest miraklGetDocumentsConfigurationRequest = mock(
				MiraklGetDocumentsConfigurationRequest.class);
		final MiraklDocumentsConfigurations miraklDocumentsConfiguration = mock(MiraklDocumentsConfigurations.class);
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getDocumentsConfiguration(miraklGetDocumentsConfigurationRequest))
						.thenReturn(miraklDocumentsConfiguration);

		// when
		final MiraklDocumentsConfigurations result = testObj
				.getDocumentsConfiguration(miraklGetDocumentsConfigurationRequest);

		// then
		assertThat(result).isEqualTo(miraklDocumentsConfiguration);
		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.getDocumentsConfiguration(miraklGetDocumentsConfigurationRequest);
	}

	@Test
	void getShopDocuments_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklGetShopDocumentsRequest miraklGetShopDocumentsRequest = mock(MiraklGetShopDocumentsRequest.class);
		final List<MiraklShopDocument> miraklShopDocuments = List.of(mock(MiraklShopDocument.class));
		when(miraklMarketplacePlatformOperatorApiClientMock.getShopDocuments(miraklGetShopDocumentsRequest))
				.thenReturn(miraklShopDocuments);

		// when
		final List<MiraklShopDocument> result = testObj.getShopDocuments(miraklGetShopDocumentsRequest);

		// then
		assertThat(result).isEqualTo(miraklShopDocuments);
		verify(miraklMarketplacePlatformOperatorApiClientMock).getShopDocuments(miraklGetShopDocumentsRequest);
	}

	@Test
	void downloadShopDocumentes_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklDownloadShopsDocumentsRequest miraklDownloadShopsDocumentsRequest = mock(
				MiraklDownloadShopsDocumentsRequest.class);
		final FileWrapper fileWrapper = mock(FileWrapper.class);
		when(miraklMarketplacePlatformOperatorApiClientMock.downloadShopsDocuments(miraklDownloadShopsDocumentsRequest))
				.thenReturn(fileWrapper);

		// when
		final FileWrapper result = testObj.downloadShopsDocuments(miraklDownloadShopsDocumentsRequest);

		// then
		assertThat(result).isEqualTo(fileWrapper);
		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.downloadShopsDocuments(miraklDownloadShopsDocumentsRequest);
	}

	@Test
	void deleteShopDocument_shouldDelegateOnMiraklSdkClient() {
		// given
		final MiraklDeleteShopDocumentRequest miraklDeleteShopDocumentRequest = mock(
				MiraklDeleteShopDocumentRequest.class);

		// when
		testObj.deleteShopDocument(miraklDeleteShopDocumentRequest);

		// then
		verify(miraklMarketplacePlatformOperatorApiClientMock).deleteShopDocument(miraklDeleteShopDocumentRequest);
	}

}
