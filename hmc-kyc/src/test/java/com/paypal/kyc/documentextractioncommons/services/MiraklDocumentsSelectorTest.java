package com.paypal.kyc.documentextractioncommons.services;

import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
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
class MiraklDocumentsSelectorTest {

	@InjectMocks
	private MiraklDocumentsSelector testObj;

	@Mock
	private Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new MiraklDocumentsSelector(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<KYCDocumentInfoModel, List<KYCDocumentModel>>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
