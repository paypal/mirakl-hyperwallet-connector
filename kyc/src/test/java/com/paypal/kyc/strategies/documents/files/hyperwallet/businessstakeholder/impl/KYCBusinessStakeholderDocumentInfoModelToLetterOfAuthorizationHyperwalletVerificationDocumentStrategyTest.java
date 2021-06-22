package com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentCategoryEnum;
import com.paypal.kyc.model.KYCDocumentModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCBusinessStakeholderDocumentInfoModelToLetterOfAuthorizationHyperwalletVerificationDocumentStrategyTest {

	private static final String AUTHORIZATION = "AUTHORIZATION";

	private static final String FAKE_ABSOLUTE_PATH = "/fakePath/fakeFile.txt";

	@InjectMocks
	private KYCBusinessStakeholderDocumentInfoModelToLetterOfAuthorizationHyperwalletVerificationDocumentStrategy testObj;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelMock;

	@Mock
	private KYCDocumentModel kycDocumentModelMock;

	@Mock
	private File fileMock;

	private static final String LETTER_OF_AUTHORIZATION_TYPE = "LETTER_OF_AUTHORIZATION";

	private final static String LETTER_OF_AUTHORIZATION_NAME = "letter_of_authorization_front";

	@Test
	void execute_shouldReturnLetterOfAuthorizationHyperwalletDocument_whenSourceHasLetterOfAuthorizationDocument() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.getLetterOfAuthorizationDocument())
				.thenReturn(List.of(kycDocumentModelMock));
		when(kycDocumentModelMock.getFile()).thenReturn(fileMock);
		when(kycDocumentModelMock.getFile().getAbsolutePath()).thenReturn(FAKE_ABSOLUTE_PATH);

		final HyperwalletVerificationDocument result = testObj.execute(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result.getType()).isEqualTo(LETTER_OF_AUTHORIZATION_TYPE);
		assertThat(result.getCategory()).hasToString(AUTHORIZATION);
		assertThat(result.getUploadFiles().keySet()).containsExactly(LETTER_OF_AUTHORIZATION_NAME);
		assertThat(result.getUploadFiles().values()).containsExactly(FAKE_ABSOLUTE_PATH);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenRequiredLetterOfAuthorizationIsTrueAndContainsBusinessContactIsTrueAndLetterOfAuthorizationDocumentIsNotEmpty() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.isRequiresLetterOfAuthorization()).thenReturn(Boolean.TRUE);
		when(kycDocumentBusinessStakeHolderInfoModelMock.isContact()).thenReturn(Boolean.TRUE);
		when(kycDocumentBusinessStakeHolderInfoModelMock.getLetterOfAuthorizationDocument())
				.thenReturn(List.of(kycDocumentModelMock));

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenRequiredLetterOfAuthorizationIsFalseAndContainsBusinessContactIsTrueAndLetterOfAuthorizationDocumentIsNotEmpty() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.isRequiresLetterOfAuthorization()).thenReturn(Boolean.FALSE);

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenRequiredLetterOfAuthorizationIsTrueAndContainsBusinessContactIsFalseAndLetterOfAuthorizationDocumentIsNotEmpty() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.isRequiresLetterOfAuthorization()).thenReturn(Boolean.TRUE);
		when(kycDocumentBusinessStakeHolderInfoModelMock.isContact()).thenReturn(Boolean.FALSE);

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenRequiredLetterOfAuthorizationIsTrueAndContainsBusinessContactIsTrueAndLetterOfAuthorizationDocumentIsEmpty() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.isRequiresLetterOfAuthorization()).thenReturn(Boolean.TRUE);
		when(kycDocumentBusinessStakeHolderInfoModelMock.isContact()).thenReturn(Boolean.TRUE);
		when(kycDocumentBusinessStakeHolderInfoModelMock.getLetterOfAuthorizationDocument()).thenReturn(List.of());

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isFalse();
	}

}
