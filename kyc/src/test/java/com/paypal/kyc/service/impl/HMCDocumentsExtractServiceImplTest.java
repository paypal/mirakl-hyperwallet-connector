package com.paypal.kyc.service.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HMCDocumentsExtractServiceImplTest {

	@InjectMocks
	private HMCDocumentsExtractServiceImpl testObj;

	@RegisterExtension
	LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
			.recordForType(HMCDocumentsExtractServiceImpl.class);

	@Mock
	private File fileOneMock, fileTwoMock;

	@Test
	void cleanUpFiles_shouldRemoveFilesReceivedAsParameter() {

		final KYCDocumentModel kycDocumentModelOne = KYCDocumentModel.builder().file(fileOneMock).build();
		final KYCDocumentModel kycDocumentModelTwo = KYCDocumentModel.builder().file(fileTwoMock).build();
		final KYCDocumentSellerInfoModel kycDocumentOne = KYCDocumentSellerInfoModel.builder().clientUserId("2000")
				.documents(List.of(kycDocumentModelOne, kycDocumentModelTwo)).build();
		final List<KYCDocumentSellerInfoModel> successfullyPushedDocumentsList = List.of(kycDocumentOne);

		testObj.cleanUpDocumentsFiles(successfullyPushedDocumentsList);

		verify(fileOneMock).delete();
		verify(fileTwoMock).delete();
		assertThat(logTrackerStub.contains("Cleaning up done successfully!")).isTrue();
	}

	@Test
	void cleanUpFiles_shouldNotDoAnythingWhenNullParameterIsReceived() {

		testObj.cleanUpDocumentsFiles(null);
		assertThat(logTrackerStub.contains("Cleaning up done successfully!")).isTrue();
	}

}
