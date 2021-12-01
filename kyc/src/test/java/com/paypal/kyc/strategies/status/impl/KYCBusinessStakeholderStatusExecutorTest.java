package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
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
class KYCBusinessStakeholderStatusExecutorTest {

	@InjectMocks
	private KYCBusinessStakeholderStatusExecutor testObj;

	@Mock
	private Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Void> kycBusinessStakeholderStatusNotificationBodyModelOptionalStrategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCBusinessStakeholderStatusExecutor(
				Collections.singleton(kycBusinessStakeholderStatusNotificationBodyModelOptionalStrategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Void>> result = testObj.getStrategies();

		assertThat(result).containsExactly(kycBusinessStakeholderStatusNotificationBodyModelOptionalStrategyMock);
	}

}
