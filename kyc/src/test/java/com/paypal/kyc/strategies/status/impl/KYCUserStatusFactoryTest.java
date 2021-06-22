package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
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
class KYCUserStatusFactoryTest {

	@InjectMocks
	private KYCUserStatusFactory testObj;

	@Mock
	private Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCUserStatusFactory(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final var result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
