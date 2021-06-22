package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklLetterOfAuthorizationBusinessStakeholderStrategyTest {

	@InjectMocks
	private MiraklLetterOfAuthorizationBusinessStakeholderStrategy testObj;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelMock;

	public static final String PROOF_OF_AUTHORIZATION = "hw-bsh-letter-authorization";

	@Test
	void isApplicable_shouldReturnTrueWhensourceIsInstanceOfKYCDocumentBusinessStakeHolderInfoModelAndIsContactIsTrueAndRequiresLetterOfAuthorizationIsTrue() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.isContact()).thenReturn(Boolean.TRUE);
		when(kycDocumentBusinessStakeHolderInfoModelMock.isRequiresLetterOfAuthorization()).thenReturn(Boolean.TRUE);

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhensourceIsNotInstanceOfKYCDocumentBusinessStakeHolderInfoModel() {

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhensourceIsInstanceOfKYCDocumentBusinessStakeHolderInfoModelAndIsContactIsFalseAndRequiresLetterOfAuthorizationIsTrue() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.isContact()).thenReturn(Boolean.FALSE);

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhensourceIsInstanceOfKYCDocumentBusinessStakeHolderInfoModelAndIsContactIsTrueAndRequiresLetterOfAuthorizationIsFalse() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.isContact()).thenReturn(Boolean.TRUE);
		when(kycDocumentBusinessStakeHolderInfoModelMock.isRequiresLetterOfAuthorization()).thenReturn(Boolean.FALSE);

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void getMiraklFieldNames_shouldReturnListOfProofOfAuthorization() {

		final List<String> result = testObj.getMiraklFieldNames(kycDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).containsExactly(PROOF_OF_AUTHORIZATION);
	}

}
