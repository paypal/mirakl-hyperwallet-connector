package com.paypal.kyc.strategies.documents.flags.impl;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCUserDocumentFlagsFactoryTest {

	@InjectMocks
	private KYCUserDocumentFlagsFactory testObj;

	@Mock
	private Strategy<KYCUserDocumentFlagsNotificationBodyModel, Optional<Void>> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCUserDocumentFlagsFactory(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final var result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
