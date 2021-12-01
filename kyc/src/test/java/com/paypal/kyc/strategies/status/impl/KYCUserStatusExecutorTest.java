package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCUserStatusExecutorTest {

	@InjectMocks
	private KYCUserStatusExecutor testObj;

	@Mock
	private Strategy<KYCUserStatusNotificationBodyModel, Void> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCUserStatusExecutor(Collections.singleton(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<KYCUserStatusNotificationBodyModel, Void>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
