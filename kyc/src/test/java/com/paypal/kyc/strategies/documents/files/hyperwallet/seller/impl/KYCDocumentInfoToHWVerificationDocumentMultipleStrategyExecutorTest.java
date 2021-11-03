package com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutorTest {

	@InjectMocks
	private KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor testObj;

	@Mock
	private Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {

		final var result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);

	}

}
