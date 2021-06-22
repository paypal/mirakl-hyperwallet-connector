package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklKYCSelectionDocumentMultipleStrategyFactoryTest {

	@InjectMocks
	private MiraklKYCSelectionDocumentMultipleStrategyFactory testObj;

	@Mock
	private Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new MiraklKYCSelectionDocumentMultipleStrategyFactory(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {

		final var result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);

	}

}
