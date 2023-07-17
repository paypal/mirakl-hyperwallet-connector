package com.paypal.kyc.incomingnotifications.services;

import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.incomingnotifications.services.KYCUserDocumentFlagsExecutor;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCUserDocumentFlagsExecutorTest {

	@InjectMocks
	private KYCUserDocumentFlagsExecutor testObj;

	@Mock
	private Strategy<KYCUserDocumentFlagsNotificationBodyModel, Void> strategyMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCUserDocumentFlagsExecutor(Set.of(strategyMock));
	}

	@Test
	void getStrategies_shouldReturnConverterStrategyMock() {
		final Set<Strategy<KYCUserDocumentFlagsNotificationBodyModel, Void>> result = testObj.getStrategies();

		assertThat(result).containsExactly(strategyMock);
	}

}
