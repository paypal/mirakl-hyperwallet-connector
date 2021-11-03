package com.paypal.kyc.strategies.status.impl;

import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCBusinessStakeholderStatusExecutorTest {

	@InjectMocks
	private KYCBusinessStakeholderStatusExecutor testObj;

	@Mock
	private Strategy<KYCBusinessStakeholderStatusNotificationBodyModel, Optional<Void>> kycBusinessStakeholderStatusNotificationBodyModelOptionalStrategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCBusinessStakeholderStatusExecutor(
				Set.of(kycBusinessStakeholderStatusNotificationBodyModelOptionalStrategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final var result = testObj.getStrategies();

		assertThat(result).containsExactly(kycBusinessStakeholderStatusNotificationBodyModelOptionalStrategyMock);
	}

}
