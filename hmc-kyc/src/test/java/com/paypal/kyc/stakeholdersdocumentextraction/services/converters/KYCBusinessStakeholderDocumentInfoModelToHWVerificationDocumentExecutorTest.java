package com.paypal.kyc.stakeholdersdocumentextraction.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.stakeholdersdocumentextraction.services.converters.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutorTest {

	private KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor testObj;

	@Mock
	private Strategy<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument>> result = testObj
				.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
