package com.paypal.kyc.strategies.documents.files;

import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.request.shop.document.MiraklDownloadShopsDocumentsRequest;
import com.paypal.kyc.model.*;
import com.paypal.kyc.strategies.documents.files.mirakl.AbstractMiraklSelectedDocumentsStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractMiraklSelectedDocumentsStrategyTest {

	private static final String MIRAKL_SHOP_ID = "2001";

	@InjectMocks
	private MyMiraklSelectedDocumentsStrategy testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklApiClientMock;

	@Mock
	private FileWrapper documentIdentityCardFrontFileWrapperMock, documentIdentityCardBackFileWrapperMock;

	@Mock
	private File fileFrontIdentityCardMock, fileBackIdentityCardMock;

	@Test
	void execute_shouldReturnOnlyFilesDefinedOnGetMiraklFieldNames() {
		final MiraklShopDocument miraklShopDocumentIdentityCardFront = new MiraklShopDocument();
		miraklShopDocumentIdentityCardFront.setId("proofOfIdentityFront");
		miraklShopDocumentIdentityCardFront.setTypeCode("field1");
		final MiraklShopDocument miraklShopDocumentIdentityCardBack = new MiraklShopDocument();
		miraklShopDocumentIdentityCardBack.setId("proofOfIdentityBack");
		miraklShopDocumentIdentityCardBack.setTypeCode("field2");
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD,
						KYCProofOfIdentityEnum.GOVERNMENT_ID.name())))
				.miraklShopDocuments(List.of(miraklShopDocumentIdentityCardFront, miraklShopDocumentIdentityCardBack))
				.build();

		final MiraklDownloadShopsDocumentsRequest downloadShopsDocumentFrontRequest = new MiraklDownloadShopsDocumentsRequest();
		downloadShopsDocumentFrontRequest.setDocumentIds(List.of("proofOfIdentityFront"));

		final MiraklDownloadShopsDocumentsRequest downloadShopsDocumentBackRequest = new MiraklDownloadShopsDocumentsRequest();
		downloadShopsDocumentBackRequest.setDocumentIds(List.of("proofOfIdentityBack"));

		when(miraklApiClientMock.downloadShopsDocuments(downloadShopsDocumentFrontRequest))
				.thenReturn(documentIdentityCardFrontFileWrapperMock);
		when(documentIdentityCardFrontFileWrapperMock.getFile()).thenReturn(fileFrontIdentityCardMock);
		when(miraklApiClientMock.downloadShopsDocuments(downloadShopsDocumentBackRequest))
				.thenReturn(documentIdentityCardBackFileWrapperMock);
		when(documentIdentityCardBackFileWrapperMock.getFile()).thenReturn(fileBackIdentityCardMock);

		final List<KYCDocumentModel> result = testObj.execute(kycDocumentSellerInfoModel);

		verify(miraklApiClientMock).downloadShopsDocuments(downloadShopsDocumentFrontRequest);
		verify(miraklApiClientMock).downloadShopsDocuments(downloadShopsDocumentBackRequest);
		final KYCDocumentModel kycFront = KYCDocumentModel.builder().documentFieldName("field1")
				.file(fileFrontIdentityCardMock).build();
		final KYCDocumentModel kycBack = KYCDocumentModel.builder().documentFieldName("field2")
				.file(fileBackIdentityCardMock).build();

		assertThat(result).containsExactlyInAnyOrder(kycFront, kycBack);
	}

	private static class MyMiraklSelectedDocumentsStrategy extends AbstractMiraklSelectedDocumentsStrategy {

		protected MyMiraklSelectedDocumentsStrategy(final MiraklMarketplacePlatformOperatorApiClient miraklApiClient) {
			super(miraklApiClient);
		}

		@Override
		protected List<String> getMiraklFieldNames(final KYCDocumentInfoModel source) {
			return List.of("field1", "field2", "field3");
		}

		@Override
		public boolean isApplicable(final KYCDocumentInfoModel source) {
			return true;
		}

	}

}
