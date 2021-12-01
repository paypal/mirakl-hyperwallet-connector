package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl.KYCDocumentInfoToHWVerificationDocumentExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletSellerExtractServiceImplTest {

	private static final String USR_TOKEN_ONE = "usr-1234564780";

	private static final String USR_TOKEN_TWO = "usr-1234564789";

	private static final String CLIENT_USER_ID_ONE = "2001";

	private static final String CLIENT_USER_ID_TWO = "2002";

	private static final String EUROPE_HYPERWALLET_PROGRAM = "EUROPE";

	@InjectMocks
	private HyperwalletSellerExtractServiceImpl testObj;

	@Mock
	private Hyperwallet hyperwalletClientMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private HyperwalletVerificationDocument uploadDataOneMock, uploadDataTwoMock;

	@Mock
	private KYCDocumentInfoToHWVerificationDocumentExecutor kycDocumentInfoToHWVerificationDocumentExecutorMock;

	@Test
	void pushDocuments_shouldReturnAllKYCDocumentInfoModelWhenAllMiraklAPICallsAreSuccessful() {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOneStub = spy(
				KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_ONE)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_ONE)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, EUROPE_HYPERWALLET_PROGRAM)))
						.build());

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelTwoStub = Mockito
				.spy(KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_TWO)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_TWO)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, EUROPE_HYPERWALLET_PROGRAM)))
						.build());

		when(kycDocumentSellerInfoModelOneStub.areDocumentsFilled()).thenReturn(true);
		when(kycDocumentSellerInfoModelTwoStub.areDocumentsFilled()).thenReturn(true);
		when(kycDocumentInfoToHWVerificationDocumentExecutorMock.execute(kycDocumentSellerInfoModelOneStub))
				.thenReturn(List.of(uploadDataOneMock));
		when(kycDocumentInfoToHWVerificationDocumentExecutorMock.execute(kycDocumentSellerInfoModelTwoStub))
				.thenReturn(List.of(uploadDataTwoMock));

		when(hyperwalletSDKServiceMock.getHyperwalletInstance(Mockito.anyString())).thenReturn(hyperwalletClientMock);

		final List<KYCDocumentSellerInfoModel> result = testObj.pushProofOfIdentityAndBusinessSellerDocuments(
				List.of(kycDocumentSellerInfoModelOneStub, kycDocumentSellerInfoModelTwoStub));

		verify(hyperwalletClientMock).uploadUserDocuments(USR_TOKEN_ONE, List.of(uploadDataOneMock));
		verify(hyperwalletClientMock).uploadUserDocuments(USR_TOKEN_TWO, List.of(uploadDataTwoMock));

		assertThat(result).containsExactlyInAnyOrder(markInfoAsSentToHyperwallet(kycDocumentSellerInfoModelOneStub),
				markInfoAsSentToHyperwallet(kycDocumentSellerInfoModelTwoStub));
	}

	@Test
	void pushDocuments_shouldDoNothingForKYCDocumentsInfoWithDocumentsNonFilled() {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOneStub = Mockito
				.spy(KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_ONE)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_ONE)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, EUROPE_HYPERWALLET_PROGRAM)))
						.build());

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelTwoStub = Mockito
				.spy(KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_TWO)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_TWO)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, EUROPE_HYPERWALLET_PROGRAM)))
						.build());

		when(kycDocumentSellerInfoModelOneStub.areDocumentsFilled()).thenReturn(true);
		when(kycDocumentSellerInfoModelTwoStub.areDocumentsFilled()).thenReturn(false);
		when(kycDocumentInfoToHWVerificationDocumentExecutorMock.execute(kycDocumentSellerInfoModelOneStub))
				.thenReturn(List.of(uploadDataOneMock));

		when(hyperwalletSDKServiceMock.getHyperwalletInstance(Mockito.anyString())).thenReturn(hyperwalletClientMock);

		final List<KYCDocumentSellerInfoModel> result = testObj.pushProofOfIdentityAndBusinessSellerDocuments(
				List.of(kycDocumentSellerInfoModelOneStub, kycDocumentSellerInfoModelTwoStub));

		verify(hyperwalletClientMock).uploadUserDocuments(USR_TOKEN_ONE, List.of(uploadDataOneMock));
		verify(hyperwalletClientMock, never()).uploadUserDocuments(USR_TOKEN_TWO, List.of(uploadDataTwoMock));

		assertThat(result).containsExactly(markInfoAsSentToHyperwallet(kycDocumentSellerInfoModelOneStub));
	}

	@Test
	void pushDocuments_shouldReturnOnlySuccessfulKYCDocumentInfoModelAPICalls() {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOKStub = Mockito
				.spy(KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_ONE)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_ONE)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, EUROPE_HYPERWALLET_PROGRAM)))
						.build());

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelKOStub = Mockito
				.spy(KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_TWO)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_TWO)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, EUROPE_HYPERWALLET_PROGRAM)))
						.build());

		when(kycDocumentSellerInfoModelOKStub.areDocumentsFilled()).thenReturn(true);
		when(kycDocumentSellerInfoModelKOStub.areDocumentsFilled()).thenReturn(true);

		when(kycDocumentInfoToHWVerificationDocumentExecutorMock.execute(kycDocumentSellerInfoModelOKStub))
				.thenReturn(List.of(uploadDataOneMock));
		when(kycDocumentInfoToHWVerificationDocumentExecutorMock.execute(kycDocumentSellerInfoModelKOStub))
				.thenReturn(List.of(uploadDataTwoMock));

		when(hyperwalletSDKServiceMock.getHyperwalletInstance(Mockito.anyString())).thenReturn(hyperwalletClientMock);

		doReturn(new HyperwalletUser()).when(hyperwalletClientMock).uploadUserDocuments(USR_TOKEN_ONE,
				List.of(uploadDataOneMock));
		doThrow(new HyperwalletException("Something went wrong")).when(hyperwalletClientMock)
				.uploadUserDocuments(USR_TOKEN_TWO, List.of(uploadDataTwoMock));

		final List<KYCDocumentSellerInfoModel> result = testObj.pushProofOfIdentityAndBusinessSellerDocuments(
				List.of(kycDocumentSellerInfoModelOKStub, kycDocumentSellerInfoModelKOStub));

		verify(hyperwalletClientMock).uploadUserDocuments(USR_TOKEN_ONE, List.of(uploadDataOneMock));
		verify(hyperwalletClientMock).uploadUserDocuments(USR_TOKEN_TWO, List.of(uploadDataTwoMock));

		assertThat(result).containsExactly(markInfoAsSentToHyperwallet(kycDocumentSellerInfoModelOKStub));
	}

	@Test
	void pushDocuments_shouldSendEmailNotificationWhenHyperWalletExceptionIsThrown() {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelKOStub = Mockito
				.spy(KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID_TWO)
						.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
								KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN_TWO)))
						.hyperwalletProgram(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
								KYCConstants.HW_PROGRAM, EUROPE_HYPERWALLET_PROGRAM)))
						.build());

		when(kycDocumentSellerInfoModelKOStub.areDocumentsFilled()).thenReturn(true);
		when(kycDocumentInfoToHWVerificationDocumentExecutorMock.execute(kycDocumentSellerInfoModelKOStub))
				.thenReturn(List.of(uploadDataTwoMock));

		when(hyperwalletSDKServiceMock.getHyperwalletInstance(Mockito.anyString())).thenReturn(hyperwalletClientMock);

		HyperwalletException hyperwalletException = new HyperwalletException("Something went wrong");
		doThrow(hyperwalletException).when(hyperwalletClientMock).uploadUserDocuments(USR_TOKEN_TWO,
				List.of(uploadDataTwoMock));

		testObj.pushProofOfIdentityAndBusinessSellerDocuments(List.of(kycDocumentSellerInfoModelKOStub));

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
				String.format("Something went wrong pushing documents to Hyperwallet for shop Id [%s]%n%s",
						String.join(",", kycDocumentSellerInfoModelKOStub.getClientUserId()),
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	private KYCDocumentSellerInfoModel markInfoAsSentToHyperwallet(final KYCDocumentSellerInfoModel sellerInfo) {
		return sellerInfo.toBuilder().sentToHyperwallet(true).build();
	}

}
