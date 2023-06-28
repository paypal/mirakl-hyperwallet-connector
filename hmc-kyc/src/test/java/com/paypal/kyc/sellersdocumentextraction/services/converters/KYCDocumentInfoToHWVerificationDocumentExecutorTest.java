package com.paypal.kyc.sellersdocumentextraction.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.sellersdocumentextraction.services.converters.KYCDocumentInfoToHWVerificationDocumentExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCDocumentInfoToHWVerificationDocumentExecutorTest {

	@InjectMocks
	private KYCDocumentInfoToHWVerificationDocumentExecutor testObj;

	@Mock
	private Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCDocumentInfoToHWVerificationDocumentExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument>> result = testObj
				.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
