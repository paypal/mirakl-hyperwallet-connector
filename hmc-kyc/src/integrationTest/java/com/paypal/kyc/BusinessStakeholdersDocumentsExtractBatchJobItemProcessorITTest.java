package com.paypal.kyc;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.kyc.documentextractioncommons.support.AbstractDocumentsBatchJobItemProcessor;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItem;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItemProcessor;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessStakeholdersDocumentsExtractBatchJobItemProcessorITTest extends AbstractMockEnabledIntegrationTest {

	@Autowired
	private BusinessStakeholdersDocumentsExtractBatchJobItemProcessor testObj;

	@RegisterExtension
	private final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
			.recordForType(AbstractDocumentsBatchJobItemProcessor.class);

	@Test
	void processItem_shouldPushFlagNotifyAndCleanDocuments_whenDocumentsCanBePushed() throws IOException {
		final File hello = Files.createTempFile("hello", ".file").toFile();
		final String shopId = "1234";
		final String userToken = "userToken";
		final String kycToken = "token";
		final boolean requiresKYC = false;

		final BusinessStakeholdersDocumentsExtractBatchJobItem item = createValidItem(hello, requiresKYC, shopId,
				kycToken, userToken);

		businessStakeHoldersEndpointMock.uploadDocument(userToken, kycToken);
		miraklShopsEndpointMock.updateDocument(shopId);
		usersEndpointMock.updatedUser(userToken);

		testObj.processItem(null, item);

		businessStakeHoldersEndpointMock.verifyUploadDocument(userToken, kycToken);
		miraklShopsEndpointMock.verifyUpdateDocument(Long.parseLong(shopId));
		usersEndpointMock.verifyUpdatedUser(userToken);

		Assertions.assertFalse(hello.exists());
		assertThat(logTrackerStub.contains("File selected to be deleted [%s]".formatted(hello.getAbsolutePath())))
				.isTrue();

	}

	@Test
	void processItem_shouldNotNotifyAndCleanDocuments_whenDocumentsCanNotBePushed() throws IOException {

		final File hello = Files.createTempFile("hello", ".file").toFile();
		final boolean requiresKYC = true;
		final BusinessStakeholdersDocumentsExtractBatchJobItem item = createValidItem(hello, requiresKYC, "1234",
				"token", "userToken");
		testObj.processItem(null, item);

		Assertions.assertFalse(hello.exists());
		assertThat(logTrackerStub.contains("File selected to be deleted [%s]".formatted(hello.getAbsolutePath())))
				.isTrue();
	}

	@Test
	void processItem_cleanUpFile_whenSomeErrorHappens() throws IOException {

		final File hello = Files.createTempFile("hello", ".file").toFile();
		final boolean requiresKYC = false;
		final String withouUserToken = null;
		final BusinessStakeholdersDocumentsExtractBatchJobItem item = createValidItem(hello, requiresKYC, "1234",
				"token", withouUserToken);

		assertThatThrownBy(() -> testObj.processItem(null, item)).isInstanceOf(HMCHyperwalletAPIException.class);

		Assertions.assertFalse(hello.exists());
		assertThat(logTrackerStub.contains("File selected to be deleted [%s]".formatted(hello.getAbsolutePath())))
				.isTrue();
	}

	private static BusinessStakeholdersDocumentsExtractBatchJobItem createValidItem(final File file,
			final boolean requiresKYC, final String shopId, final String kycToken, final String userToken) {
		final KYCDocumentModel document = KYCDocumentModel.builder().file(file)
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION).build();
		final KYCDocumentBusinessStakeHolderInfoModel kycDocument = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.hyperwalletProgram("DEFAULT").requiresKYC(requiresKYC).requiresLetterOfAuthorization(true)
				.contact(true).documents(List.of(document)).clientUserId(shopId).token(kycToken).userToken(userToken)
				.build();

		return new BusinessStakeholdersDocumentsExtractBatchJobItem(kycDocument);
	}

}
